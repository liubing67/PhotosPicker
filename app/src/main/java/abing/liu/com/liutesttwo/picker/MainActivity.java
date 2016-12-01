package abing.liu.com.liutesttwo.picker;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import abing.liu.com.liutesttwo.R;
import me.iwf.liu.utils.OnlyCameraUtil;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import me.iwf.photopicker.utils.RequestPermissionsUtil;

public class MainActivity extends ActionBarActivity {


    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;

    ArrayList<String> selectedPhotos = new ArrayList<>();

    public final static int REQUEST_CODE = 1;
    ImageView image;
    private OnlyCameraUtil onlyCameraUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        onlyCameraUtil=new OnlyCameraUtil(MainActivity.this);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        image= (ImageView) findViewById(R.id.image);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
//        intent.setPhotoCount(9);
//        startActivityForResult(intent, REQUEST_CODE);
                startActivity(new Intent(MainActivity.this, GetPhotosActivity.class));
            }
        });


        findViewById(R.id.button_no_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(7);
                intent.setShowCamera(false);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        findViewById(R.id.button_one_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onlyCameraUtil.getByAlbum();
                                if (RequestPermissionsUtil.requestPer(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 1)) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                    intent.setPhotoCount(1);
                    intent.setShowCamera(true);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    promptNoPermission(me.iwf.photopicker.R.string.no_permission_gallery);
                }
            }
        });
//        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, abing.liu.com.liutesttwo.MainActivity.class));
//            }
//        });

    }


    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode==1)
////        {
//            onlyCameraUtil.onActivityResult(requestCode,resultCode,data,image,null,null);
////        }

        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {

                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermissionsUtil.onRequestPermissionsResult(MainActivity.this, Manifest.permission.CAMERA, requestCode, permissions, grantResults);
    }

    private void promptNoPermission(@StringRes int res) {
        Snackbar.make(findViewById(android.R.id.content), res, Snackbar.LENGTH_LONG).setAction(me.iwf.photopicker.R.string.btn_setting, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }).show();
    }

//
}
