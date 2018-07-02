package jp.ac.titech.itpro.sdl.activitytest2;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.content.*;
import android.widget.Toast;
import android.support.v8.renderscript.*;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;



import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private TextView textView;
    private ImageView imageView;
    private final static String TAG="MainActivity";

    private static final int MIN_MOVE = 50;
    private static final int MIN_VELOCITY = 250;
    private static final int MAX_MOVE = 200;
    private final int REQUEST_PERMISSION = 1000;

    private GestureDetector myGesDetect;
    boolean isFlick = false;
    Uri originalImage;
    RenderScript rs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        /*やること
        * フリックリスナーの実装:ok
        * フリックされたら別画面起動、そっちにフリックのデータと画像データ渡す:やめた
        * フリックデータに応じて画像編集
        * 編集した画像表示:ok
        * 別画面で保存ボタン、戻るボタン:ok
        * 別画面側で編集した画像を保存して別画面終了:ok
        * 戻るボタンで保存せずに別画面終了:ok
        *
        * */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rs = RenderScript.create(this);

        textView = findViewById(R.id.answer_text);
        imageView = findViewById(R.id.image_view);

        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }

        //Log.d("debug","filePath="+filePath);

        Button go_button = findViewById(R.id.go_button);
        go_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"onClick_select");
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

            }
        });
        Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"onClick_back");
                ParcelFileDescriptor pfDescriptor = null;
                try{
                    Uri uri = originalImage;

                    pfDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    if(pfDescriptor != null){
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        imageView.setImageBitmap(bmp);
                        toFlick();//フリック受付状態に
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try{
                        if(pfDescriptor != null){
                            pfDescriptor.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)

            public void onClick(View v) {
                Log.d(TAG,"onClick_save");

                // 画像を置く外部ストレージのパスを設定
                //todo:タイトルをyakudo_(今日の日付).jpgにする
                //Date dt = new Date();
                //SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String fileName = "poi.jpg";
                String filePath = Environment.getExternalStorageDirectory().getPath()
                        + "/DCIM/Camera/"+fileName;
                File file = new File(filePath);
                file.getParentFile().mkdir();
                Log.d(TAG,"addPath:"+filePath);
                FileOutputStream output = null;

                try {
                    // openFileOutputはContextのメソッドなのでActivity内ならばthisでOK
                    //output = openFileOutput(fileName, Context.MODE_PRIVATE);
                    output = new FileOutputStream(filePath);
                    ((BitmapDrawable)imageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, output);
                    Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_LONG).show();
                    output.close();
                } catch (IOException e) {
                    // エラー処理
                    Toast.makeText(getApplicationContext(),"保存失敗",Toast.LENGTH_LONG).show();
                } finally {
                    if(output != null) {
                        try {
                            output.close();
                        }catch (IOException e) {

                        }
                    }
                }
                registerDatabase(filePath);
                toSelect();
            }
        });
        myGesDetect = new GestureDetector(this,myOnGesListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d(TAG,"onActivityResult");
        //画像選択時のactivity
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            if(resultData.getData() != null){

                ParcelFileDescriptor pfDescriptor = null;
                try{
                    Uri uri = resultData.getData();
                    originalImage = uri;

                    pfDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    if(pfDescriptor != null){
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        imageView.setImageBitmap(bmp);
                        toFlick();//フリック受付状態に
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try{
                        if(pfDescriptor != null){
                            pfDescriptor.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        return myGesDetect.onTouchEvent(e);
    }

    private final GestureDetector.SimpleOnGestureListener myOnGesListener = new GestureDetector.SimpleOnGestureListener() {
        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if(!isFlick){
                return false;
            }
            Log.d(TAG,"onFling");
            try {

                // 移動距離・スピードを出力
                float distance_x = ((event1.getX() - event2.getX()));
                float velocity_x = (velocityX);
                float distance_y = ((event1.getY() - event2.getY()));
                float velocity_y = (velocityY);
                textView.setText("distance:(" + distance_x + "," + distance_y + " )speed:(" + velocity_x + "," + velocity_y +")");

                editImage(velocity_x,velocity_y,event1.getX(),event1.getY());
                toSave();

            } catch (Exception e) {
                // TODO
            }

            return false;
        }
    };
    private void editImage(float vx,float vy,float px,float py){
        //引用:http://java-lang-programming.com/ja/articles/80
        //グレースケールに変える、躍動するのはまだ
        //todo:躍動させる
        Bitmap mutableBitmap = getMutableBitmap();

        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();

        Allocation alloc = Allocation.createFromBitmap(rs,mutableBitmap);//アロケーションにデータを入れる
        //ScriptC_invert invert = new ScriptC_invert(rs);
        ScriptC_saturation scr = new ScriptC_saturation(rs);
        scr.set_saturationValue(1000);
        scr.forEach_saturation(alloc,alloc);

        //ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs,Element.RGBA_8888(rs));
        //blur.setInput(alloc);
        //blur.forEach(alloc);
        alloc.copyTo(mutableBitmap);

        //Log.d("Main_where heavy","計算開始");
        //計算がとても重い、描画は大したことない
        //計算をGPUで行うべき,並列化したい！
        /*
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {

                // Returns the Color at the specified location.
                int pixel = mutableBitmap.getPixel(i, j);

                int red = 0;
                int green = 0;
                int blue = 0;

                int average = (red + green + blue) / 3;
                int gray_rgb = Color.rgb(average, average, average);

                mutableBitmap.setPixel(i, j, gray_rgb);
            }
        }
        */
        /*
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // Returns the Color at the specified location.
                int pixel = mutableBitmap.getPixel(i, j);

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                int average = (red + green + blue) / 3;
                int gray_rgb = Color.rgb(average, average, average);

                mutableBitmap.setPixel(i, j, gray_rgb);
            }
        }
        */
        //Log.d("Main_where heavy","計算終了");
        imageView.setImageBitmap(mutableBitmap);
        //Log.d("Main_where heavy","描画終了");
    }
    private Bitmap getMutableBitmap() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // 変更可能なbitmap
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return mutableBitmap;
    }
    public void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            //setUpWriteExternalStorage();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);
        }
    }
    private void registerDatabase(String file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
    }
    //状態遷移関数
    private void toSelect(){
        Log.d(TAG,"toSelect");
        View go_button = findViewById(R.id.go_button);
        go_button.setClickable(true);
        View back = findViewById(R.id.back_button);
        back.setClickable(false);
        View save = findViewById(R.id.save_button);
        save.setClickable(false);
        isFlick = false;
        //Selectまで戻るなら画像も消えるよね
        ImageView image = findViewById(R.id.image_view);
        image.setImageDrawable(null);
    }
    private void toFlick(){
        Log.d(TAG,"toFlick");
        View go_button = findViewById(R.id.go_button);
        go_button.setClickable(true);
        View back = findViewById(R.id.back_button);
        back.setClickable(false);
        View save = findViewById(R.id.save_button);
        save.setClickable(false);
        isFlick = true;
    }
    private void toSave(){
        Log.d(TAG,"toSave");
        View go_button = findViewById(R.id.go_button);
        go_button.setClickable(false);
        View back = findViewById(R.id.back_button);
        back.setClickable(true);
        View save = findViewById(R.id.save_button);
        save.setClickable(true);
        isFlick = false;
    }

}