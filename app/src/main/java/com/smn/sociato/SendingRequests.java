package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Dell on 04-10-2017.
 */

public class SendingRequests {
    SharedPreferences sharedPreferences;
    String id;
    private ProgressDialog mProgress;
    String succ="true";

    public String sendRequest(Context context,String destinationId){

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        mProgress = new ProgressDialog(context);
        mProgress.setTitle("Sending request...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from",id);
            jsonObject.put("to",destinationId);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        mProgress.show();
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/addconnection")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                mProgress.dismiss();
                                succ="true";
                            }
                        } catch (JSONException e) {
                            succ="false";
                            mProgress.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                        mProgress.dismiss();
                    }
                });
            return succ;
    }

    public String cancelRequest(Context context,String destinationId){

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        mProgress = new ProgressDialog(context);
        mProgress.setTitle("Canceling request...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from",id);
            jsonObject.put("to",destinationId);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        mProgress.show();
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/cancleconnection")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                mProgress.dismiss();
                                succ="true";
                            }
                        } catch (JSONException e) {
                            succ="false";
                            mProgress.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                        mProgress.dismiss();
                    }
                });
        return succ;
    }

    public String acceptRequest(Context context,String destinationId){

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        mProgress = new ProgressDialog(context);
        mProgress.setTitle("Accepting request...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from",id);
            jsonObject.put("to",destinationId);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        mProgress.show();
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/acceptconnection")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                mProgress.dismiss();
                                succ="true";
                            }
                        } catch (JSONException e) {
                            succ="false";
                            mProgress.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                        mProgress.dismiss();
                    }
                });
        return succ;
    }
    public String removeConnection(Context context,String destinationId){

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        mProgress = new ProgressDialog(context);
        mProgress.setTitle("Removing connection...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from",id);
            jsonObject.put("to",destinationId);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        mProgress.show();
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/removeconnection")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                mProgress.dismiss();
                                succ="true";
                            }
                        } catch (JSONException e) {
                            succ="false";
                            mProgress.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                        mProgress.dismiss();
                    }
                });
        return succ;
    }
    public String like(final Context context, String postId){

        //Toast.makeText(context, "Inside Like", Toast.LENGTH_SHORT).show();

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",id);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/posts/like/"+postId)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                                succ="true";
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Problem", Toast.LENGTH_SHORT).show();
                            succ="false";
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                    }
                });
        return succ;
    }
    public String dislike(final Context context, String postId){
        //Toast.makeText(context, "Inside Dislike", Toast.LENGTH_SHORT).show();

        AndroidNetworking.initialize(context);

        sharedPreferences=context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey","");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",id);
        } catch (JSONException e) {
            succ="false";
            e.printStackTrace();
        }
        AndroidNetworking.post("http://campuscrowd.herokuapp.com/posts/dislike/"+postId)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object=new JSONObject(response.toString());
                            if(object.has("success")){
                                succ="true";
                                Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Prob", Toast.LENGTH_SHORT).show();
                            succ="false";
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        succ="false";
                    }
                });
        return succ;
    }
}