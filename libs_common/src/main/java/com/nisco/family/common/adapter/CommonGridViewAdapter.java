package com.nisco.family.common.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nisco.family.common.R;
import com.nisco.family.common.model.ReportMenuModel;
import com.nisco.family.common.model.UserApp;
import com.nisco.family.common.model.Video;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class CommonGridViewAdapter<T> extends BaseAdapter {
    private final String TAG = CommonGridViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<UserApp> userApps;
    private LinkedList<Video> videos;
    private List<T> mDatas;
    private int layoutId;
    private int flag;

    public CommonGridViewAdapter(Context context, List<T> datas, int layoutId) {
        super();
        this.mContext = context;
        this.mDatas = datas;
        this.layoutId = layoutId;
//        flag = 0;
    }

//    public CommonGridViewAdapter(Context context, LinkedList<Video> videos) {
//        super();
//        this.mContext = context;
//        this.videos = videos;
//        flag = 1;
//    }
//
//    public CommonGridViewAdapter(Context context, List<T> reportDatas, int flag) {
//        super();
//        this.mContext = context;
//        this.reportDatas = reportDatas;
//        this.flag = flag;
//    }

    @Override
    public int getCount() {
//        if (flag == 0) {
//            return userApps.size();
//        } else if (flag == 1) {
//            return videos.size();
//        } else {
//            return reportDatas.size();
//        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
//        if (flag == 0) {
//            return userApps.get(position);
//        } else if (flag == 1) {
//            return videos.get(position);
//        } else {
//            return reportDatas.get(position);
//        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;


        if (mDatas.get(position) instanceof UserApp) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(layoutId, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.update((UserApp) mDatas.get(position));
        }
//        else if (flag == 1) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_grid_item, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.update(videos.get(position));
//        } else if (flag == 2) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.report_item, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.update((UserApp) reportDatas.get(position));
//        }else if (flag == 3) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item1, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.update((UserApp) reportDatas.get(position));
//
//        }

        return convertView;
    }

    public class ViewHolder {

        private ImageView appIcon;
        private TextView appText;

        public ViewHolder(View view) {
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
            appText = (TextView) view.findViewById(R.id.app_text);
        }

        private void update(UserApp userApp) {
            appIcon.setBackgroundResource(userApp.getAppIcon());
            appText.setText(userApp.getAppName());
        }

        private void update(Video video) {
            String newImageUrl = video.getNewSnapshotUrl();
            String imageUrl = video.getSnapshotUrl();
            if (newImageUrl.equalsIgnoreCase("") || newImageUrl == null) {
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.common_default_img).into(appIcon);
            } else {
                Glide.with(mContext).load(newImageUrl).placeholder(R.drawable.common_default_img).into(appIcon);
            }
            appText.setText(video.getVideoName());
        }

        private void update(ReportMenuModel reportMenuModel) {
            String title = reportMenuModel.getStrTitle();
            appText.setText(title);
            InputStream is = mContext.getResources().openRawResource(reportMenuModel.getImage());
            Bitmap bg_bitmap = BitmapFactory.decodeStream(is);
            appIcon.setImageBitmap(bg_bitmap);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
