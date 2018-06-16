package jp.ac.titech.itpro.sdl.activitytest2;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MotionEvent;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private TextView textView;
    private ImageView imageView;
    private final static String TAG="MainActivity";

    private static final int MIN_MOVE = 50;
    private static final int MIN_VELOCITY = 250;
    private static final int MAX_MOVE = 200;

    private GestureDetector myGesDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        /*やること
        * フリックリスナーの実装
        * フリックされたら別画面起動、そっちにフリックのデータと画像データ渡す
        * フリックデータに応じて画像編集
        * 編集した画像表示
        * 別画面で保存ボタン、戻るボタン
        * 別画面側で編集した画像を保存して別画面終了
        * 戻るボタンで保存せずに別画面終了
        *
        * */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.answer_text);

        imageView = findViewById(R.id.image_view);

        Button button = findViewById(R.id.go_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
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

                    pfDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    if(pfDescriptor != null){
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        imageView.setImageBitmap(bmp);
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
            Log.d(TAG,"onFling");
            try {

                // 移動距離・スピードを出力
                float distance_x = ((event1.getX() - event2.getX()));
                float velocity_x = (velocityX);
                float distance_y = ((event1.getY() - event2.getY()));
                float velocity_y = (velocityY);
                textView.setText("distance:(" + distance_x + "," + distance_y + " )speed:(" + velocity_x + "," + velocity_y +")");
                /*
                // Y軸の移動距離が大きすぎる場合
                if (Math.abs(event1.getY() - event2.getY()) > MAX_MOVE) {
                    textView.setText("縦の移動距離が大きすぎ");
                }
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if  (event1.getX() - event2.getX() > MIN_MOVE && Math.abs(velocityX) > MIN_VELOCITY) {
                    textView.setText("右から左");
                }
                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event2.getX() - event1.getX() > MIN_MOVE && Math.abs(velocityX) > MIN_VELOCITY) {
                    textView.setText("左から右");
                }
                */

            } catch (Exception e) {
                // TODO
            }

            return false;
        }
    };


}