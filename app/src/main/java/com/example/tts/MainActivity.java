package com.example.tts;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button cpvBtn;
    private AppExecutors appExecutors;
    final int PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appExecutors = new AppExecutors();

        SharedPreferences sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        final String clientId = sharedPref.getString("application_client_id", "");
        final String clientSecret = sharedPref.getString("application_client_secret", "");

        cpvBtn = (Button) findViewById(R.id.btn_cpv);

        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },PERMISSION);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
            }
        }

        cpvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText cpvInputText = (EditText) findViewById(R.id.text_input_cpv);
                String strText = cpvInputText.getText().toString();

                Spinner spinner = (Spinner)findViewById(R.id.spinner_cpv_lang);
                String selItem = spinner.getSelectedItem().toString();

                String[] splits = selItem.split("\\(");
                String speaker = splits[0];

                if (speaker.isEmpty() || speaker.equals("")){
                    //기본 값 설정
                    speaker = "nara";
                }
                executeNaverTTSTask(strText, speaker);
            }
        });

        Spinner s = (Spinner)findViewById(R.id.spinner_cpv_lang);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Spinner s = (Spinner)findViewById(R.id.spinner_cpv_lang);
                String lang = s.getSelectedItem().toString();
                String text = "";
                if (lang.contains("한국어")) {
                    text = "네이버";
                }else if (lang.contains("영어")) {
                    text = "NAVER CLOUD PLATFORM provides various AI services in API formats, such as Clova and Papago.";
                }else if (lang.contains("일본어")) {
                    text = "NAVER CLOUD PLATFORMは、ClovaやPapagoなどのAPIフォーマットでさまざまなAIサービスを提供します.";

                }else if (lang.contains("중국어")) {
                    text = "NAVER CLOUD PLATFORM以API格式提供各种AI服务，例如Clova和Papago.";

                }else if (lang.contains("스페인어")) {
                    text = "NAVER CLOUD PLATFORM proporciona varios servicios de AI en formatos API, como Clova y Papago.";

                }else {
                    text = "";
                }

                TextView textViewVersionInfo = (TextView) findViewById(R.id.text_input_cpv);
                textViewVersionInfo.setText(text);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    // 작업을 실행하는 메소드
    private void executeNaverTTSTask(final String text, final String speaker) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                CpvProc.main(text, speaker);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        TextView textViewResult = findViewById(R.id.text_view_result);
                        textViewResult.setText("작업완료");
                    }
                });
            }
        });
    }
}