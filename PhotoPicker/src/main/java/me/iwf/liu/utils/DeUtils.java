package me.iwf.liu.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DeUtils {

	/**
	 * 删除文件夹
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if(file.isDirectory()){
			File[] childFiles = file.listFiles();

			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for(File f:childFiles){
				deleteFile(f);
			}
			file.delete();
		}
	}
	/**
	 * 根据传入的Url获取压缩后的图片的cacheUrl路径
	 * @param bitMapUrl
	 * @return
	 */
	public static String getCacheUrl(String bitMapUrl){
		Bitmap photo = getCompressBitmap(bitMapUrl,480,800);
		String cacheurl=getFileUrl(photo,true);
		return cacheurl;
	}
	public static String getCacheUrl(String bitMapUrl,Context context){
		Bitmap photo = getCompressBitmap(bitMapUrl,480,800);
		String cacheurl=getFileUrl(photo,true,context);
		return cacheurl;
	}


	/**
	 * 根据传入的Bitmap获取压缩后的图片的cacheUrl路径
	 * @param
	 * @return
	 */
	public static String getFileUrl(Bitmap photo,boolean tab) {
		String cacheFile = getCacheName();
		try {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			if(tab){
				photo.compress(CompressFormat.JPEG,60, fos);
			}else{
				photo.compress(CompressFormat.JPEG,100, fos);
			}

			fos.flush();
			fos.close();
			if(photo!=null&&!photo.isRecycled()){
				photo.recycle();
				photo=null;
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cacheFile;
	}
	public static String getFileUrl(Bitmap photo,boolean tab, Context context) {
		String cacheFile = getCacheName(context);
		try {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			if(tab){
				photo.compress(CompressFormat.JPEG,60, fos);
			}else{
				photo.compress(CompressFormat.JPEG,100, fos);
			}

			fos.flush();
			fos.close();
			if(photo!=null&&!photo.isRecycled()){
				photo.recycle();
				photo=null;
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cacheFile;
	}
	/**
	 * 创建一个用于暂存压缩后图片的Url地址
	 * @return
	 */
	public static String getCacheName() {
		//获取系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//用系统时间为图片命名
		String name = formatter.format(System.currentTimeMillis()) + ".png";

		String pathUrl =getCompressImageName();

		File newfile = new File(pathUrl);
		//如果文件不存在则创建文件
		if (!newfile.exists() && !newfile.isDirectory()){
			newfile.mkdirs();// 创建文件夹
		}
		String cacheFile=pathUrl+"/"+name;
		return cacheFile;
	}
	public static String getCacheName(Context context) {
		//获取系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//用系统时间为图片命名
		String name = formatter.format(System.currentTimeMillis()) + ".png";

		String pathUrl =getCompressImageName(context);

		File newfile = new File(pathUrl);
		//如果文件不存在则创建文件
		if (!newfile.exists() && !newfile.isDirectory()){
			newfile.mkdirs();// 创建文件夹
		}
		String cacheFile=pathUrl+"/"+name;
		return cacheFile;
	}
	public static String getCompressImageName(){
		String pathUrl = Environment.getExternalStorageDirectory()+"/hbguard/image/";
		return pathUrl;
	}
	public static String getCompressImageName(Context context){
		String pathUrl = context.getExternalCacheDir().getAbsolutePath()+"/hbguard/image/";
		return pathUrl;
	}
	public static String getPhotoImageName(){
		String pathUrl = Environment.getExternalStorageDirectory()+"/hbguard/cacheImage/";
		return pathUrl;
	}
	/**
	 * 获取一个指定比例的bitmap
	 * @param filePath
	 * @param height
	 * @param width
	 * @return
	 */
	private static Bitmap getCompressBitmap(String filePath, int height, int width) {
		Bitmap bitmap = null;
		int scallType = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(filePath);
			int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			int rotate = 0;
			switch (result) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				default:
					break;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(filePath, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, width, height);

			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			boolean ISOOMFRIST=true;
			while (ISOOMFRIST) {
				try {
					bitmap = BitmapFactory.decodeFile(filePath, options);
					ISOOMFRIST=false;
				} catch (OutOfMemoryError e) {
					options.inSampleSize = options.inSampleSize + 1;
					ISOOMFRIST = true;
				}
			}

			if (rotate > 0) {
				Matrix matrix = new Matrix();
				matrix.setRotate(rotate);
				boolean ISOOM = true;
				while (ISOOM) {
					try {
						Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);
						if (rotateBitmap != null) {
							bitmap.recycle();
							bitmap = rotateBitmap;
						}
						ISOOM = false;
					} catch (OutOfMemoryError e) {
						options.inSampleSize = options.inSampleSize + 1;
						ISOOM = true;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	/**
	 * 获取采样率
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			if(inSampleSize<1){
				inSampleSize=1;
			}
		}

		return inSampleSize;
	}
	//获取当前acitivyt的截图
	@SuppressWarnings("deprecation")
	public static Bitmap getWindowBitMap(Activity activity) {
		// 获取windows中最顶层的view
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();

		// 获取状态栏高度
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int statusBarHeights = rect.top;
		Display display = activity.getWindowManager().getDefaultDisplay();

		// 获取屏幕宽和高
		int widths = display.getWidth();
		int heights = display.getHeight();

		// 允许当前窗口保存缓存信息
		view.setDrawingCacheEnabled(true);
		Log.i("heights", heights+"");
		Log.i("statusBarHeights", statusBarHeights+"");
		Log.i("widths", widths+"");
		// 去掉状态栏
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
				statusBarHeights, widths, heights - statusBarHeights);
		// 销毁缓存信息
		view.destroyDrawingCache();

		return bmp;
	}

	public static void scrollToBottom(final ScrollView scroll, final View inner) {
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset =scroll.getChildAt(0).getMeasuredHeight() - inner.getHeight();
	/*	Log.i("dldl",scroll.getChildAt(0).getMeasuredHeight()+"");
		Log.i("dldl",inner.getHeight()+"");*/
				if (offset < 0) {
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}

	public static Bitmap getSmallBitmap(View v){
		v.buildDrawingCache();
		v.setDrawingCacheEnabled(true);

		Bitmap bitmap=Bitmap.createBitmap(v.getDrawingCache());
		v.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * 存放照相机拍照的相片
	 */
	public static String getCameraName() {
		//获取系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//用系统时间为图片命名
		String name = formatter.format(System.currentTimeMillis()) + ".png";

		String pathUrl =getPhotoImageName();

		File newfile = new File(pathUrl);
		//如果文件不存在则创建文件
		if (!newfile.exists() && !newfile.isDirectory()){
			newfile.mkdirs();// 创建文件夹
		}
		String cacheFile=pathUrl+"/"+name;
		return cacheFile;
	}

	/**
	 * 存apk
	 */
	public static String getApkFileName() {

		String pathUrl = Environment.getExternalStorageDirectory()+"/hbguard/";

		File newfile = new File(pathUrl);
		//如果文件不存在则创建文件
		if (!newfile.exists() && !newfile.isDirectory()){
			newfile.mkdirs();// 创建文件夹
		}
		String cacheFile=pathUrl+"hbapk/";
		return cacheFile;
	}

	/*public static void appearViewAnim(final View v,int hight,Context context) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		v.getLayoutParams().height=0;
		v.setVisibility(View.VISIBLE);
		final int mHight=DpOrPx.dip2px(context, hight);
		Animation animation=new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height= (interpolatedTime==1)?mHight:(int)(mHight*interpolatedTime);
				v.requestLayout();
			}
			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		animation.setDuration(800);
		v.startAnimation(animation);
	};*/


}
