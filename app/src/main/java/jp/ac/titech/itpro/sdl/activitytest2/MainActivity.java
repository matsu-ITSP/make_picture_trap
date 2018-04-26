package jp.ac.titech.itpro.sdl.activitytest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private final static int REQ_INPUT = 1234;

    private TextView answerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        TextView tagText = findViewById(R.id.main_tag_text);
        tagText.setText(TAG);
        answerText = findViewById(R.id.answer_text);
        Button goButton = findViewById(R.id.go_button);
        goButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
        case R.id.go_button:
            Intent intent = new Intent(this, InputActivity.class);
            startActivityForResult(intent, REQ_INPUT);
            break;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        switch (reqCode) {
        case REQ_INPUT:
            if (resCode == RESULT_OK) {
                String name = data.getStringExtra(InputActivity.NAME_EXTRA);
                if (name != null && name.length() > 0) {
                    answerText.setText(getString(R.string.answer_text_format, name));
                }
            }
            else {
                answerText.setText(R.string.answer_text_default);
            }
            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
