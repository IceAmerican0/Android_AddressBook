package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adressbook.Bean.AddressInfo;
import com.example.adressbook.NetworkTask.NetworkTask;
import com.example.adressbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemberUpdateActivity extends AppCompatActivity {

    String urlAddr="http://192.168.145.42:8080/address/MemberUpdateReturn.jsp";

    TextView back,save,nameCount,commentCount,numberCount;
    EditText ename,enumber,ecomment,ecategory;
    ImageView imageView;
    String sname,snumber,scomment,scategory,scode;
    String simage=null;

    ArrayList<AddressInfo> member;

    private final int REQ_CODE_SELECT_IMAGE = 300; // Gallery Return Code
    private String img_path = null; // 최종 file name
    private String f_ext = null;    // 최종 file extension
    File tempSelectFile;

    String devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.exampleaddressbook/";
    String imageJspAddr = "http://192.168.145.42:8080/address/multipartRequest.jsp";

    int mSelect=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_update);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //==============선언====================
        back=findViewById(R.id.update_btnBack);
        save=findViewById(R.id.update_btnSave);
        ename=findViewById(R.id.update_eName);
        enumber=findViewById(R.id.update_eNumber);
        ecategory=findViewById(R.id.update_eCategory);
        ecomment=findViewById(R.id.update_eComment);
        numberCount=findViewById(R.id.update_numberCount);
        nameCount=findViewById(R.id.update_nameCount);
        commentCount=findViewById(R.id.update_commentCount);
        imageView=findViewById(R.id.update_btnImage);

        //내용 불러오기
        Intent intent=getIntent();
        sname=intent.getStringExtra("name");
        snumber=intent.getStringExtra("number");
        scomment=intent.getStringExtra("comment");
        scategory=intent.getStringExtra("category");
        simage=intent.getStringExtra("image");
        scode=intent.getStringExtra("code");

        //입력 자릿수 제한
        ename.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        enumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        ecomment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        ename.setText(sname);
        enumber.setText(snumber);
        if(scomment.equals("null")) ecomment.setText("");
        else ecomment.setText(scomment);
        if(scategory.equals("null")) ecategory.setText("");
        else ecategory.setText(scategory);

        nameCount.setText(ename.length()+" / 20");
        numberCount.setText(enumber.length()+" / 11");
        commentCount.setText(ecomment.length()+" / 50");

        //입력시 글자수 세기
        ename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameCount.setText(s.length()+" / 20");
                check();
            }
        });

        enumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                numberCount.setText(s.length()+" / 11");
                check();
            }
        });

        ecomment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                commentCount.setText(s.length()+" / 50");
                check();
            }
        });



        back.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        ecomment.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);
        ecategory.setOnClickListener(onClickListener);
    }//====================onCreate===================

    //이름 및 전화번호 입력 안돼있을시 저장버튼 비활성
    public void check() {
            if(!ename.getText().toString().equals(sname) || !enumber.getText().toString().equals(snumber)) {
                if (ename.getText().toString().trim().length()!=0 && enumber.getText().toString().trim().length()!=0) {
                save.setEnabled(true);
                save.setTextColor(Color.parseColor("#2e4bf2"));
            }
        }
            if(ename.getText().toString().equals(sname) && enumber.getText().toString().equals(snumber)) {
                if(ename.getText().toString().trim().length()==0 || enumber.getText().toString().trim().length()<=8) {
                save.setEnabled(false);
                save.setTextColor(Color.parseColor("#ced4da"));
            }
        }
    }


    //========================버튼 클릭 처리=======================
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.update_btnBack: //뒤로가기
                    finish();
                    break;
                case R.id.update_btnSave: //저장하기
                    ModifyAction();
                    break;
                case R.id.update_eCategory: //그룹정하기
                    new AlertDialog.Builder(MemberUpdateActivity.this)
                            .setTitle("그룹을 선택하세요")
                            .setIcon(R.drawable.group)
                            .setSingleChoiceItems(R.array.group, mSelect,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mSelect=which;
                                        }
                                    })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] group=getResources().getStringArray(R.array.group);
                                    EditText editText=findViewById(R.id.add_eCategory);
                                    editText.setText(group[mSelect]);
                                }
                            })
                            .setNegativeButton("취소",null)
                            .show();
                    break;
                case R.id.update_btnImage:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                    break;
            }
        }
    };

    //========================입력 정보 넣기===========================
    private void ModifyAction(){
        sname=ename.getText().toString();
        snumber=enumber.getText().toString();

        if(ecomment.getText().toString().trim().length()==0) scomment=null;
        else scomment=ecomment.getText().toString();

        if(ecategory.getText().toString().trim().length()==0) scategory=null;
        else scategory=ecategory.getText().toString();

       simage=null;
        Log.v("message",sname+"//"+snumber+"//"+scomment+"//"+scategory+"//"+simage+"//"+scode);
        urlAddr=urlAddr+"?name="+sname+"&number="+snumber+"&comment="+scomment+"&image="+simage+"&category="+scategory+"&code="+scode;

        Log.v("message",urlAddr);

        String result=connectUpdateData();
        Log.v("message",result);
//        if(result.equals("1")){
//            connectSelectData();
//
//            ename.setText(member.get(0).getName());
//            enumber.setText(member.get(0).getNumber());
//            if(member.get(0).getComment().equals("null")) ecomment.setText("");
//            else ecomment.setText(member.get(0).getComment());
//            if(member.get(0).getCategory().equals("null")) ecategory.setText("");
//            else ecategory.setText(member.get(0).getCategory());

            Toast.makeText(MemberUpdateActivity.this,sname+"님의 정보가 수정되었습니다!",Toast.LENGTH_SHORT).show();
//
//        }else{
//            Toast.makeText(MemberUpdateActivity.this,sname +"수정 실패!",Toast.LENGTH_SHORT).show();
//        }

    }

    private String connectUpdateData(){ //업데이트 jsp 실행
        String result=null;
        try{
            NetworkTask networkTask=new NetworkTask(MemberUpdateActivity.this,urlAddr,"insert");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private String connectSelectData(){ //select jsp 실행
        String result=null;
        String urlAddr="http://192.168.145.42:8080/address/MemberInfoReturn.jsp?code="+scode;
        try{
            NetworkTask networkTask=new NetworkTask(MemberUpdateActivity.this,urlAddr,"select");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }


    //Photo App.에서 Image 선택후 작업내용
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                //이미지의 URI를 얻어 경로값으로 반환.
                img_path = getImagePathToUri(data.getData());;

                //이미지를 비트맵형식으로 반환
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                Bitmap image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
                imageView.setImageBitmap(image_bitmap_copy);

                // 파일 이름 및 경로 바꾸기(임시 저장, 경로는 임의로 지정 가능)
                String date = new SimpleDateFormat("yyyyMMddHmsS").format(new Date());
                String imageName = date + "." + f_ext;
                tempSelectFile = new File(devicePath , imageName);
                OutputStream out = new FileOutputStream(tempSelectFile);
                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                // 임시 파일 경로로 위의 img_path 재정의
                img_path = devicePath + imageName;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //사용자가 선택한 이미지의 정보를 받아옴
    private String getImagePathToUri(Uri data) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);


        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        // 확장자 명 저장
        f_ext = imgPath.substring(imgPath.length()-3, imgPath.length());

        return imgPath;
    }
}//========================Activity======================