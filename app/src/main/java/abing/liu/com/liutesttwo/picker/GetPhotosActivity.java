package abing.liu.com.liutesttwo.picker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abing.liu.com.liutesttwo.R;
import abing.liu.com.liutesttwo.pickerone.PhotoAdapter;
import me.iwf.liu.utils.OnlyCameraUtil;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.RequestPermissionsUtil;

/**
 * 项目名称：LiuTestTwo
 * 类描述：11111111
 * 创建人：liubing
 * 创建时间：2016/11/19 12:23
 * 修改人：Administrator
 * 修改时间：2016/11/19 12:23
 * 修改备注：
 */
public class GetPhotosActivity extends Activity {

    private ImageView imageView;
    private Map<String, File> map = new HashMap<String, File>();
    private OnlyCameraUtil onlyCameraUtil;


    public final static int REQUEST_CODE = 11;
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;

    ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getphoto);
        imageView = (ImageView) findViewById(R.id.image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
        onlyCameraUtil = new OnlyCameraUtil(GetPhotosActivity.this);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() { ///压缩回调反应较慢
            @Override
            public void onClick(View v) {

                if (RequestPermissionsUtil.requestPer(GetPhotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 2)) {
                    onlyCameraUtil.getByAlbum(false);
                } else {
                    promptNoPermission(R.string.no_permission_gallery);
                }
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestPermissionsUtil.requestPer(GetPhotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 2)) {
                    onlyCameraUtil.getByAlbum(true);
                } else {
                    promptNoPermission(R.string.no_permission_gallery);
                }
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RequestPermissionsUtil.requestPer(GetPhotosActivity.this, Manifest.permission.CAMERA, 1)) {
                    onlyCameraUtil.takePhoto(1, false);
                } else {
                    promptNoPermission(R.string.no_permission_camera);
                }
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RequestPermissionsUtil.requestPer(GetPhotosActivity.this, Manifest.permission.CAMERA, 2)) {
                    onlyCameraUtil.takePhoto(1, true);
                } else {
                    promptNoPermission(R.string.no_permission_camera);
                }
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (RequestPermissionsUtil.requestPer(GetPhotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 1)) {
//                    PhotoPickerIntent intent = new PhotoPickerIntent(GetPhotosActivity.this);
//                    intent.setPhotoCount(1);
//                    intent.setShowCamera(true);
//                    startActivityForResult(intent, REQUEST_CODE);
//                } else {
//                    promptNoPermission(me.iwf.photopicker.R.string.no_permission_gallery);
//                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 801:
                onlyCameraUtil.onActivityResult(requestCode, resultCode, data, imageView, map, "111");
                break;
            case 802:
                onlyCameraUtil.onActivityResult(requestCode, resultCode, data, imageView, map, "333");
                break;
            case 1:
                onlyCameraUtil.onActivityResult(requestCode, resultCode, data, imageView, map, "22222222");
                break;
            case 11:
//                List<String> photos = null;
//                if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//                    if (data != null) {
//                        photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
//                    }
//                    selectedPhotos.clear();
//
//                    if (photos != null) {
//
//                        selectedPhotos.addAll(photos);
//                    }
//                    photoAdapter.notifyDataSetChanged();
//                }
                break;
        }

        Log.e("111111111111", map.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                RequestPermissionsUtil.onRequestPermissionsResult(GetPhotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, requestCode, permissions, grantResults);
                break;
            case 2:
                RequestPermissionsUtil.onRequestPermissionsResult(GetPhotosActivity.this, Manifest.permission.CAMERA, requestCode, permissions, grantResults);
                break;
        }

    }

    private void promptNoPermission(@StringRes int res) {
        Snackbar.make(findViewById(android.R.id.content), res, Snackbar.LENGTH_LONG).setAction(R.string.btn_setting, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }).show();
    }
}
