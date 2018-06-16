package jp.ac.titech.itpro.sdl.activitytest2;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private TextView textView;
    private ImageView imageView;

    private static final int MIN_MOVE = 50;
    private static final int MIN_VELOCITY = 250;
    private static final int MAX_MOVE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //textView = findViewById(R.id.answer_text);

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

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

}