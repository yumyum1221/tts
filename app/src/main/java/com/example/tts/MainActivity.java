package com.example.tts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button cpvBtn;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appExecutors = new AppExecutors();

        SharedPreferences sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        final String clientId = sharedPref.getString("application_client_id", "");
        final String clientSecret = sharedPref.getString("application_client_secret", "");

        cpvBtn = (Button) findViewById(R.id.btn_cpv);
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
                    text = "네이버 클라우드 플랫폼에서는 Clova, papago 등 네이버의 다양한 인공지능 서비스를 API 형태로 제공합니다.";
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