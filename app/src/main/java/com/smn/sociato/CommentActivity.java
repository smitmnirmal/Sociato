package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    SharedPreferences sharedPreferences;

    ImageView iv_user_prof;
    TextView tv_user,tv_time,tv_detail;
    Button btn_add_comment;

    private List<comment> commentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private commentAdapter commentAdapter;
    EditText ed_addComment;

    String post_id,user_id,user_name,avatar_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");

        AndroidNetworking.initialize(getApplicationContext());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading Comments...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        post_id=sharedPreferences.getString("postKey",null);
        user_id=sharedPreferences.getString("userKey",null);
        user_name=sharedPreferences.getString("nameKey",null);
        avatar_url=sharedPreferences.getString("avatarKey",null);
        if (avatar_url.isEmpty()){
            avatar_url="null";
        }
        Toast.makeText(CommentActivity.this, avatar_url, Toast.LENGTH_LONG).show();


        iv_user_prof=(ImageView)findViewById(R.id.iv_comment_profile);
        tv_user=(TextView) findViewById(R.id.tv_comment_username);
        tv_detail=(TextView)findViewById(R.id.tv_comment_details);
        tv_time=(TextView)findViewById(R.id.tv_comment_time);
        btn_add_comment=(Button)findViewById(R.id.btn_addComment);
        ed_addComment=(EditText)findViewById(R.id.ed_addComment);

        recyclerView=(RecyclerView)findViewById(R.id.rv_comment);

        commentAdapter = new commentAdapter(commentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //post post = postList.get(position);
                //Toast.makeText(getActivity().getApplicationContext(), post.getPostId() + " is selected!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        prepareCommentData();

        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmnt=ed_addComment.getText().toString();
                if(cmnt.isEmpty()){
                    ed_addComment.requestFocus();
                    ed_addComment.setError("Can't post empty comment!");
                }
                else{
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userid",user_id);
                        jsonObject.put("fullname",user_name);
                        jsonObject.put("body",cmnt);
                        if(!avatar_url.equals("null")){
                            jsonObject.put("avatar",avatar_url);
                        }else{
                            jsonObject.put("avatar","");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("http://campuscrowd.herokuapp.com/posts/addcomment/"+post_id)
                            .addJSONObjectBody(jsonObject) // posting json
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONObject readerObject1=null;
                                    try {
                                        readerObject1=new JSONObject(response.toString());
                                        if(readerObject1.has("success")){
                                            mProgress.dismiss();
                                            Intent intent=new Intent(getApplicationContext(),CommentActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        mProgress.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    mProgress.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void prepareCommentData() {
        mProgress.show();

        AndroidNetworking.get("http://campuscrowd.herokuapp.com/posts")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = response.length()-1 ; i >= 0; i--)
                        {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String postid = object.getString("_id");

                                if(postid.equals(post_id)){
                                    JSONArray commentArray=object.getJSONArray("comments");
                                    if(commentArray!=null){
                                        for(int j = commentArray.length()-1 ; j >= 0; j--)
                                        {
                                            //Toast.makeText(CommentActivity.this, postid+"matched", Toast.LENGTH_SHORT).show();
                                            JSONObject newObject=commentArray.getJSONObject(j);
                                            String name,body,avatar="null",avatar_url="null",date;
                                            name=newObject.getString("fullname");
                                            body=newObject.getString("body");
                                            if(newObject.has("avatar")){
                                                avatar=newObject.getString("avatar");
                                            }
                                            if(avatar.isEmpty()){
                                                avatar="null";
                                            }
                                            date=newObject.getString("date");
                                            //Toast.makeText(CommentActivity.this, name, Toast.LENGTH_SHORT).show();

                                            if(!avatar.equals("null")){
                                                avatar_url="http://campuscrowd.herokuapp.com/"+avatar;
                                                comment comment=new comment(avatar_url,name,body,date);
                                                commentList.add(comment);
                                            }
                                            else {
                                                comment comment=new comment("none",name,body,date);
                                                commentList.add(comment);
                                            }
                                        }
                                    }
                                }
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                        mProgress.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(),"Check your connection and try again later!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}