package com.smn.sociato;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Dell on 07-10-2017.
 */

public class commentAdapter  extends RecyclerView.Adapter<commentAdapter.MyViewHolder> {
    private List<comment> commentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUsername, commentDetails, commentTime;
        public ImageView commentUserProfile;

        public MyViewHolder(View view){
            super(view);
            commentUsername=(TextView)view.findViewById(R.id.tv_comment_username);
            commentDetails=(TextView)view.findViewById(R.id.tv_comment_details);
            commentUserProfile=(ImageView)view.findViewById(R.id.iv_comment_profile);
            commentTime=(TextView)view.findViewById(R.id.tv_comment_time);
        }
    }

    public commentAdapter(List<comment> commentList){
        this.commentList=commentList;
    }

    @Override
    public commentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(commentAdapter.MyViewHolder holder, int position) {
        comment comment=commentList.get(position);
        holder.commentUsername.setText(comment.getCommentUsername());
        holder.commentDetails.setText(comment.getCommentDetails());
        //holder.postImage.setBackgroundResource(post.getPostImage());
        //holder.postProfile.setBackgroundResource(post.getPostProfile());
        holder.commentTime.setText(comment.getCommentTime());
        if(!comment.getCommentUserProfile().equals("none"))
        {
            URL url = null;
            try {
                url = new URL(comment.getCommentUserProfile());
                Picasso.with(holder.commentUserProfile.getContext()).load(String.valueOf(url)).fit().into(holder.commentUserProfile);
                //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //holder.connectionProfile.setImageBitmap(bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        //holder.linearLayout.setBackgroundResource(offer.getBackground());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

}
