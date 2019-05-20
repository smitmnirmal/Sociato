package com.smn.sociato;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Dell on 01-10-2017.
 */

public class postAdapter extends RecyclerView.Adapter<postAdapter.MyViewHolder> {
    private List<post> postList;
    private ClickListener listener;
    SharedPreferences sharedPreferences;
    SendingRequests sendingRequests=new SendingRequests();

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView postUsername,postDetails,postTime,postTotalLike,postTotalComment;
        public ImageView postProfile,postImage,like,iv_comment,share;
        public LinearLayout linearLayout;
        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener){
            super(view);

            listenerRef = new WeakReference<>(listener);

            postUsername=(TextView)view.findViewById(R.id.tv_post_username);
            postDetails=(TextView)view.findViewById(R.id.tv_post_details);
            postProfile=(ImageView)view.findViewById(R.id.iv_post_profile);
            postImage=(ImageView)view.findViewById(R.id.iv_post);
            linearLayout=(LinearLayout)view.findViewById(R.id.ll_post);
            postTime=(TextView)view.findViewById(R.id.tv_post_time);
            like=(ImageView)view.findViewById(R.id.iv_like);
            iv_comment=(ImageView)view.findViewById(R.id.iv_comment);
            share=(ImageView)view.findViewById(R.id.iv_share);
            postTotalLike=(TextView)view.findViewById(R.id.tv_totLike);
            postTotalComment=(TextView)view.findViewById(R.id.tv_totComment);

            view.setOnClickListener(this);
            like.setOnClickListener(this);
            iv_comment.setOnClickListener(this);
            share.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //v.animate().alpha(0.7f).scaleX(0.9f).scaleY(0.9f).setDuration(500);
            post post = postList.get(this.getPosition());
            sharedPreferences=v.getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("postKey",post.getPostId());
            editor.commit();
            if(v.getId()==like.getId()){
                String status=like.getTag().toString();
                if(status.equals("liked")){
                    String succ=sendingRequests.dislike(v.getContext(),post.getPostId());
                    if(succ.equals("true")){
                        String tot_like=postTotalLike.getText().toString();
                        int tot=Integer.parseInt(tot_like);
                        tot-=1;
                        like.setBackgroundResource(R.drawable.thumbs_up_icon);
                        like.setTag("like");
                        postTotalLike.setText(Integer.toString(tot));
                    }
                }
                else if(status.equals("like")){
                    String succ=sendingRequests.like(v.getContext(),post.getPostId());
                    if(succ.equals("true")){
                        String tot_like=postTotalLike.getText().toString();
                        int tot=Integer.parseInt(tot_like);
                        tot+=1;
                        like.setBackgroundResource(R.drawable.thumbs_up_color_icon);
                        like.setTag("liked");
                        postTotalLike.setText(Integer.toString(tot));
                    }
                }
                /*if(like.getBackground().getConstantState().equals(R.drawable.thumbs_up_icon)){
                    like.setBackgroundResource(R.drawable.thumbs_up_color_icon);
                }
                else if(getDrawableId().equals(R.drawable.thumbs_up_color_icon)){
                    like.setBackgroundResource(R.drawable.thumbs_up_icon);
                }*/
            }
            if(v.getId()==iv_comment.getId()){
                //Toast.makeText(v.getContext(), post.getPostId(), Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(v.getContext(),CommentActivity.class);
                v.getContext().startActivity(intent);
            }
        }
    }

    public postAdapter(List<post> postList){
        this.postList=postList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);
        return new MyViewHolder(itemView,listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        post post=postList.get(position);
        holder.postUsername.setText(post.getPostUsername());
        holder.postDetails.setText(post.getPostDetails());
        //holder.postImage.setBackgroundResource(post.getPostImage());
        //holder.postProfile.setBackgroundResource(post.getPostProfile());
        holder.postTime.setText(post.getPostTime());
        if(!post.getPostImage().equals("none"))
        {
            URL url = null;
            try {
                url = new URL(post.getPostImage());
                Picasso.with(holder.postImage.getContext()).load(String.valueOf(url)).fit().into(holder.postImage);
                //holder.postImage.getLayoutParams().height=300;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else {
            holder.postImage.setBackgroundResource(R.drawable.ic_menu_camera);
        }
        if(!post.getPostProfile().equals("none"))
        {
            URL url = null;
            try {
                url = new URL(post.getPostProfile());
                Picasso.with(holder.postProfile.getContext()).load(String.valueOf(url)).fit().into(holder.postProfile);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        int tot_like=post.getPostTotalLike();
        int tot_cmnt=post.getPostTotalComment();
        holder.postTotalLike.setText(Integer.toString(tot_like));
        holder.postTotalComment.setText(Integer.toString(tot_cmnt));

        String like_detail=post.getPostLike();
        if(like_detail.equals("like")){
            holder.like.setBackgroundResource(R.drawable.thumbs_up_icon);
            holder.like.setTag("like");
        }
        else if(like_detail.equals("liked")){
            holder.like.setBackgroundResource(R.drawable.thumbs_up_color_icon);
            holder.like.setTag("liked");
        }
        //holder.linearLayout.setBackgroundResource(offer.getBackground());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}
