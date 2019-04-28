package org.tensorflow.demo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nisco.family.common.base.BaseFragment;

import org.tensorflow.demo.AutoFitTextureView;
import org.tensorflow.demo.OverlayView;
import org.tensorflow.demo.R;
import org.tensorflow.demo.RecognitionScoreView;

/**
 * 临时车辆进厂主档详情fragment
 */
public class ImageFragment extends BaseFragment  {
    private static final String TAG = ImageFragment.class.getSimpleName();
    private View rootView;
    private int isShow = 0;
    private AutoFitTextureView mTexture;
    private OverlayView mDebugOverlay;
    private RecognitionScoreView mResults;

    public static ImageFragment newInstance() {
        Bundle args = new Bundle();
        ImageFragment fragment = new ImageFragment();

        fragment.setArguments(args);
        return fragment;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isShow++;
                    if (isShow > 0) {
//                        containerLl.setVisibility( View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate( R.layout.camera_connection_fragment, container, false);
        initViews();
        initFragment();
        return rootView;
    }

    private void initViews() {


        mTexture = (AutoFitTextureView) rootView.findViewById(R.id.texture);
        mDebugOverlay = (OverlayView) rootView.findViewById(R.id.debug_overlay);
        mResults = (RecognitionScoreView) rootView.findViewById(R.id.results);


    }

    private void initFragment() {

    }

    @Override
    public String getFragmentName() {
        return TAG;
    }



}
