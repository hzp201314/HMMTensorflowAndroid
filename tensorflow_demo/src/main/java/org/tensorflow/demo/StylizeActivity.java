/*
 * Copyright 2017 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Sample activity that stylizes the camera preview according to "A Learned Representation For
 * Artistic Style" (https://arxiv.org/abs/1610.07629)
 */
@Route(path = "/tensorflow/activity/StylizeActivity")
public class StylizeActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";
    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";
    private static final int NUM_STYLES = 26;

    private static final boolean SAVE_PREVIEW_BITMAP = false;

    // Whether to actively manipulate non-selected sliders so that sum of activations always appears
    // to be 1.0. The actual style input tensor will be normalized to sum to 1.0 regardless.
    private static final boolean NORMALIZE_SLIDERS = true;

    private static final float TEXT_SIZE_DIP = 12;

    private static final boolean DEBUG_MODEL = false;

    private static final int[] SIZES = {128, 192, 256, 384, 512, 720};

    private static final Size DESIRED_PREVIEW_SIZE = new Size( 1280, 720 );

    // Start at a medium size, but let the user step up through smaller sizes so they don't get
    // immediately stuck processing a large image.
    private int desiredSizeIndex = -1;
    private int desiredSize = 720;
    private int initializedSize = 0;

    private Integer sensorOrientation;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private final float[] styleVals = new float[NUM_STYLES];
    private int[] intValues;
    private float[] floatValues;

    private int frameNum = 0;

    private Bitmap textureCopyBitmap;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private BorderedText borderedText;

    private TensorFlowInferenceInterface inferenceInterface;

    private int lastOtherStyle = 1;

    private boolean allZero = false;

    private ImageGridAdapter adapter;
    private GridView grid;

    /**
     * 触摸监听器
     */
    private final OnTouchListener gridTouchAdapter =
            new OnTouchListener() {
                ImageSlider slider = null;

                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    switch (event.getActionMasked()) {
                        //当屏幕检测到第一个触点按下之后就会触发到这个事件
                        case MotionEvent.ACTION_DOWN:
                            //26个styles,获取高度
                            for (int i = 0; i < NUM_STYLES; ++i) {
                                final ImageSlider child = adapter.items[i];
                                final Rect rect = new Rect();
                                child.getHitRect( rect );
                                if (rect.contains( (int) event.getX(), (int) event.getY() )) {
                                    slider = child;
                                    slider.setHilighted( true );
                                }
                            }
                            break;
                        //当触点在屏幕上移动时触发，触点在屏幕上停留也是会触发的，主要是由于它的灵敏度很高，
                        //而我们的手指又不可能完全静止（即使我们感觉不到移动，但其实我们的手指也在不停地抖动）。
                        case MotionEvent.ACTION_MOVE:
                            if (slider != null) {
                                final Rect rect = new Rect();
                                slider.getHitRect( rect );

                                final float newSliderVal =
                                        (float)
                                                Math.min(
                                                        1.0,
                                                        Math.max(
                                                                0.0, 1.0 - (event.getY() - slider.getTop()) / slider.getHeight() ) );

                                setStyle( slider, newSliderVal );//设置风格图片的权重值
                            }
                            break;
                        //当触点松开时被触发。
                        case MotionEvent.ACTION_UP:
                            if (slider != null) {
                                slider.setHilighted( false );
                                slider = null;
                            }
                            break;

                        default: // fall out

                    }
                    return true;
                }
            };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_stylize;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    /**
     * 从资源获取位图
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return 返回位图
     */
    public static Bitmap getBitmapFromAsset(final Context context, final String filePath) {
        final AssetManager assetManager = context.getAssets();

        Bitmap bitmap = null;
        try {
            final InputStream inputStream = assetManager.open( filePath );
            bitmap = BitmapFactory.decodeStream( inputStream );
        } catch (final IOException e) {
            LOGGER.e( "Error opening bitmap!", e );
        }

        return bitmap;
    }

    /**
     * 图像滑块
     */
    private class ImageSlider extends ImageView {
        private float value = 0.0f;
        private boolean hilighted = false;

        private final Paint boxPaint;
        private final Paint linePaint;

        public ImageSlider(final Context context) {
            super( context );
            value = 0.0f;

            //box
            boxPaint = new Paint();
            boxPaint.setColor( Color.BLACK );
            boxPaint.setAlpha( 128 );

            //line
            linePaint = new Paint();
            linePaint.setColor( Color.WHITE );
            linePaint.setStrokeWidth( 10.0f );
            linePaint.setStyle( Style.STROKE );
        }

        /**
         * 画box和line
         *
         * @param canvas
         */
        @Override
        public void onDraw(final Canvas canvas) {
            super.onDraw( canvas );
            final float y = (1.0f - value) * canvas.getHeight();

            // If all sliders are zero, don't bother shading anything.
            if (!allZero) {
                canvas.drawRect( 0, 0, canvas.getWidth(), y, boxPaint );
            }

            if (value > 0.0f) {
                canvas.drawLine( 0, y, canvas.getWidth(), y, linePaint );
            }

            if (hilighted) {
                canvas.drawRect( 0, 0, getWidth(), getHeight(), linePaint );
            }
        }

        @Override
        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
            setMeasuredDimension( getMeasuredWidth(), getMeasuredWidth() );
        }

        public void setValue(final float value) {
            this.value = value;
            postInvalidate();
        }

        public void setHilighted(final boolean highlighted) {
            this.hilighted = highlighted;
            this.postInvalidate();
        }
    }

    /**
     * 图片网格适配器
     */
    private class ImageGridAdapter extends BaseAdapter {
        final ImageSlider[] items = new ImageSlider[NUM_STYLES];
        final ArrayList<Button> buttons = new ArrayList<>();

        {
            //大小按钮
            final Button sizeButton =
                    new Button( StylizeActivity.this ) {
                        @Override
                        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
                            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
                            setMeasuredDimension( getMeasuredWidth(), getMeasuredWidth() );
                        }
                    };
            sizeButton.setText( "" + desiredSize );//设置大小按钮默认为256
            sizeButton.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            desiredSizeIndex = (desiredSizeIndex + 1) % SIZES.length;//取余数，循环设置desiredSizeIndex为0~5
                            desiredSize = SIZES[desiredSizeIndex];//设置大小
                            sizeButton.setText( "" + desiredSize );//设置按钮值
                            sizeButton.postInvalidate();
                        }
                    } );

            //保存按钮
            final Button saveButton =
                    new Button( StylizeActivity.this ) {
                        @Override
                        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
                            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
                            setMeasuredDimension( getMeasuredWidth(), getMeasuredWidth() );
                        }
                    };
            saveButton.setText( "save" );
            saveButton.setTextSize( 12 );

            saveButton.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (textureCopyBitmap != null) {
                                //保存图片
                                // TODO(andrewharp): Save as jpeg with guaranteed unique filename.
                                ImageUtils.saveBitmap( textureCopyBitmap, "stylized" + frameNum + ".png" );
                                Toast.makeText(
                                        StylizeActivity.this,
                                        "Saved image to: /sdcard/tensorflow/" + "stylized" + frameNum + ".png",
                                        Toast.LENGTH_LONG )
                                        .show();
                            }
                        }
                    } );

            buttons.add( sizeButton );//添加大小按钮
            buttons.add( saveButton );//添加保存按钮

            //将26张风格图片放入items中
            for (int i = 0; i < NUM_STYLES; ++i) {
                LOGGER.v( "Creating item %d", i );

                if (items[i] == null) {
                    final ImageSlider slider = new ImageSlider( StylizeActivity.this );
                    //获取资源图片
                    final Bitmap bm =
                            getBitmapFromAsset( StylizeActivity.this, "thumbnails/style" + i + ".jpg" );
                    slider.setImageBitmap( bm );

                    items[i] = slider;
                }
            }
        }

        @Override
        public int getCount() {
            return buttons.size() + NUM_STYLES;//2+26=28
        }

        @Override
        public Object getItem(final int position) {//获取控件
            if (position < buttons.size()) {//position<2
                return buttons.get( position );//返回buttons
            } else {
                return items[position - buttons.size()];//返回items
            }
        }

        @Override
        public long getItemId(final int position) {
            return getItem( position ).hashCode();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            if (convertView != null) {
                return convertView;
            }
            return (View) getItem( position );
        }
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        //绘图
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics() );
        borderedText = new BorderedText( textSizePx );
        borderedText.setTypeface( Typeface.MONOSPACE );

        //tf接口传入Assets和模型文件名
        inferenceInterface = new TensorFlowInferenceInterface( getAssets(), MODEL_FILE );

        //图像宽高
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        final Display display = getWindowManager().getDefaultDisplay();
        final int screenOrientation = display.getRotation();

        LOGGER.i( "Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation );

        //方向
        sensorOrientation = rotation + screenOrientation;

        //调试视图
        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        renderDebug( canvas );
                    }
                } );

        //新建ImageGridAdapter适配器
        adapter = new ImageGridAdapter();
        grid = (GridView) findViewById( R.id.grid_layout );
        grid.setAdapter( adapter );
        grid.setOnTouchListener( gridTouchAdapter );//设置监听器

        // Change UI on Android TV
        UiModeManager uiModeManager = (UiModeManager) getSystemService( UI_MODE_SERVICE );
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
            int styleSelectorHeight = displayMetrics.heightPixels;
            int styleSelectorWidth = displayMetrics.widthPixels - styleSelectorHeight;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( styleSelectorWidth, ViewGroup.LayoutParams.MATCH_PARENT );

            // Calculate number of style in a row, so all the style can show up without scrolling
            int numOfStylePerRow = 3;
            while (styleSelectorWidth / numOfStylePerRow * Math.ceil( (float) (adapter.getCount() - 2) / numOfStylePerRow ) > styleSelectorHeight) {
                numOfStylePerRow++;
            }
            grid.setNumColumns( numOfStylePerRow );
            layoutParams.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
            grid.setLayoutParams( layoutParams );
            adapter.buttons.clear();
        }

        setStyle( adapter.items[0], 1.0f );//默认设置第一张风格图片高度为1.0f
    }

    /**
     * 设置风格图片权重高度
     *
     * @param slider 滑块
     * @param value  长度值，高度值，权重值
     */
    private void setStyle(final ImageSlider slider, final float value) {
        slider.setValue( value );//设置高度（权重）

        if (NORMALIZE_SLIDERS) {
            // Slider vals correspond directly to the input tensor vals, and normalization is visually
            // maintained by remanipulating non-selected sliders.
            float otherSum = 0.0f;

            for (int i = 0; i < NUM_STYLES; ++i) {
                if (adapter.items[i] != slider) {//不是当前slider的其他slider
                    otherSum += adapter.items[i].value;//计算其他slider的总权重
                }
            }

            if (otherSum > 0.0) {//如果其他风格图片的权重值不为0
                float highestOtherVal = 0;//最高的权重
                final float factor = otherSum > 0.0f ? (1.0f - value) / otherSum : 0.0f;//算比例
                for (int i = 0; i < NUM_STYLES; ++i) {
                    final ImageSlider child = adapter.items[i];
                    if (child == slider) {
                        continue;
                    }
                    final float newVal = child.value * factor;//计算新的权重
                    child.setValue( newVal > 0.01f ? newVal : 0.0f );//权重大于0.01f就赋予新的权重否则权重为0

                    //找出非当前风格图片的最高的权重
                    if (child.value > highestOtherVal) {
                        lastOtherStyle = i;
                        highestOtherVal = child.value;
                    }
                }
            } else {//如果其他风格图片的权重值全为0
//        其他的一切都是0，所以只要选择一个合适的滑块，在选定的滑块下降时向上推即可，选定滑块上升时向下推。
//        这个合适的滑块是最后一次修改的风格图片权重的那个滑块
                // Everything else is 0, so just pick a suitable slider to push up when the
                // selected one goes down.
                if (adapter.items[lastOtherStyle] == slider) {//如果最后一次修改权重的滑块是当前滑块就＋1，避免bug，保证总权重为1.0f
                    lastOtherStyle = (lastOtherStyle + 1) % NUM_STYLES;
                }
                adapter.items[lastOtherStyle].setValue( 1.0f - value );
            }
        }

        final boolean lastAllZero = allZero;
        float sum = 0.0f;
        for (int i = 0; i < NUM_STYLES; ++i) {
            sum += adapter.items[i].value;//计算总权重
        }
        allZero = sum == 0.0f;//如果总权重为0.0f则allZero=ture否则为flase
        // 现在更新用于输入张量的值。如果没有设置，则将所有内容均匀混合。否则，所有内容都将归一化为1.0。
        // Now update the values used for the input tensor. If nothing is set, mix in everything
        // equally. Otherwise everything is normalized to sum to 1.0.
        for (int i = 0; i < NUM_STYLES; ++i) {
            styleVals[i] = allZero ? 1.0f / NUM_STYLES : adapter.items[i].value / sum;

            if (lastAllZero != allZero) {
                adapter.items[i].postInvalidate();
            }
        }
    }

    private void resetPreviewBuffers() {
        croppedBitmap = Bitmap.createBitmap( desiredSize, desiredSize, Config.ARGB_8888 );

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                desiredSize, desiredSize,
                sensorOrientation, true );

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert( cropToFrameTransform );
        intValues = new int[desiredSize * desiredSize];
        floatValues = new float[desiredSize * desiredSize * 3];
        initializedSize = desiredSize;
    }

    @Override
    protected void processImage() {
        if (desiredSize != initializedSize) {
            LOGGER.i(
                    "Initializing at size preview size %dx%d, stylize size %d",
                    previewWidth, previewHeight, desiredSize );

            //rgb帧位图
            rgbFrameBitmap = Bitmap.createBitmap( previewWidth, previewHeight, Config.ARGB_8888 );
            //裁剪帧位图
            croppedBitmap = Bitmap.createBitmap( desiredSize, desiredSize, Config.ARGB_8888 );
            //帧到裁剪转换矩阵
            //将照相机获取的原始图片，转换为desiredSize尺寸大小的图片，用来作为模型预测的输入。
            frameToCropTransform = ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight,
                    desiredSize, desiredSize,
                    sensorOrientation, true );
            //裁剪到帧转换矩阵
            cropToFrameTransform = new Matrix();
            //帧到裁剪转换矩阵 将裁剪到帧转换矩阵 转置 得到帧到裁剪转换矩阵
            frameToCropTransform.invert( cropToFrameTransform );
            intValues = new int[desiredSize * desiredSize];
            floatValues = new float[desiredSize * desiredSize * 3];
            initializedSize = desiredSize;
        }
        //RGB帧位图 将位图中的像素替换为getRgbBytes()数组中的颜色。
        rgbFrameBitmap.setPixels( getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight );
        //用croppedBitmap裁剪位图构造画布
        final Canvas canvas = new Canvas( croppedBitmap );
        //使用指定的矩阵绘制位图。
        //参数:
        //rgbFrameBitmap：位图要绘制的位图（这个图就是原始图像位图）
        //frameToCropTransform：矩阵绘制位图时用于转换位图的矩阵
        //null：可能为空。用于绘制位图的绘制
        canvas.drawBitmap( rgbFrameBitmap, frameToCropTransform, null );

        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {//保存裁剪位图
            ImageUtils.saveBitmap( croppedBitmap );
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        //创建复制裁剪位图
                        cropCopyBitmap = Bitmap.createBitmap( croppedBitmap );
                        final long startTime = SystemClock.uptimeMillis();//开始时间
                        stylizeImage( croppedBitmap );//风格变换
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;//耗费时间
                        textureCopyBitmap = Bitmap.createBitmap( croppedBitmap );
                        requestRender();
                        readyForNextImage();
                    }
                } );
        if (desiredSize != initializedSize) {
            resetPreviewBuffers();
        }
    }

    private void stylizeImage(final Bitmap bitmap) {
        ++frameNum;
        //获取位图像素放入到intValues
        bitmap.getPixels( intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight() );

        //调试模式
        if (DEBUG_MODEL) {
            // Create a white square that steps through a black background 1 pixel per frame.
            final int centerX = (frameNum + bitmap.getWidth() / 2) % bitmap.getWidth();
            final int centerY = bitmap.getHeight() / 2;
            final int squareSize = 10;
            for (int i = 0; i < intValues.length; ++i) {
                final int x = i % bitmap.getWidth();
                final int y = i / bitmap.getHeight();
                final float val =
                        Math.abs( x - centerX ) < squareSize && Math.abs( y - centerY ) < squareSize ? 1.0f : 0.0f;
                floatValues[i * 3] = val;
                floatValues[i * 3 + 1] = val;
                floatValues[i * 3 + 2] = val;
            }
        } else {
            for (int i = 0; i < intValues.length; ++i) {
                final int val = intValues[i];
                floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
                floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
            }
        }

        // Copy the input data into TensorFlow.
        LOGGER.i( "Width: %s , Height: %s", bitmap.getWidth(), bitmap.getHeight() );
        inferenceInterface.feed(
                INPUT_NODE, floatValues, 1, bitmap.getWidth(), bitmap.getHeight(), 3 );
        inferenceInterface.feed( STYLE_NODE, styleVals, NUM_STYLES );

        inferenceInterface.run( new String[]{OUTPUT_NODE}, isDebug() );
        inferenceInterface.fetch( OUTPUT_NODE, floatValues );

        for (int i = 0; i < intValues.length; ++i) {
            intValues[i] =
                    0xFF000000
                            | (((int) (floatValues[i * 3] * 255)) << 16)
                            | (((int) (floatValues[i * 3 + 1] * 255)) << 8)
                            | ((int) (floatValues[i * 3 + 2] * 255));
        }

        //给位图重新填充风格迁移后的值
        bitmap.setPixels( intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight() );
    }

    private void renderDebug(final Canvas canvas) {
        // TODO(andrewharp): move result display to its own View instead of using debug overlay.
        final Bitmap texture = textureCopyBitmap;
        if (texture != null) {
            final Matrix matrix = new Matrix();
            final float scaleFactor =
                    DEBUG_MODEL
                            ? 4.0f
                            : Math.min(
                            (float) canvas.getWidth() / texture.getWidth(),
                            (float) canvas.getHeight() / texture.getHeight() );
            matrix.postScale( scaleFactor, scaleFactor );
            canvas.drawBitmap( texture, matrix, new Paint() );
        }

        if (!isDebug()) {
            return;
        }

        final Bitmap copy = cropCopyBitmap;
        if (copy == null) {
            return;
        }

        canvas.drawColor( 0x55000000 );

        final Matrix matrix = new Matrix();
        final float scaleFactor = 2;
        matrix.postScale( scaleFactor, scaleFactor );
        matrix.postTranslate(
                canvas.getWidth() - copy.getWidth() * scaleFactor,
                canvas.getHeight() - copy.getHeight() * scaleFactor );
        canvas.drawBitmap( copy, matrix, new Paint() );

        final Vector<String> lines = new Vector<>();

        final String[] statLines = inferenceInterface.getStatString().split( "\n" );
        Collections.addAll( lines, statLines );

        lines.add( "" );

        lines.add( "Frame: " + previewWidth + "x" + previewHeight );
        lines.add( "Crop: " + copy.getWidth() + "x" + copy.getHeight() );
        lines.add( "View: " + canvas.getWidth() + "x" + canvas.getHeight() );
        lines.add( "Rotation: " + sensorOrientation );
        lines.add( "Inference time: " + lastProcessingTimeMs + "ms" );
        lines.add( "Desired size: " + desiredSize );
        lines.add( "Initialized size: " + initializedSize );

        borderedText.drawLines( canvas, 10, canvas.getHeight() - 10, lines );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int moveOffset = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveOffset = -1;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveOffset = 1;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                moveOffset = -1 * grid.getNumColumns();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                moveOffset = grid.getNumColumns();
                break;
            default:
                return super.onKeyDown( keyCode, event );
        }

        // get the highest selected style
        int currentSelect = 0;
        float highestValue = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.items[i].value > highestValue) {
                currentSelect = i;
                highestValue = adapter.items[i].value;
            }
        }
        // 设置权重为1.0f
        setStyle( adapter.items[(currentSelect + moveOffset + adapter.getCount()) % adapter.getCount()], 1 );

        return true;
    }
}
