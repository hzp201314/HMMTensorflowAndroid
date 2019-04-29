/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.util.List;
import java.util.Vector;
@Route(path = "/tensorflow/activity/ClassifierActivity")
public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();

  protected static final boolean SAVE_PREVIEW_BITMAP = false;

  private ResultsView resultsView;

  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private long lastProcessingTimeMs;

  // These are the settings for the original v1 Inception model. If you want to
  // use a model that's been produced from the TensorFlow for Poets codelab,
  // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
  // INPUT_NAME = "Mul", and OUTPUT_NAME = "final_result".
  // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
  // the ones you produced.
  //
  // To use v3 Inception model, strip the DecodeJpeg Op from your retrained
  // model first:
  //
  // python strip_unused.py \
  // --input_graph=<retrained-pb-file> \
  // --output_graph=<your-stripped-pb-file> \
  // --input_node_names="Mul" \
  // --output_node_names="final_result" \
  // --input_binary=true
  private static final int INPUT_SIZE = 224;
  private static final int IMAGE_MEAN = 117;
  private static final float IMAGE_STD = 1;
  private static final String INPUT_NAME = "input";
  private static final String OUTPUT_NAME = "output";


  private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
  private static final String LABEL_FILE =
      "file:///android_asset/imagenet_comp_graph_label_strings.txt";


  private static final boolean MAINTAIN_ASPECT = true;

  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);


  private Integer sensorOrientation;
  private Classifier classifier;
  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;


  private BorderedText borderedText;


  @Override
  protected int getLayoutId() {
    return R.layout.camera_connection_fragment;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  private static final float TEXT_SIZE_DIP = 10;

  /**
   *  CameraAcitivty中new的CameraConnnectionFragment实例中的第一个回调cameraConnectCallback
   *  CameraConnnectionFragment的onResume()
   *  --->openCamera()方法打开摄像头
   *  --->设置捕获相机输出方法setupCameraOutputs()方法
   *  --->获取的预览图片的大小perviewSize，摄像头的方向sensorientation，
   *      更重要是回调了cameraConnectCallback中的onPreviewSizeChosen()方法，
   *  onPreviewSizeChosen()方法中包含perviewSize(图片大小)和sensorientation(摄像头方向)
   *  onPreviewSizeChosen(size,rotation)方法会回调到ClassifierActivity中
   *  图片预览展现出来时回调。主要是构造分类器classifier，和裁剪输入图片为224*224
   *
   * onPreviewSizeChosen()方法作用：
   * 1.构造分类器classifier，它是模型分类预测的一个比较关键的类
   * 2.预处理输入图片，如裁剪到和模型训练所使用的图片相同的尺寸
   * @param size 图片大小，回调获取
   * @param rotation 摄像头方向回调获取
   */
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
  //    这个方法的作用是 把Android系统中的非标准度量尺寸转变为标准度量尺寸 (Android系统中的标准尺寸是px, 即像素)
    final float textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    //边框文本框
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    // 构造分类器，利用了TensorFlow训练出来的Model，也就是.pb文件。
    // 这是后面做物体分类识别的关键
    classifier =
        TensorFlowImageClassifier.create(
            getAssets(),
            MODEL_FILE,
            LABEL_FILE,
            INPUT_SIZE,
            IMAGE_MEAN,
            IMAGE_STD,
            INPUT_NAME,
            OUTPUT_NAME);

    //图片宽高
    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    //获取窗口屏幕的默认信息
    final Display display = getWindowManager().getDefaultDisplay();
    final int screenOrientation = display.getRotation();//屏幕方向

    LOGGER.i("Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation);

    //方向
    sensorOrientation = rotation + screenOrientation;

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    //新建rgb帧位图
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    //新建裁剪位图
    croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

    // 将照相机获取的原始图片，转换为224*224的图片，用来作为模型预测的输入。
    frameToCropTransform = ImageUtils.getTransformationMatrix(
        previewWidth, previewHeight,
        INPUT_SIZE, INPUT_SIZE,
        sensorOrientation, MAINTAIN_ASPECT);

    //新建裁剪到帧转换矩阵
    cropToFrameTransform = new Matrix();
    //帧到裁剪转换矩阵 转置裁剪到帧转换矩阵
    frameToCropTransform.invert(cropToFrameTransform);

    //添加回调
    /*调试debug视图*/
    addCallback(//绘制回调
        new DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            //渲染调试 画布
            renderDebug(canvas);
          }
        });
  }

  /**
   * 利用训练模型来预测图片
   * processImage()先做图片绘制方面的工作，将相机捕获的图片绘制出来。
   * 然后利用分类器classifier来识别图片，获取图片为每个分类的概率。
   * 最后将概率最大的前三个分类，展示在result区域上。
   */
  @Override
  protected void processImage() {
    // 将位图中的像素替换为getRgbBytes()数组中的颜色。
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    //用croppedBitmap裁剪位图构造画布
    final Canvas canvas = new Canvas(croppedBitmap);
    //使用指定的矩阵绘制位图。
    //参数
    //rgbFrameBitmap：位图要绘制的位图
    //frameToCropTransform：矩阵绘制位图时用于转换位图的矩阵
    //null：可能为空。用于绘制位图的绘制
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    // 利用分类器classifier对图片进行预测分析，得到图片为每个分类的概率. 比较耗时，放在子线程中，在后台运行
    runInBackground(
        new Runnable() {
          @Override
          public void run() {
            final long startTime = SystemClock.uptimeMillis();//开始时间
            // 1 classifier对图片进行识别，得到输入图片为每个分类的概率
            //重点:分类器识别图片关键方法classifier.recognizeImage()
            final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;//分类耗费时间
            LOGGER.i("Detect: %s", results);
            // 2 将得到的前三个最大概率的分类的名字及概率，反馈到app上。也就是results区域
            //创建裁剪复制位图
            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
            if (resultsView == null) {
              resultsView = (ResultsView) findViewById( R.id.results);
            }
            resultsView.setResults(results);
            // 3 请求重绘，并准备下一次的识别
            requestRender();
            readyForNextImage();
          }
        });
  }

  @Override
  public void onSetDebug(boolean debug) {
    classifier.enableStatLogging(debug);
  }

  private void renderDebug(final Canvas canvas) {
    //是否调试模式
    if (!isDebug()) {//不是就return
      return;
    }

    //新建一个裁剪复制位图
    final Bitmap copy = cropCopyBitmap;
    if (copy != null) {
      //新建一个矩阵
      final Matrix matrix = new Matrix();
      //scale缩放比例因子
      final float scaleFactor = 2;
      //缩放
      matrix.postScale(scaleFactor, scaleFactor);
      //平移
      matrix.postTranslate(
          canvas.getWidth() - copy.getWidth() * scaleFactor,
          canvas.getHeight() - copy.getHeight() * scaleFactor);
      //绘制位图
      canvas.drawBitmap(copy, matrix, new Paint());

      //矢量线集合
      final Vector<String> lines = new Vector<String>();
      if (classifier != null) {
        String statString = classifier.getStatString();
        String[] statLines = statString.split("\n");
        for (String line : statLines) {
          lines.add(line);//线
        }
      }

      lines.add("Frame: " + previewWidth + "x" + previewHeight);//帧图像宽高
      lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());//复制框的宽高
      lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());//画布宽高
      lines.add("Rotation: " + sensorOrientation);//方向
      lines.add("Inference time: " + lastProcessingTimeMs + "ms");//时间

      borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);//绘制边框文本图像
    }
  }
}
