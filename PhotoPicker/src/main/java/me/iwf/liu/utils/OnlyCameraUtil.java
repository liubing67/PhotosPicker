package me.iwf.liu.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import me.iwf.photopicker.R;
import me.iwf.photopicker.utils.ImageCaptureManager;

/**
 * 项目名称：BingYao
 * 类描述： 获取图片的方法
 * 创建人：liubing
 * 创建时间：2016/9/27 9:28
 * 修改人：Administrator
 * 修改时间：2016/9/27 9:28
 * 修改备注：
 * 使用方法，
 * 第一步：获取对象的实例化  private OnlyCameraUtil onlyCameraUtil=new OnlyCameraUtil(MainActivity.this);
 * <p>
 * 第二步：获取点击方法    findViewById(R.id.button_one_photo).setOnClickListener(new View.OnClickListener() {
 * *  @Override public void onClick(View v) {
 * /////权限请求
 * <p>
 * onlyCameraUtil.takePhoto(1);
 * }
 * });
 * <p>
 * 第三步：拍照完回调
 *
 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * super.onActivityResult(requestCode, resultCode, data);
 * if (requestCode==1)
 * {
 * onlyCameraUtil.onActivityResult(requestCode,resultCode,data,image,null,null);
 * }.
 */

public class OnlyCameraUtil {

    private static Activity activity;
    private static final int GET_BY_ALBUM = 801;//如果有冲突，记得修改
    private static final int GET_BY_CROP = 802;//如果有冲突，记得修改
    private static String path;
    private static byte[] mContent;
    private static boolean crop;
    private ImageCaptureManager captureManager;
    public OnlyCameraUtil(Activity conten) {
        this.activity = conten;
        captureManager = new ImageCaptureManager(activity);
    }

    /**
     * 调用系统相机拍照
     */
    public void takePhoto(int requestCode, boolean iscrop) {
        crop = iscrop;

        Intent intent = null;
        try {
            intent=captureManager.dispatchTakePictureIntent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        path=captureManager.getCurrentPhotoPath();
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 从相册获取图片
     */
    public static void getByAlbum(boolean iscrop) {
        crop = iscrop;
        Intent intentxiangce = new Intent(Intent.ACTION_PICK, null);
        intentxiangce.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        activity.startActivityForResult(intentxiangce, GET_BY_ALBUM);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, GET_BY_CROP);
    }
//    File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
//    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
//    Uri outputUri = FileProvider.getUriForFile(context, "com.jph.takephoto.fileprovider",file);
//    Uri imageUri=FileProvider.getUriForFile(context, "com.jph.takephoto.fileprovider", new File("/storage/emulated/0/temp/1474960080319.jpg");//通过FileProvider创建一个content类型的Uri
//    Intent intent = new Intent("com.android.camera.action.CROP");
//    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//    intent.setDataAndType(imageUri, "image/*");
//    intent.putExtra("crop", "true");
//    intent.putExtra("aspectX", 1);
//    intent.putExtra("aspectY", 1);
//    intent.putExtra("scale", true);
//    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//    intent.putExtra("noFaceDetection", true); // no face detection
//    startActivityForResult(intent,1008);
    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private static void setPicToView(Intent picdata, ImageView image, Map<String,File> map, String key) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Bitmap bitmap = PicturesOperation.comp(photo);
            try {
                PicturesOperation.saveMyBitmap("imageviewimage", bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String imgFilePath = Environment.getExternalStorageDirectory().toString()
                    + "/imageviewimage.png";
            // //把得到的图片绑定在控件上显示
            image.setImageBitmap(bitmap);
            if (map != null) {
                map.put(key, new File(DeUtils.getCacheUrl(imgFilePath)));
            }

        }
    }

    /**
     * @param requestCode 请求码
     * @param resultCode  返回结果码
     * @param data        返回数据
     * @param imageView   显示图片的控件
     * @param map         存放图片的map集合
     * @param key         存放图片map集合对应的key值
     */

    public static void onActivityResult(int requestCode, int resultCode, Intent data, ImageView imageView, Map<String,File> map, String key) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_BY_ALBUM) { ///选择从图库中的照片
                ContentResolver resolver = activity.getContentResolver();
                try {
                    if (crop) {//是否剪切
                        startPhotoZoom(data.getData());
                    } else {
                        // 获得图片的uri
                        Uri originalUri = data.getData();
                        // 将图片内容解析成字节数组
                        mContent = readStream(resolver.openInputStream(Uri
                                .parse(originalUri.toString())));
                        // 将字节数组转换为ImageView可调用的Bitmap对象
                        Bitmap myBitmap = getPicFromBytes(mContent, null);
                        if (map != null) {
                            try {
                                PicturesOperation.saveMyBitmap("imageviewimage", myBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final String imgFilePath = Environment.getExternalStorageDirectory().toString()
                                    + "/imageviewimage.png";
                            // //把得到的图片绑定在控件上显示
                            imageView.setImageBitmap(myBitmap);
                            if (map != null) {
                                map.put(key, new File(DeUtils.getCacheUrl(imgFilePath)));
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GET_BY_CROP)//// 取得裁剪后的图片
            {
                if (data != null) {
                    setPicToView(data, imageView, map, key);
                }
            } else {///选择从相机拍照的照片
                if (!TextUtils.isEmpty(path)) {
                    if (crop) {
                        startPhotoZoom(FileProvider.getUriForFile(activity, "com.jph.takephoto.fileprovider",new File(path)));
                    } else {
                        Glide.with(activity)
                                .load(new File(path))
                                .centerCrop()
                                .thumbnail(0.1f)
                                .placeholder(R.drawable.ic_photo_black_48dp)
                                .error(R.drawable.ic_broken_image_black_48dp)
                                .into(imageView);
//                   ImageDisplayer.getInstance(activity).displayBmp(imageView, null, path);//在空件中显示图片
                        if (map != null) {
                            map.put(key, new File(DeUtils.getCacheUrl(path)));//将压缩后的图片放入map中
                        }
                    }

                } else {
                    Toast.makeText(activity, "请重新拍摄", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private static Bitmap getPicFromBytes(byte[] bytes,
                                          BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
}
