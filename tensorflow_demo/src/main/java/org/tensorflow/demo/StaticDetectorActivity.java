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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


@Route(path = "/tensorflow/activity/StaticDetectorActivity")
public class StaticDetectorActivity extends Activity implements View.OnClickListener {
    private Button mButton;
    private ImageView mImageView;
    private Button mTakePhoto;
    private Button mSelectPhoto;
    private Uri imageUri;
    public static final int TAKE_PHOTO=1;
    private static final int SELECT_PHOTO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_static_detector );
        initViews();
        initActivity();
    }


    private void initViews() {
        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mSelectPhoto = (Button) findViewById(R.id.select_photo);


        mButton.setOnClickListener( this );
        mTakePhoto.setOnClickListener( this );
        mSelectPhoto.setOnClickListener( this );

    }

    private void initActivity() {

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
            Bitmap bitmap=BitmapFactory.decodeFile( imagePath );
            /**
             * 把图片旋转为正的方向
             */
            bitmap = rotaingImageView(degree, bitmap);
            mImageView.setImageBitmap( bitmap );
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
}
