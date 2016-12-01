package me.iwf.liu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 项目名称：BingYao
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/8/29 17:23
 * 修改人：Administrator
 * 修改时间：2016/8/29 17:23
 * 修改备注：
 */
public class PicturesOperation {

//    android图片压缩的3种方法实例
//
//    作者： 字体：[增加 减小] 类型：转载 时间：2013-09-04 我要评论
//
//
//    这篇文章介绍了android图片压缩的3种方法实例，有需要的朋友可以参考一下
//
//            ..
//
//
//    android 图片压缩方法：
//
//    第一：质量压缩法：
//
//
//    复制代码 代码如下:


    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 50) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            Log.e("options", options + "-----" + baos.toByteArray().length / 1024);
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


//    第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
//
//
//    复制代码 代码如下:


    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }


//    图片比例压缩时， 我看到一个算法，说比较快。。
//    be = (int) ((w / STANDARD_WIDTH + h/ STANDARD_HEIGHT) / 2);
//    结论二：图片比例压缩倍数 就是 （宽度压缩倍数+高度压缩倍数）/2..
//
//
//    第三：图片按比例大小压缩方法（根据Bitmap图片压缩）：
//
//
//    复制代码 代码如下:


    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 50, baos);
        if (baos.toByteArray().length / 1024 > 50) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.PNG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    // 保存图片        Tools.saveBitmap(getApplicationContext().getFilesDir().getAbsolutePath(), "xiaoma.png", photo);
//                 String imgFilePath = Environment.getExternalStorageDirectory().toString()
//                   + "/xiaoma.png";
//    Bitmap bitmap=BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getAbsolutePath()+"/xiaoma.png");
//    public static void saveBitmap(String path, String picName, Bitmap bm) {
//        File f = new File(path, picName);
//        if (f.exists()) {
//            f.delete();
//        }
//        try {
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//        } catch (FileNotFoundException e) {
//
//            e.printStackTrace();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//    }


    //保存图片到本地
    //  Bitmap bitmap= PicturesOperation.comp(photo);
//    try {
//        PicturesOperation.saveMyBitmap("parkheader",bitmap);
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
    // 根据图片的名称获取图片
//    String imgFilePath = Environment.getExternalStorageDirectory().toString()
//            + "/parkheader.png";
    public static void saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {
        File f = new File("/sdcard/" + bitName + ".png");
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
