package com.smn.sociato;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Dell on 04-10-2017.
 */

public class connectionAdapter extends RecyclerView.Adapter<connectionAdapter.MyViewHolder> {
    private List<connection> connectionList;
    private ClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView connectionUsername;
        public Button connectionRequestDetails,connectionMessage;
        public ImageView connectionProfile;
        private WeakReference<ClickListener> listenerRef;
        //public LinearLayout linearLayout;
        SendingRequests sendingRequests=new SendingRequests();

        public MyViewHolder(View view, ClickListener listener){
            super(view);

            listenerRef = new WeakReference<>(listener);

            connectionUsername=(TextView)view.findViewById(R.id.tv_add_connection_username);
            connectionRequestDetails=(Button) view.findViewById(R.id.btn_request);
            connectionProfile=(ImageView)view.findViewById(R.id.iv_add_connection);
            connectionMessage=(Button)view.findViewById(R.id.btn_send_message);
            //linearLayout=(LinearLayout)view.findViewById(R.id.ll_post);

            view.setOnClickListener(this);
            connectionRequestDetails.setOnClickListener(this);
            connectionMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==connectionRequestDetails.getId()){
                String conn=connectionRequestDetails.getText().toString();
                connection connection = connectionList.get(this.getPosition());
                if(conn.equals("Send Request")){
                    String succ=sendingRequests.sendRequest(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionRequestDetails.setText("Cancel Request");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(conn.equals("Cancel Request")){
                    String succ=sendingRequests.cancelRequest(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionRequestDetails.setText("Send Request");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(conn.equals("Accept Request")){
                    String succ=sendingRequests.acceptRequest(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionRequestDetails.setText("Request Accepted");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(conn.equals("Remove Connection")){
                    String succ=sendingRequests.removeConnection(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionRequestDetails.setText("Send Request");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if(v.getId()==connectionMessage.getId()){
                String msg=connectionMessage.getText().toString();
                connection connection = connectionList.get(this.getPosition());
                if(msg.equals("Decline Connection")){
                    String succ=sendingRequests.cancelRequest(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionMessage.setText("Message");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                /*else if(msg.equals("Remove Connection")){
                    String succ=sendingRequests.cancelRequest(v.getContext(),connection.getConnectionUserId());
                    //Toast.makeText(v.getContext(), connection.getConnectionUserId(), Toast.LENGTH_SHORT).show();
                    if(succ.equals("true")){
                        connectionMessage.setText("Message");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        }
    }

    public connectionAdapter(List<connection> connectionList){
        this.connectionList=connectionList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent1, int viewType) {
        View itemView = LayoutInflater.from(parent1.getContext())
                .inflate(R.layout.fragment_add_connection, parent1, false);
        return new MyViewHolder(itemView,listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(connectionAdapter.MyViewHolder holder, int position) {
        connection connection=connectionList.get(position);
        holder.connectionUsername.setText(connection.getConnectionUsername());
        if(connection.getConnectionRequestDetails().equals("null")){
            holder.connectionRequestDetails.setVisibility(View.INVISIBLE);
        }
        holder.connectionRequestDetails.setText(connection.getConnectionRequestDetails());
        if(!connection.getConnectionProfile().equals("none"))
        {
            URL url = null;
            try {
                url = new URL(connection.getConnectionProfile());
                Picasso.with(holder.connectionProfile.getContext()).load(String.valueOf(url)).fit().into(holder.connectionProfile);
                //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //holder.connectionProfile.setImageBitmap(bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            holder.connectionProfile.setBackgroundResource(R.drawable.profile_pic);
        }
        if(connection.getConnectionMessage().equals("Decline Connection")){
            holder.connectionMessage.setText("Decline Connection");
        }
        //setBackgroundResource(connection.getConnectionProfile());
        //holder.linearLayout.setBackgroundResource(offer.getBackground());
    }

    @Override
    public int getItemCount() {
        return connectionList.size();
    }

}

