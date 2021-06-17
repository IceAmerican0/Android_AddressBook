package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adressbook.Bean.AddressInfo;
import com.example.adressbook.NetworkTask.NetworkTask;
import com.example.adressbook.R;

import java.io.File;
import java.util.ArrayList;

public class MemberInfoActivity extends AppCompatActivity {
    String simage=null;
    String scomment=null;
    String scategory=null;

    TextView name,number,comment,category,back;
    String sname,snumber,scode;
    ImageButton edit,delete,call,message;
    WebView image;
    ArrayList<AddressInfo> member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        name=findViewById(R.id.info_name);
        number=findViewById(R.id.info_number);
        comment=findViewById(R.id.info_comment);
        category=findViewById(R.id.info_category);
        back=findViewById(R.id.info_btnBack);
        edit=findViewById(R.id.info_btnEdit);
        delete=findViewById(R.id.info_btnDelete);
        image=findViewById(R.id.info_img);
        call=findViewById(R.id.info_call);
        message=findViewById(R.id.info_message);


        back.setOnClickListener(onClickListener);
        edit.setOnClickListener(onClickListener);
        delete.setOnClickListener(onClickListener);
        call.setOnClickListener(onClickListener);
        message.setOnClickListener(onClickListener);

        Intent intent=getIntent();
        sname=intent.getStringExtra("name");
        snumber=intent.getStringExtra("number");
        scomment=intent.getStringExtra("comment");
        scategory=intent.getStringExtra("category");
        scode=intent.getStringExtra("code");
        simage=intent.getStringExtra("image");

        name.setText(sname);
        number.setText(snumber);
        if(scomment.equals("null")) comment.setText("");
        else comment.setText(scomment);
        if(scategory.equals("null")) category.setText("");
        else category.setText(scategory);

        if(simage.equals("null")){// 이미지가 비어있으면 기본이미지 아니면 지정 이미지 띄워주기
            image.loadDataWithBaseURL(null,
                    "<html><head></head> <body><img style = 'width: 100%; height:auto;' src=\"http://192.168.145.42:8080/image/user.jpg\"></body></html>",
                    "text/html","utf-8",null);
        }else {
            image.loadDataWithBaseURL(null,htmlData(simage),"text/html","utf-8",null);
        }
    }//================onCreate=================

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()){
                case R.id.info_btnBack: //뒤로가기
                    finish();
                    break;
                case R.id.info_btnDelete: //삭제하기
                    new AlertDialog.Builder(MemberInfoActivity.this)
                            .setMessage(sname+"님의 연락처를 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String result=connectDeleteData();
                                        finish();
                                        ((MainActivity)MainActivity.context).onResume(); //삭제 후 리스트 다시 불러오기
                                        Toast.makeText(MemberInfoActivity.this,sname+"님의 연락처가 삭제되었습니다!",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                    break;
                case R.id.info_btnEdit: // 편집하기
                    intent=new Intent(MemberInfoActivity.this,MemberUpdateActivity.class);
                    intent.putExtra("name",sname);
                    intent.putExtra("number",snumber);
                    intent.putExtra("comment",scomment);
                    intent.putExtra("category",scategory);
                    intent.putExtra("image",simage);
                    intent.putExtra("code",scode);
                    startActivity(intent);
                    break;
                case R.id.info_call: //전화 다이얼 띄우기
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+snumber));
                    startActivity(call);
                    break;
                case R.id.info_message: //문자 보내기 창띄우기
                    Uri smsUri = Uri.parse("smsto:" + snumber);
                    intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //        connectSelectData();
//
//        name.setText(member.get(0).getName());
//        number.setText(member.get(0).getNumber());
//        if(member.get(0).getComment().equals("null")) comment.setText("");
//        else comment.setText(member.get(0).getComment());
//        if(member.get(0).getCategory().equals("null")) category.setText("");
//        else category.setText(member.get(0).getCategory());
    }

    public String htmlData(String location){
        String htmlData="<html><head></head> <body><img style = 'width: 100%; height:auto;' src=\"http://192.168.145.42:8080/image/"+location+"\"></body></html>";

        return  htmlData;
    }

    private String connectDeleteData(){
        String result=null;
        String urlAddr="http://192.168.145.42:8080/address/MemberDeleteReturn.jsp?code="+scode;
        try{
            NetworkTask networkTask=new NetworkTask(MemberInfoActivity.this,urlAddr,"insert");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private String connectSelectData(){ //입력 jsp 실행
        String result=null;
        String urlAddr="http://192.168.145.42:8080/address/MemberInfoReturn.jsp?code="+scode;
        try{
            NetworkTask networkTask=new NetworkTask(MemberInfoActivity.this,urlAddr,"select");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }



}//====================Activity====================