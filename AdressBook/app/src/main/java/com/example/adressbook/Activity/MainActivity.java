package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.adressbook.Adapter.MemberAdapter;
import com.example.adressbook.Bean.AddressInfo;
import com.example.adressbook.NetworkTask.NetworkTask;
import com.example.adressbook.R;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    String urlAddr="http://192.168.145.42:8080/address/";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ArrayList<AddressInfo> member;
    WebView webView;
    ImageButton search,add;
    EditText esearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //======================선언=====================

        webView=findViewById(R.id.member_image);
        recyclerView=findViewById(R.id.main_recycler);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        search=findViewById(R.id.main_search);
        add=findViewById(R.id.main_add);
        esearch=findViewById(R.id.main_eSearch);

        //=============================첫 실행 여부 확인 후 첫 실행시 설명페이지 띄우기=====================================
        SharedPreferences preferences=getSharedPreferences("First", Activity.MODE_PRIVATE);
        boolean first=preferences.getBoolean("First",false);
        if(first==false){
            //==========================초기 화면 및 로딩 중 표시=========================
            Intent intent1=new Intent(MainActivity.this,StartActivity.class);
            startActivity(intent1);
        }else{
            connectGetData();
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);


        add.setOnClickListener(onClickListener);

        esearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ssearch=esearch.getText().toString().trim();
                if(ssearch.length()==0) connectGetData();
                else searchAction(ssearch);

            }
        });



    }// ======================onCreate===================


    //============================연락처 리스트 불러오기=========================
    private void connectGetData(){
        try{
            NetworkTask networkTask=new NetworkTask(MainActivity.this,urlAddr+"member_query_all.jsp","select");
            Object obj=networkTask.execute().get();
            member=(ArrayList<AddressInfo>) obj;

            adapter=new MemberAdapter(MainActivity.this,R.layout.member_layout,member);
            recyclerView.setAdapter(adapter);
            

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // =====================페이지 켜질때마다 리스트 초기화=======================
    @Override
    public void onResume() {
        super.onResume();
        if(esearch.getText().toString().trim().length()==0) connectGetData();
        else searchAction(esearch.getText().toString().trim());
    }


    // ======================뒤로가기 버튼 처리=================================
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            finish();
        }
    }


    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()){
                case R.id.main_add:// 멤버 추가하기
                    intent=new Intent(MainActivity.this,MemberAddActivity.class);
                    intent.putExtra("IP",urlAddr+"MemberInsertReturn.jsp");
                    startActivity(intent);
                    break;
            }
        }
    };

    ////=======================검색================================================

    private void searchAction(String search){
        try{
            NetworkTask networkTask=new NetworkTask(MainActivity.this,urlAddr+"MemberSearch.jsp?name="+search,"select");
            Object obj=networkTask.execute().get();
            member=(ArrayList<AddressInfo>) obj;

            adapter=new MemberAdapter(MainActivity.this,R.layout.member_layout,member);
            recyclerView.setAdapter(adapter);


        }catch(Exception e){
            e.printStackTrace();
        }
    }





}// =========================MainActivity======================