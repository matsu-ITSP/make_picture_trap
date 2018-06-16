package jp.ac.titech.itpro.sdl.activitytest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FlickListenActivity extends AppCompatActivity
        implements View.OnClickListener {
    private final static String TAG = "FlickListenActivity";
    public final static String NAME_EXTRA = "name";

    private EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_nameinput);
        TextView tagText = findViewById(R.id.input_tag_text);
        tagText.setText(TAG);
        nameInput = findViewById(R.id.name_input);
        Button okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
        case R.id.ok_button:
            String name = nameInput.getText().toString().trim();
            if (name.length() > 0) {
                Intent data = new Intent();
                data.putExtra(NAME_EXTRA, name);
                setResult(RESULT_OK, data);
            }
            else {
                setResult(RESULT_CANCELED);
            }
            finish();
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
