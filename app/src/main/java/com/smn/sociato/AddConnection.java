package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 03-10-2017.
 */

public class AddConnection extends Fragment {

    private List<connection> connectionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private connectionAdapter connectionAdapter;
    private ProgressDialog mProgress;

    SharedPreferences sharedPreferences;
    String id;

    JSONArray receivedRequest,sentRequest,myNetwork;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_connection_recy, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Discover People");

        AndroidNetworking.initialize(getActivity().getApplicationContext());

        sharedPreferences=this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        //String username=sharedPreferences.getString("nameKey",null);
        id=sharedPreferences.getString("idKey",null);
        //Toast.makeText(getActivity().getApplicationContext(), id, Toast.LENGTH_SHORT).show();

        mProgress = new ProgressDialog(this.getActivity());
        mProgress.setTitle("Loading Users...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        recyclerView=(RecyclerView)getActivity().findViewById(R.id.rv_myAddConnection);

        connectionAdapter = new connectionAdapter(connectionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(connectionAdapter);

        int count=connectionAdapter.getItemCount();
        if(count==0){
            //Toast.makeText(view.getContext(), "Sorry you have no request pending", Toast.LENGTH_SHORT).show();
            Snackbar.make(view,"Sorry no new user found!",Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                connection connection = connectionList.get(position);
                Toast.makeText(getActivity().getApplicationContext(), connection.getConnectionUserId() + " is selected!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareConnectionData();
    }

    private void prepareConnectionData() {
        mProgress.show();

        //Toast.makeText(getActivity().getApplicationContext(), id, Toast.LENGTH_SHORT).show();

        AndroidNetworking.get("http://campuscrowd.herokuapp.com/users/"+id)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "Users")
                .setTag("data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject newObject=new JSONObject(response.toString());
                            receivedRequest =newObject.getJSONArray("recievedrequests");
                            sentRequest=newObject.getJSONArray("sentrequests");
                            /*for(int q=0;q<sentRequest.length();q++){
                                Toast.makeText(getActivity().getApplicationContext(), sentRequest.getString(q), Toast.LENGTH_SHORT).show();
                            }*/
                            myNetwork=newObject.getJSONArray("network");

                            AndroidNetworking.get("http://campuscrowd.herokuapp.com/users/")
                                    .addPathParameter("pageNumber", "0")
                                    .addQueryParameter("limit", "30")
                                    .addHeaders("token", "data")
                                    .setTag("data")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONArray(new JSONArrayRequestListener() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            for(int i = 0; i < response.length(); i++)
                                            {
                                                //Toast.makeText(getActivity().getApplicationContext(), "Inside Second REquest", Toast.LENGTH_SHORT).show();
                                                try {
                                                    JSONObject object = response.getJSONObject(i);
                                                    String userid = object.getString("_id");
                                                    String username =object.getString("username");
                                                    String avatarurl="null";
                                                    if(object.has("profile")){
                                                        JSONObject profile=object.getJSONObject("profile");
                                                        String avtar=profile.getString("avatar");
                                                        if(!avtar.isEmpty()){
                                                            avatarurl="http://campuscrowd.herokuapp.com/"+avtar;
                                                        }
                                                    }
                                                    //if(receivedRequest!=null) {
                                                        if(receivedRequest.toString().contains(userid)){
                                                            continue;
                                                        }
                                                    //}
                                                    //if(sentRequest!=null){
                                                        //Toast.makeText(getActivity().getApplicationContext(), "Inside sent request", Toast.LENGTH_SHORT).show();
                                                        /*for(int k=0;k<sentRequest.length();k++){
                                                            if(sentRequest.getString(k).equals(userid)){
                                                                Toast.makeText(getActivity().getApplicationContext(), "Matched with "+username, Toast.LENGTH_SHORT).show();
                                                                connection connection= new connection(R.drawable.ic_menu_camera,username,"Cancel Request",userid);
                                                                connectionList.add(connection);
                                                                connectionAdapter.notifyDataSetChanged();
                                                                continue;
                                                            }
                                                        }*/
                                                        if(sentRequest.toString().contains(userid)){
                                                            //Toast.makeText(getActivity().getApplicationContext(), "Matched with "+username, Toast.LENGTH_SHORT).show();
                                                            if(!avatarurl.equals("null")){
                                                                connection connection= new connection(avatarurl,username,"Cancel Request",userid,"none");
                                                                connectionList.add(connection);
                                                            }
                                                            else {
                                                                connection connection= new connection("none",username,"Cancel Request",userid,"none");
                                                                connectionList.add(connection);
                                                            }
                                                            connectionAdapter.notifyDataSetChanged();
                                                            continue;
                                                        }
                                                    //}
                                                    //if(myNetwork!=null){
                                                        if(myNetwork.toString().contains(userid)){
                                                            continue;
                                                        }
                                                    //}
                                                    if(userid.equals(id)){
                                                        continue;
                                                    }
                                                    else{
                                                        if(!avatarurl.equals("null")) {
                                                            connection connection = new connection(avatarurl, username, "Send Request", userid,"none");
                                                            connectionList.add(connection);
                                                        }
                                                        else {
                                                            connection connection = new connection("none", username, "Send Request", userid,"none");
                                                            connectionList.add(connection);
                                                        }
                                                        connectionAdapter.notifyDataSetChanged();
                                                    }
                                                } catch (JSONException e) {
                                                    mProgress.dismiss();
                                                    Toast.makeText(getActivity().getApplicationContext(),"Something went wrong, try again later!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            connectionAdapter.notifyDataSetChanged();
                                            mProgress.dismiss();
                                        }
                                        @Override
                                        public void onError(ANError error) {
                                            mProgress.dismiss();
                                            Toast.makeText(getActivity().getApplicationContext(),"Check your connection and try again later!", Toast.LENGTH_LONG).show();
                                        }
                                    });


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),"Check your connection and try again later!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getActivity().getApplicationContext(),"Check your connection and try again later!", Toast.LENGTH_LONG).show();
                    }
                });
        int count=connectionAdapter.getItemCount();
        if(count==0){
            //Toast.makeText(view.getContext(), "Sorry you have no request pending", Toast.LENGTH_SHORT).show();
            Snackbar.make(getView(),"Sorry no new user found!",Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}