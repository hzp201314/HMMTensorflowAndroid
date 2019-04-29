# HMMTensorflowAndroid

[![Badge](https://img.shields.io/badge/link-996.icu-%23FF4D5B.svg?style=flat-square)](https://996.icu/#/zh_CN)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
## 功能简介
1. 使用`expert-graph`模型实现手写数字识别
2. 使用`mobilenet_v1`和`mobilenet_v2`模型实现相册图片的图像分类
3. 使用`tensorflow_inception_graph`模型实现实时图像分类
4. 使用`graph-tiny-yolo-voc`模型实现实时图像目标检测
5. 使用`stylize_quantized`模型实现图像风格迁移
6. 使用`conv_actions_frozen`模型实现简单单词的语音识别
## 应用效果图

### 1.APP主界面

<img src="image/APPMainActivity.png" width = "500"  div align=left />
<p align=left>图1 APP主界面效果图<p>

### 2.手写数字识别效果图
<table>
    <tr>
        <td ><center><img src="image/mnist_0.png" >图2 手写数字0</center></td>
        <td ><center><img src="image/mnist_1.png" >图3 手写数字1</center></td>
		<td ><center><img src="image/mnist_2.png" >图4 手写数字2</center></td>
		<td ><center><img src="image/mnist_3.png" >图5 手写数字3</center></td>		
    </tr>
	<tr>
        <td ><center><img src="image/mnist_4.png" >图6 手写数字4</center></td>
        <td ><center><img src="image/mnist_5.png" >图7 手写数字5</center></td>
		<td ><center><img src="image/mnist_6.png" >图8 手写数字6</center></td>
		<td ><center><img src="image/mnist_7.png" >图9 手写数字7</center></td>
    </tr>
	<tr>
        <td ><center><img src="image/mnist_8.png" >图10 手写数字8</center></td>
		<td ><center><img src="image/mnist_9.png" >图11 手写数字9</center></td>
		<td ><center><img src="" ></center></td>
		<td ><center><img src="" ></center></td>    
    </tr>

</table>

### 3.相册图片图像分类效果图
#### 3.1 `mobilenet_v1`模型识别效果图
<img src="image/mobilenet_v1.png" width = "500"  div align=left />
<p align=left>图12 mobilenet_v1模型识别效果图<p>

#### 3.2 `mobilenet_v2`模型识别效果图
<img src="image/mobilenet_v2.png" width = "500"  div align=left />
<p align=left>图13 mobilenet_v2模型识别效果图<p>


### 4.实时图像分类效果图
<img src="image/tensorflow_classifier.png" width = "500"  div align=left />
<p align=left>图14 实时图像分类效果图<p>


### 5.实时图像目标检测效果图
<img src="image/tensorflow_detector.png" width = "500"  div align=left />
<p align=left>图15 实时图像目标检测效果图<p>


### 6.图像风格迁移效果图
<table>
	<tr>
        <td ><center><img src="image/tensorflow_stylize_1.png" >图16 图像风格迁移效果图1</center></td>
		<td ><center><img src="image/tensorflow_stylize_2.png" >图17 图像风格迁移效果图2</center></td>  
    </tr>
</table>

### 7.单词语音识别效果图
<table>
	<tr>
        <td ><center><img src="image/tensorflow_speech.png" >图18 单词语音识别界面图</center></td>
		<td ><center><img src="image/tensorflow_speech_yes.png" >图19 单词“Yes”语音识别效果图</center></td> 
		<td ><center><img src="image/tensorflow_speech_no.png" >图20 单词“No”语音识别效果图</center></td> 		
    </tr>
</table>

## 参考资料

* [Tensorflow-android 官方demo源码分析](https://blog.csdn.net/u013510838/article/details/79827119)
* [Tensorflow在手机端的部署——官网Android工程源码分析之TensorFlowYoloDetector.java](https://blog.csdn.net/c20081052/article/details/84387738)
* [Tensorflow-android 官方demo源码](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android)
