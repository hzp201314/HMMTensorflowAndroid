/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.demo;

import android.content.res.AssetManager;//assets文件夹下的文件不会被映射到R.java中，访问的时候需要AssetManager类
import android.graphics.Bitmap;//导入安卓系统的图像处理类Bitmap ,以便进行图像剪切、旋转、缩放等操作，并可以指定格式保存图像文件
import android.graphics.RectF;//这个类包含一个矩形的四个单精度浮点坐标。矩形通过上下左右4个边的坐标来表示一个矩形
import android.os.Trace;// Android SDK中提供了`android.os.Trace#beginSection`和`android.os.Trace#endSection` 这两个接口，我们可以在代码中插入这些代码来分析某个特定的过程。

import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;//tf针对安卓封装的inference类
import org.tensorflow.demo.env.Logger;//定义的一个类用于LOG日志生成便于分析

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Wrapper for frozen detection models trained using the Tensorflow Object Detection API:
 * github.com/tensorflow/models/tree/master/research/object_detection
 */
public class TensorFlowObjectDetectionAPIModel implements Classifier {
  private static final Logger LOGGER = new Logger();

  // Only return this many results.
  private static final int MAX_RESULTS = 100;//根据概率刷选出的前100个结果

  // Config values.
  private String inputName;//输入名称
  private int inputSize;//输入尺寸

  //预先分配buffer
  // Pre-allocated buffers.
  private Vector<String> labels = new Vector<String>();
  private int[] intValues;//整型数组（传入网络图像尺寸长*宽）
  private byte[] byteValues;
  private float[] outputLocations;
  private float[] outputScores;
  private float[] outputClasses;
  private float[] outputNumDetections;
  private String[] outputNames;//输出名

  private boolean logStats = false;//log状态

  private TensorFlowInferenceInterface inferenceInterface;

  /**
   * Initializes a native TensorFlow session for classifying images.
   *
   * @param assetManager The asset manager to be used to load assets.
   * @param modelFilename The filepath of the model GraphDef protocol buffer.
   * @param labelFilename The filepath of label file for classes.
   */
  public static Classifier create(
      final AssetManager assetManager,//资源管理类 对象实例
      final String modelFilename,//模型名
      final String labelFilename,//标签名
      final int inputSize) throws IOException {//输入图片尺寸
    final TensorFlowObjectDetectionAPIModel d = new TensorFlowObjectDetectionAPIModel();

    InputStream labelsInput = null;
    String actualFilename = labelFilename.split("file:///android_asset/")[1];
    labelsInput = assetManager.open(actualFilename);
    BufferedReader br = null;
    br = new BufferedReader(new InputStreamReader(labelsInput));
    String line;
    while ((line = br.readLine()) != null) {
      LOGGER.w(line);
      d.labels.add(line);
    }
    br.close();


    d.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

    final Graph g = d.inferenceInterface.graph();

    d.inputName = "image_tensor";
    // The inputName node has a shape of [N, H, W, C], where
    // N is the batch size
    // H = W are the height and width
    // C is the number of channels (3 for our purposes - RGB)
    final Operation inputOp = g.operation(d.inputName);
    if (inputOp == null) {
      throw new RuntimeException("Failed to find input Node '" + d.inputName + "'");
    }
    d.inputSize = inputSize;
    // The outputScoresName node has a shape of [N, NumLocations], where N
    // is the batch size.
    final Operation outputOp1 = g.operation("detection_scores");
    if (outputOp1 == null) {
      throw new RuntimeException("Failed to find output Node 'detection_scores'");
    }
    final Operation outputOp2 = g.operation("detection_boxes");
    if (outputOp2 == null) {
      throw new RuntimeException("Failed to find output Node 'detection_boxes'");
    }
    final Operation outputOp3 = g.operation("detection_classes");
    if (outputOp3 == null) {
      throw new RuntimeException("Failed to find output Node 'detection_classes'");
    }

    //框，分数，类别
    // Pre-allocate buffers.
    d.outputNames = new String[] {"detection_boxes", "detection_scores",
                                  "detection_classes", "num_detections"};
    d.intValues = new int[d.inputSize * d.inputSize];//输入图片长*宽
    d.byteValues = new byte[d.inputSize * d.inputSize * 3];//长*宽*3
    d.outputScores = new float[MAX_RESULTS];//分数
    d.outputLocations = new float[MAX_RESULTS * 4];//输出位置
    d.outputClasses = new float[MAX_RESULTS];//输出类别
    d.outputNumDetections = new float[1];//输出数检测
    return d;//返回对象
  }

  private TensorFlowObjectDetectionAPIModel() {}

  /**
   * 识别图片
   * 步骤：
   * 1.预处理输入图片，读取像素点，并将RGB三通道数值归一化. 归一化后分布于 -117 ~ 138
   * 2.将输入数据填充到TensorFlow中，并feed数据给模型；feed用来填充输入图片
   * 3.跑TensorFlow预测模型；run用来跑模型并得到结果
   * 4.将tensorflow预测模型输出节点的输出值拷贝出来；fetch用来从TensorFlow内部获取输出节点的输出值
   * 5.得到概率最大的前三个分类，并组装为Recognition对象
   * @param bitmap
   * @return
   */
  @Override
  public List<Recognition> recognizeImage(final Bitmap bitmap) {
    // Log this method so that it can be analyzed with systrace.
    //跟踪：recognizeImage
    Trace.beginSection("recognizeImage");

    //跟踪：preprocessBitmap
    Trace.beginSection("preprocessBitmap");
    // 1 预处理输入图片，读取像素点，并将RGB三通道数值归一化. 归一化后分布于 -117 ~ 138
    // Preprocess the image data to extract R, G and B bytes from int of form 0x00RRGGBB
    // on the provided parameters.
// 以像素为单位返回位图中数据的副本。
// 每个值都是表示颜色的压缩int。stride参数允许调用者允许行之间返回的像素数组中存在间隙。
// 对于正常的压缩结果，只需传递跨距值的宽度。
    //intValues：：接收位图颜色的数组 The array to receive the bitmap's colors
    //偏移：写入像素的第一个索引[]
    //步幅：行与行之间要跳过的以像素为单位的条目数（必须大于等于位图的宽度）。可以是负数。
    //x：从位图中读取的第一个像素的x坐标
    //Y：从位图中读取的第一个像素的Y坐标
    //宽度：从每行读取的像素数
    //高度:要读取的行数
    /**
     * Returns in pixels[] a copy of the data in the bitmap. Each value is
     * a packed int representing a {@link Color}. The stride parameter allows
     * the caller to allow for gaps in the returned pixels array between
     * rows. For normal packed results, just pass width for the stride value.
     * The returned colors are non-premultiplied ARGB values in the
     * {@link ColorSpace.Named#SRGB sRGB} color space.
     *
     * @param pixels   The array to receive the bitmap's colors
     * @param offset   The first index to write into pixels[]
     * @param stride   The number of entries in pixels[] to skip between
     *                 rows (must be >= bitmap's width). Can be negative.
     * @param x        The x coordinate of the first pixel to read from
     *                 the bitmap
     * @param y        The y coordinate of the first pixel to read from
     *                 the bitmap
     * @param width    The number of pixels to read from each row
     * @param height   The number of rows to read
     */
    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

    //转换 int[]转byte[] 将int数组转换为占三个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
    for (int i = 0; i < intValues.length; ++i) {
      byteValues[i * 3 + 2] = (byte) (intValues[i] & 0xFF);
      byteValues[i * 3 + 1] = (byte) ((intValues[i] >> 8) & 0xFF);
      byteValues[i * 3 + 0] = (byte) ((intValues[i] >> 16) & 0xFF);
    }
    Trace.endSection(); // preprocessBitmap

    // 2 将输入数据填充到TensorFlow中，并feed数据给模型
    // inputName为输入节点
    // floatValues为输入tensor的数据源，
    // dims构成了tensor的shape, [batch_size, height, width, in_channel], 此处为[1, inputSize, inputSize, 3]
    // Copy the input data into TensorFlow.
    Trace.beginSection("feed");
    inferenceInterface.feed(inputName, byteValues, 1, inputSize, inputSize, 3);
    Trace.endSection();

    // 3 跑TensorFlow预测模型
    // outputNames为输出节点名， 通过session来run tensor
    // Run the inference call.
    Trace.beginSection("run");
    inferenceInterface.run(outputNames, logStats);
    Trace.endSection();

    // 4 将tensorflow预测模型输出节点的输出值拷贝出来
    // 找到输出节点outputName的tensor，并复制到outputs中。
    // outputs为分类预测的结果，是一个一维向量，
    // 每个值对应labels中一个分类的概率。
    // Copy the output Tensor back into the output array.
    Trace.beginSection("fetch");
    outputLocations = new float[MAX_RESULTS * 4];//100*4
    outputScores = new float[MAX_RESULTS];//分数
    outputClasses = new float[MAX_RESULTS];//类别
    outputNumDetections = new float[1];//识别
    inferenceInterface.fetch(outputNames[0], outputLocations);
    inferenceInterface.fetch(outputNames[1], outputScores);
    inferenceInterface.fetch(outputNames[2], outputClasses);
    inferenceInterface.fetch(outputNames[3], outputNumDetections);
    Trace.endSection();

    // 5 得到概率最大的前三个分类，并组装为Recognition对象
    // Find the best detections.
    final PriorityQueue<Recognition> pq =
        new PriorityQueue<Recognition>(
            1,
            new Comparator<Recognition>() {
              @Override
              public int compare(final Recognition lhs, final Recognition rhs) {
                // Intentionally reversed to put high confidence at the head of the queue.
                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
              }
            });

    //将它们缩放回输入大小
    // Scale them back to the input size.
    for (int i = 0; i < outputScores.length; ++i) {
      //矩形的四个单精度浮点坐标。矩形通过上下左右4个边的坐标来表示一个矩形
      final RectF detection =
          new RectF(
              outputLocations[4 * i + 1] * inputSize,
              outputLocations[4 * i] * inputSize,
              outputLocations[4 * i + 3] * inputSize,
              outputLocations[4 * i + 2] * inputSize);
      pq.add(
              //封装： id，标签类别，精确度分数，矩形区域
          new Recognition("" + i, labels.get((int) outputClasses[i]), outputScores[i], detection));
    }

    //将Recognition实例放入数组列表recognitions中
    final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
    for (int i = 0; i < Math.min(pq.size(), MAX_RESULTS); ++i) {
      recognitions.add(pq.poll());
    }
    Trace.endSection(); // "recognizeImage"
    //返回recognitions，识别检测结束
    return recognitions;
  }

  @Override
  public void enableStatLogging(final boolean logStats) {
    this.logStats = logStats;
  }

  @Override
  public String getStatString() {
    return inferenceInterface.getStatString();
  }

  @Override
  public void close() {
    inferenceInterface.close();
  }
}
