package com.example.adressbook.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adressbook.Activity.MainActivity;
import com.example.adressbook.Activity.MemberInfoActivity;
import com.example.adressbook.Activity.StartActivity;
import com.example.adressbook.Bean.AddressInfo;
import com.example.adressbook.NetworkTask.NetworkTask;
import com.example.adressbook.R;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context mcontext=null;
    private int layout=0;
    private ArrayList<AddressInfo> data=null;
    private LayoutInflater inflater=null;
    private String image="";

    private OnMemberItemClickListener mListener=null;
    private String scode=null;

    //====================클릭 이벤트 인터페이스==============================

    public interface OnMemberItemClickListener{
        void onItemClick(View v, int position);
    }

    //==================OnItemClickListener 객체 참조를 어댑터에 전달=================

    public void setOnItemClickListeners(OnMemberItemClickListener listener){
        this.mListener=listener;
    }

    //===========================생성자===================================

    public MemberAdapter(Context mcontext, int layout, ArrayList<AddressInfo> data) {
        this.mcontext = mcontext;
        this.layout = layout;
        this.data = data;
        this.inflater= (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //===========================뷰 홀더==================================

    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView member_name;
        public WebView webView;
        public ImageButton member_call;
        public ImageButton member_message;
        public ViewHolder(View converView){
            super(converView);
            member_name=converView.findViewById(R.id.member_name);
            webView=converView.findViewById(R.id.member_image);
            member_call=converView.findViewById(R.id.member_call);
            member_message=converView.findViewById(R.id.member_message);


            //=====================클릭 시 몇번째인지 불러옴===================
            converView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();

                    if(position!=RecyclerView.NO_POSITION){
                        Intent intent=new Intent(mcontext,MemberInfoActivity.class);
                        intent.putExtra("name",data.get(position).getName());
                        intent.putExtra("number",data.get(position).getNumber());
                        intent.putExtra("comment",data.get(position).getComment());
                        intent.putExtra("category",data.get(position).getCategory());
                        intent.putExtra("image",data.get(position).getImage());
                        intent.putExtra("code",data.get(position).getCode());
                        mcontext.startActivity(intent);
                    }
                }
            });

            converView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position=getAdapterPosition();

                    if(position!=RecyclerView.NO_POSITION){
                        scode=data.get(position).getCode();
                    }

                    String sname=data.get(position).getName();

                    new AlertDialog.Builder(mcontext)
                            .setMessage(sname+"님의 연락처를 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    connectDeleteData();

                                    ((MainActivity)MainActivity.context).onResume(); //삭제 후 리스트 다시 불러오기

                                    Toast.makeText(mcontext,sname+"님의 연락처가 삭제되었습니다!",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                    return true;
                }
            });

            member_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();

                    if(position!=RecyclerView.NO_POSITION){
                            Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data.get(position).getNumber()));
                            mcontext.startActivity(call);
                    }
                }
            });

            member_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();

                    if(position!=RecyclerView.NO_POSITION) {
                        Uri smsUri = Uri.parse("smsto:" + data.get(position).getNumber());
                        Intent sms = new Intent(Intent.ACTION_SENDTO, smsUri);
                        mcontext.startActivity(sms);
                    }
                }
            });
        }

    }

    //==========================레이아웃 구성====================================

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.member_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    //===============================리스트 중 한줄 구성==================================

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
        holder.member_name.setText(data.get(position).getName());
        if(data.get(position).getImage().equals("null")){// 이미지가 비어있으면 기본이미지 아니면 지정 이미지 띄워주기
            holder.webView.loadDataWithBaseURL(null,
                    "<html><head></head> <body><img style = 'width: 100%; height:auto;' src=\"http://192.168.145.42:8080/image/user.jpg\"></body></html>",
                    "text/html","utf-8",null);
        }else {
            holder.webView.loadDataWithBaseURL(null,htmlData(data.get(position).getImage()),"text/html","utf-8",null);
        }
    }


    //=======================데이터 양 만큼 불러오기====================

    @Override
    public int getItemCount() {
        return data.size();
    }


    //===============================멤버 이미지 서버 경로에서 불러오기 =====================

    public String htmlData(String location){
        String htmlData="<html><head></head> <body><img style = 'width: 100%; height:auto;' src=\"http://192.168.145.42:8080/image/"+location+"\"></body></html>";

        return  htmlData;
    }



    private String connectDeleteData(){
        String result=null;
        try{
            NetworkTask networkTask=new NetworkTask(mcontext,"http://192.168.145.42:8080/address/MemberDeleteReturn.jsp?code="+scode,"insert");
            Object obj=networkTask.execute().get();
            result=(String)obj;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }


}
