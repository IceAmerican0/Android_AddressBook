package com.example.adressbook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adressbook.NetworkTask.ImageNetworkTask;
import com.example.adressbook.NetworkTask.NetworkTask;
import com.example.adressbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MemberAddActivity extends AppCompatActivity {
    String urlAddr=null;
    TextView back,save,nameCount,commentCount,numberCount;
    EditText ename,enumber,ecomment,ecategory;
    ImageView img;
    String sname,snumber,scomment,scategory;
    String simage=null;

    final static String TAG = "MemberAddActivity";

    private final int REQ_CODE_SELECT_IMAGE = 300; // Gallery Return Code
    private String img_path = null; // 최종 file name
    private String f_ext = null;    // 최종 file extension
    private String imageName;
    File tempSelectFile;

    int mSelect=0;

    String devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.addressbook/";
    String imageJspAddr = "http://192.168.145.42:8080/address/multipartRequest.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //==============선언====================
        back=findViewById(R.id.add_btnBack);
        save=findViewById(R.id.add_btnSave);
        ename=findViewById(R.id.add_eName);
        enumber=findViewById(R.id.add_eNumber);
        ecategory=findViewById(R.id.add_eCategory);
        ecomment=findViewById(R.id.add_eComment);
        numberCount=findViewById(R.id.add_numberCount);
        nameCount=findViewById(R.id.add_nameCount);
        commentCount=findViewById(R.id.add_commentCount);
        img=findViewById(R.id.add_btnImage);

        //ip주소 불러오기
        Intent intent=getIntent();
        urlAddr=intent.getStringExtra("IP");

        //입력 자릿수 제한
        ename.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        enumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        ecomment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

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
        ecategory.setOnClickListener(onClickListener);
        img.setOnClickListener(onClickListener);
    }//====================onCreate===================

    //이름 및 전화번호 입력 안돼있을시 저장버튼 비활성
    public void check() {
        if (ename.getText().toString().trim().length()!=0 && enumber.getText().toString().trim().length()!=0) {
            save.setEnabled(true);
            save.setTextColor(Color.parseColor("#2e4bf2"));
        }
        if(ename.getText().toString().trim().length()==0 || enumber.getText().toString().trim().length()<=8) {
            save.setEnabled(false);
            save.setTextColor(Color.parseColor("#ced4da"));
        }
    }


    //========================버튼 클릭 처리=======================
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.add_btnBack: //뒤로가기
                    finish();
                    break;
                case R.id.add_btnSave: //저장하기
//                    imageUpload();
                    AddAction();
                    break;
                case R.id.add_eCategory: //그룹정하기
                    new AlertDialog.Builder(MemberAddActivity.this)
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
                case R.id.add_btnImage://이미지 바꾸기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
//                    Toast.makeText(MemberAddActivity.this,img_path,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //========================입력 정보 넣기===========================
    private void AddAction(){
        sname=ename.getText().toString();
        snumber=enumber.getText().toString();

        if(ecomment.getText().toString().trim().length()==0) scomment=null;
        else scomment=ecomment.getText().toString();

        if(ecategory.getText().toString().trim().length()==0) scategory=null;
        else scategory=ecategory.getText().toString();


        urlAddr=urlAddr+"?name="+sname+"&number="+snumber+"&comment="+scomment+"&image="+simage+"&category"+scategory;

        String result=connectInsertData();
        if(result.equals("1")){
            Toast.makeText(MemberAddActivity.this,sname+"님의 정보가 추가되었습니다!",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(MemberAddActivity.this,"입력 실패!",Toast.LENGTH_SHORT).show();
        }

    }

    private String connectInsertData(){ //입력 jsp 실행
        String result=null;
        try{
            NetworkTask networkTask=new NetworkTask(MemberAddActivity.this,urlAddr,"insert");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private void imageUpload(){
        ImageNetworkTask networkTask = new ImageNetworkTask(MemberAddActivity.this, img, img_path, imageJspAddr);

        //=================NetworkTask Class의 doInBackground Method의 결과값을 가져온다================
        try {
            Integer result = networkTask.execute(100).get();

            //=====================doInBackground의 결과값으로 Toast생성============================

            switch (result){
                case 1:
                    simage=imageName;

                    //=========Device에 생성한 임시 파일 삭제
                    File file = new File(img_path);
                    file.delete();
                    break;
                case 0:
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Photo App.에서 Image 선택후 작업내용
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Data :" + String.valueOf(data));

        if (requestCode == REQ_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                //이미지의 URI를 얻어 경로값으로 반환.
                img_path = getImagePathToUri(data.getData());
                Log.v(TAG, "image path :" + img_path);
                Log.v(TAG, "Data :" +String.valueOf(data.getData()));

                //이미지를 비트맵형식으로 반환
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                Bitmap image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
                img.setImageBitmap(image_bitmap_copy);

                // 파일 이름 및 경로 바꾸기(임시 저장, 경로는 임의로 지정 가능)
                String date = new SimpleDateFormat("yyyyMMddHmsS").format(new Date());
                imageName = date + "." + f_ext;
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

}//===============Activity==================