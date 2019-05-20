package com.smn.sociato;

/**
 * Created by Dell on 20-09-2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private List<post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private postAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;

    String user_id;
    String tag;
    SharedPreferences sharedPreferences;

    private ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Dashboard");

        AndroidNetworking.initialize(getActivity().getApplicationContext());

        sharedPreferences=view.getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        user_id=sharedPreferences.getString("idKey","");

        mProgress = new ProgressDialog(this.getActivity());
        mProgress.setTitle("Loading Posts...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        recyclerView=(RecyclerView)getActivity().findViewById(R.id.rv_myPost);
        swipeRefreshLayout=(SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refresh);

        postAdapter = new postAdapter(postList);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*Intent intent=new Intent(getActivity().getApplicationContext(),Dashboard.class);
                startActivity(intent);
                getActivity().finish();*/
                if(postList!=null){
                    postList.clear();
                }
                preparePostData();
                //onViewCreated(view,savedInstanceState);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //post post = postList.get(position);
                //Toast.makeText(getActivity().getApplicationContext(), post.getPostId() + " is selected!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        preparePostData();
    }

    private void preparePostData() {
        mProgress.show();

        AndroidNetworking.get("http://campuscrowd.herokuapp.com/posts/feed/"+user_id)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i < response.length(); i++)
                        {
                            String image_url="null";
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String postid = object.getString("_id");
                                String body=object.getString("body");
                                String img=object.getString("img");
                                String time=object.getString("updatedAt");
                                String avatar="null",avatar_url="none";
                                if(!img.isEmpty()){
                                    image_url="http://campuscrowd.herokuapp.com/"+img;
                                }
                                JSONObject userObject=object.getJSONObject("uploader");
                                String username;
                                if(userObject.has("fullname")){
                                    username=userObject.getString("fullname");
                                }
                                else{
                                    username=userObject.getString("name");
                                }
                                if(userObject.has("avatar")){
                                    avatar=userObject.getString("avatar");
                                }
                                if(!avatar.isEmpty()){
                                    avatar_url="http://campuscrowd.herokuapp.com/"+avatar;
                                }
                                JSONArray likeArray=object.getJSONArray("likes");
                                /*for(int l=0;l<likeArray.length();l++){
                            }*/
                                if(likeArray.toString().contains(user_id)){
                                    tag="liked";
                                }
                                else {
                                    tag="like";
                                }
                                JSONArray commentArray=object.getJSONArray("comments");
                                int likes=likeArray.length();
                                int comments=commentArray.length();
                                if(!image_url.equals("null")){
                                    post post= new post(avatar_url,image_url,username,body,postid,time,tag,likes,comments);
                                    postList.add(post);
                                }
                                else {
                                    post post= new post(avatar_url,"none",username,body,postid,time,tag,likes,comments);
                                    postList.add(post);
                                }
                                postAdapter.notifyItemInserted(i);
                            } catch (JSONException e) {
                                mProgress.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(),"Something went wrong, try again later!", Toast.LENGTH_LONG).show();
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                        mProgress.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        mProgress.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(),"Check your connection and try again later!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}