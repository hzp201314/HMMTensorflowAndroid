package org.tensorflow.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.nisco.family.common.base.BaseActivity;

import org.tensorflow.demo.adapter.FragmentAdapter;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.fragment.ImageFragment;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


@Route(path = "/tensorflow/activity/StaticDetectorActivity")
public class StaticDetectorActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=StaticDetectorActivity.class.getSimpleName();
    private Button mButton;
    private ImageView mImageView;
    private Button mTakePhoto;
    private Button mSelectPhoto;
    private Uri imageUri;
    public static final int TAKE_PHOTO=1;
    private static final int SELECT_PHOTO=2;

    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged multibox model.
    private static final int MB_INPUT_SIZE = 224;
    private static final int MB_IMAGE_MEAN = 128;
    private static final float MB_IMAGE_STD = 128;
    private static final String MB_INPUT_NAME = "ResizeBilinear";
    private static final String MB_OUTPUT_LOCATIONS_NAME = "output_locations/Reshape";
    private static final String MB_OUTPUT_SCORES_NAME = "output_scores/Reshape";
    private static final String MB_MODEL_FILE = "file:///android_asset/multibox_model.pb";
    private static final String MB_LOCATION_FILE =
            "file:///android_asset/multibox_location_priors.txt";

    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/ssd_mobilenet_v1_android_export.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";

    // Configuration values for tiny-yolo-voc. Note that the graph is not included with TensorFlow and
    // must be manually placed in the assets/ directory by the user.
    // Graphs and models downloaded from http://pjreddie.com/darknet/yolo/ may be converted e.g. via
    // DarkFlow (https://github.com/thtrieu/darkflow). Sample command:
    // ./flow --model cfg/tiny-yolo-voc.cfg --load bin/tiny-yolo-voc.weights --savepb --verbalise
    private static final String YOLO_MODEL_FILE = "file:///android_asset/graph-tiny-yolo-voc.pb";
    private static final int YOLO_INPUT_SIZE = 416;
    private static final String YOLO_INPUT_NAME = "input";
    private static final String YOLO_OUTPUT_NAMES = "output";
    private static final int YOLO_BLOCK_SIZE = 32;
    private int bitmapWidth;
    private int bitmapHeight;
    private int previewWidth;
    private int previewHeight;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mDatas;
    private ImageFragment imageFragment;
    private FragmentAdapter mAdapter;
    private int currentIndex;
    private byte[][] yuvBytes;
    private int yRowStride;
    private int[] rgbBytes;
    private Bitmap bitmap;

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.  Optionally use legacy Multibox (trained using an older version of the API)
    // or YOLO.
    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }
    private static final StaticDetectorActivity.DetectorMode MODE = StaticDetectorActivity.DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;
    private static final float MINIMUM_CONFIDENCE_MULTIBOX = 0.1f;
    private static final float MINIMUM_CONFIDENCE_YOLO = 0.25f;

    private static final boolean MAINTAIN_ASPECT = MODE == StaticDetectorActivity.DetectorMode.YOLO;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private byte[] luminanceCopy;

    private BorderedText borderedText;

    private boolean debug = false;

//handler.sendEmptyMessage(0);
    private long ishow=0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ishow++;
                    if (ishow > 1) {
                        mImageView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_static_detector );
        initViews();
        initActivity();
    }

    @Override
    public String getActivityName() {
        return TAG;
    }




    private void initViews() {
        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mSelectPhoto = (Button) findViewById(R.id.select_photo);
        mViewPager =(ViewPager)findViewById( R.id.viewpager );

        mButton.setOnClickListener( this );
        mTakePhoto.setOnClickListener( this );
        mSelectPhoto.setOnClickListener( this );

    }

    private void initActivity() {
        ViewPagerfounction();
    }

    private void ViewPagerfounction() {
        mDatas =new ArrayList<Fragment>(  );

        imageFragment=new ImageFragment();
        imageFragment.setArguments( null );
        mDatas.add( imageFragment );

        mAdapter=new FragmentAdapter( this.getSupportFragmentManager(), mDatas );
        mViewPager.setOffscreenPageLimit( 1 );
        mViewPager.setAdapter( mAdapter );
        mViewPager.setCurrentItem( 0 );
        mViewPager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                 /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0 ==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        } );


    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button) {//选择图片
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 2);

        }else if(i==R.id.take_photo){//拍照
            File outputImage=new File( getExternalCacheDir(),"output_image.jpg" );
            try {
                if(outputImage.exists()){
                    outputImage.delete();
                }
                outputImage.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(Build.VERSION.SDK_INT>=24){
                imageUri= FileProvider.getUriForFile( this,"com.example.cameraalbumtest.fileprovider",outputImage );
            }else {
                imageUri=Uri.fromFile( outputImage );
            }

            //启动相机程序
            Intent intent=new Intent( "android.media.action.IMAGE_CAPTURE" );
            intent.putExtra( MediaStore.EXTRA_OUTPUT,imageUri );
            startActivityForResult( intent,TAKE_PHOTO );
        }else if(i==R.id.select_photo){//选择相册图片
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions( this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
            }else {
                openAlbum();
            }
        }
    }

    private void openAlbum() {
        Intent intent=new Intent( "android.intent.action.GET_CONTENT" );
        intent.setType( "image/*" );
        startActivityForResult( intent,SELECT_PHOTO );//打开相册
    }

    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch(requestCode){
            case 3:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText( this,"你拒绝了请求的权限",Toast.LENGTH_SHORT ).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try{
                        /**
                         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                         */
                        int degree =readPictureDegree(imageUri.getPath());
                        //将拍摄的照片显示出来
                        Bitmap bitmap= BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
                        /**
                         * 把图片旋转为正的方向
                         */
                        bitmap = rotaingImageView(degree+90, bitmap);
                        mImageView.setImageBitmap( bitmap );
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if(resultCode==RESULT_OK){
                   //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat( data );
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat( data );
                    }
                }
                break;
        }
//        if (requestCode == 2) {
//            // 从相册返回的数据
//            if (data != null) {
//                // 得到图片的全路径
//                Uri uri = data.getData();
//                mImageView.setImageURI(uri);
//            }
//        }


    }

    @TargetApi( 19 )
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri( this,uri )){
            //如果是document类型的Uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId( uri );
            if("com.android.providers.media.documents".equals( uri.getAuthority() )){
                String id =docId.split( ":" )[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals( uri.getAuthority() )){
                Uri contentUri= ContentUris.withAppendedId( Uri.parse( "content://downloads/public_downloads" ),Long.valueOf( docId ) );
                imagePath=getImagePath( contentUri,null );
            }
        }else if("content".equalsIgnoreCase( uri.getScheme() )){
            //如果是content 类型的Uri，则使用普通方式处理
            imagePath=getImagePath( uri,null );
        }else if ("file".equalsIgnoreCase( uri.getScheme() )){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri =data.getData();
        String imagePath=getImagePath( uri,null );
        displayImage( imagePath );
    }

    private void displayImage(String imagePath) {
        if(imagePath!=null){
            /**
             * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
             */
            int degree =readPictureDegree(imagePath);
            bitmap = BitmapFactory.decodeFile( imagePath );
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            final Size size=new Size(bitmapWidth,bitmapHeight  );
            Log.d( "111", "displayImage: 图片宽高"+bitmapWidth+"*"+bitmapHeight );
            /**
             * 把图片旋转为正的方向
             */
            bitmap = rotaingImageView(degree, bitmap );

            new Handler().postDelayed( new Runnable() {
                @Override
                public void run() {
                    onPreviewSizeChosen(size, 90);
                    processImage();

                }
            }, 50);


//            mImageView.setImageBitmap( bitmap );


        }else {
            Toast.makeText( this,"failed to get image",Toast.LENGTH_SHORT ).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor=getContentResolver().query( uri,null,selection,null,null );
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ) );
            }
            cursor.close();
        }
        return path;
    }



    /////////////////////
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface( Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            //TODO 用的这个模型
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            LOGGER.e("Exception initializing classifier!", e);
            Toast toast =
                    Toast.makeText( getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }


        //图片宽高
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        //    rotation=90   getScreenOrientation=0
        Log.d( "111", "onPreviewSizeChosen: rotation="+rotation+";getScreenOrientation="+getScreenOrientation() );
        //摄像头的方向
        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        //创建bitmap
        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight );
//        rgbFrameBitmap = Bitmap.createBitmap( previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        // 将照相机获取的原始图片，转换为cropSize=300*300的图片，用来作为模型预测的输入。
        // 帧到裁剪转换
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        //裁剪到帧转换
        cropToFrameTransform = new Matrix();
        // 帧到裁剪转换
        frameToCropTransform.invert(cropToFrameTransform);

        //跟踪覆盖视图
        trackingOverlay = (OverlayView) findViewById( R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (!isDebug()) {
                            return;
                        }
                        // 裁剪复制位图
                        final Bitmap copy = cropCopyBitmap;
                        if (copy == null) {
                            return;
                        }

                        final int backgroundColor = Color.argb(100, 0, 0, 0);
                        canvas.drawColor(backgroundColor);

                        final Matrix matrix = new Matrix();
                        final float scaleFactor = 2;
                        matrix.postScale(scaleFactor, scaleFactor);
                        matrix.postTranslate(
                                canvas.getWidth() - copy.getWidth() * scaleFactor,
                                canvas.getHeight() - copy.getHeight() * scaleFactor);
                        canvas.drawBitmap(copy, matrix, new Paint());

                        final Vector<String> lines = new Vector<String>();
                        if (detector != null) {
                            final String statString = detector.getStatString();
                            final String[] statLines = statString.split("\n");
                            for (final String line : statLines) {
                                lines.add(line);
                            }
                        }
                        lines.add("");

                        lines.add("Frame: " + previewWidth + "x" + previewHeight );
                        lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
                        lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
                        lines.add("Rotation: " + sensorOrientation);
                        lines.add("Inference time: " + lastProcessingTimeMs + "ms");

                        //边缘画线
                        borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
                    }
                });
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void addCallback(final OverlayView.DrawCallback callback) {
        final OverlayView overlay = (OverlayView) findViewById( R.id.debug_overlay);
        if (overlay != null) {
            overlay.addCallback(callback);
        }
    }

    //跟踪覆盖视图
    OverlayView trackingOverlay;

    /**
     * 利用训练模型来预测图片
     * processImage()先做图片绘制方面的工作，将相机捕获的图片绘制出来。
     * 然后利用分类器classifier来识别图片，获取图片为每个分类的概率。
     * 最后将概率最大的前三个分类，展示在result区域上。
     */
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;//当前时间
        byte[] originalLuminance = getLuminance();
        tracker.onFrame(
                previewWidth,
                previewHeight,
                getLuminanceStride(),
                sensorOrientation,
                originalLuminance,
                timestamp);
        trackingOverlay.postInvalidate();

////    不需要互斥，因为此方法不可重入。
//        // No mutex needed as this method is not reentrant.
//        if (computingDetection) {
//            readyForNextImage();//准备下次图片
//            return;
//        }
//      计算机检测标识，true为检测完毕，进入下一次的检测
//        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        //RGB帧位图
//        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight );

        if (luminanceCopy == null) {
            luminanceCopy = new byte[originalLuminance.length];
        }
        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
//        readyForNextImage();

        //裁剪位图
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(bitmap, frameToCropTransform, null);
        //用于检查实际tf输入。
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);//保存
        }

        // 利用分类器classifier对图片进行预测分析，得到图片为每个分类的概率. 比较耗时，放在子线程中
//        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        //识别图像
                        // 1 classifier对图片进行识别，得到输入图片为每个分类的概率
                        //重点:分类器识别图片关键方法classifier.recognizeImage()
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;//检测时间

                        // 2 将得到的前三个最大概率的分类的名字及概率，反馈到app上。也就是results区域
                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor( Color.RED);
                        paint.setStyle( Paint.Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        //for循环画出所有的检测结果，并且封装到mappedRecognitions中
                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);
                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                        trackingOverlay.postInvalidate();

                        // 3 请求重绘，并准备下一次的识别
//                        requestRender();
                        computingDetection = false;
                    }
                };
//                );
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }

    protected byte[] getLuminance() {
        return yuvBytes[0];
    }
    private Runnable imageConverter;
    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }


    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_tracking;
    }


    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }


    public void onSetDebug(final boolean debug) {
        detector.enableStatLogging(debug);
    }
}
