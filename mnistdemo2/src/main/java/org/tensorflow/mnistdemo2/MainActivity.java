package org.tensorflow.mnistdemo2;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.tensorflow.mnistdemo2.view.DrawModel;
import org.tensorflow.mnistdemo2.view.DrawView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Route( path = "/mnist2/activity/MainActivity")
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    private static final int PIXEL_WIDTH = 28;

    private TextView mResultText;

    private float mLastX;

    private float mLastY;

    private DrawModel mModel;
    private DrawView mDrawView;

    private View detectButton;

    private PointF mTmpPoint = new PointF();

    private static final int INPUT_SIZE = 28;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

//    private static final String MODEL_FILE = "file:///android_asset/mnist_model_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/graph_label_strings.txt";

    private static final String MODEL_FILE = "file:///android_asset/expert-graph.pb";
//    private static final String LABEL_FILE = "file:///android_asset/label.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
//创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。



    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mnist2_activity_main );
        mModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);

        mDrawView = (DrawView) findViewById(R.id.view_draw);
        mDrawView.setModel(mModel);
        mDrawView.setOnTouchListener(this);
        
        //识别按钮
        detectButton = findViewById(R.id.buttonDetect);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetectClicked();
            }
        });

        //清除按钮
        View clearButton = findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClicked();
            }
        });
        //文本框用来存放结果
        mResultText = (TextView) findViewById(R.id.textResult);

        //初始化TF和模型
        initTensorFlowAndLoadModel();
    }
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建分类器
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();//显示按钮
                    Log.d(TAG, "Load Success");
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
//点击识别按钮使识别结果可见
    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                detectButton.setVisibility(View.VISIBLE);
            }
        });
    }
//手写数字
    @Override
    protected void onResume() {
        mDrawView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDrawView.onPause();
        super.onPause();
    }
//触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {//如果手指按下
            processTouchDown(event);
            return true;

        } else if (action == MotionEvent.ACTION_MOVE) {//手指移动
            processTouchMove(event);
            return true;

        } else if (action == MotionEvent.ACTION_UP) {//手指抬起
            processTouchUp();
            return true;
        }
        return false;
    }

    /**
     * 开始触碰
     * @param event
     */
    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX(); //触摸点相对于其所在组件原点的x坐标
        mLastY = event.getY();// 触摸点相对于其所在组件原点的y坐标
        mDrawView.calcPos(mLastX, mLastY, mTmpPoint);
        float lastConvX = mTmpPoint.x;
        float lastConvY = mTmpPoint.y;
        mModel.startLine(lastConvX, lastConvY);
    }

    /**
     * 触摸移动
     * @param event
     */
    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mDrawView.calcPos(x, y, mTmpPoint);
        float newConvX = mTmpPoint.x;
        float newConvY = mTmpPoint.y;
        mModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        mDrawView.invalidate();
    }

    /**
     * 结束触碰
     */
    private void processTouchUp() {
        mModel.endLine();
    }

    /**
     * 识别按钮
     */
    private void onDetectClicked() {
        float pixels[] = mDrawView.getPixelData();//获取mDrawView的像素

        //进行识别，结果放在分类器
        final List<Classifier.Recognition> results = classifier.recognizeImage(pixels);

        if (results.size() > 0) {
            String value = " 识别结果 : " +results.get(0).getTitle();//取最大的一个值
            mResultText.setText(value);
        }

    }

    /**
     * 清除画板
     */
    private void onClearClicked() {
        mModel.clear();
        mDrawView.reset();
        mDrawView.invalidate();

        mResultText.setText(" 识别结果 : ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }
}
