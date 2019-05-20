package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    EditText ed_user,ed_pass;
    TextView tv_response;
    Button btn_login;

    private ProgressDialog mProgress;

    SharedPreferences sharedPreferences;
    public static final String mypref="MyPref";
    public static final String namekey="nameKey";
    public static final String idkey="idKey";
    public static final String tokenkey="tokenKey";

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (Button) findViewById(R.id.btn_signin);
        tv_response= (TextView)findViewById(R.id.tv_response);

        AndroidNetworking.initialize(getApplicationContext());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Logging you in...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        sharedPreferences=getSharedPreferences(mypref, Context.MODE_PRIVATE);
        String user=sharedPreferences.getString("userNamekey","");
        String fullname=sharedPreferences.getString("nameKey","");
        if(!fullname.isEmpty()){
            Intent intent=new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            finish();
        }
        else if(!user.isEmpty())
        {
            Intent intent=new Intent(getApplicationContext(),CompleteProfile.class);
            startActivity(intent);
            finish();
        }

        if(!checkPermission())
        {
            requestPermission();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                ed_user=(EditText)findViewById(R.id.ed_username);
                ed_pass=(EditText)findViewById(R.id.ed_password);
                //tv_response=(TextView)findViewById(R.id.tv_response);

                String user,pass;
                user=ed_user.getText().toString();
                pass=ed_pass.getText().toString();

                if(user.isEmpty()){
                    ed_user.requestFocus();
                    ed_user.setError("Enter Username");
                }
                else if(pass.isEmpty()){
                    ed_pass.requestFocus();
                    ed_pass.setError("Enter Password");
                }
                else {
                    mProgress.show();

                    String urlString = "http://campuscrowd.herokuapp.com/users/authenticate";

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username",user);
                        jsonObject.put("password",pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AndroidNetworking.post(urlString)
                            .addJSONObjectBody(jsonObject) // posting json
                            .setTag("test")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    JSONObject readerObject,userObject = null;
                                    try {
                                        readerObject = new JSONObject(response.toString());

                                        if(readerObject.has("success")){
                                            String status="",avatar="",fullname="null";
                                            //status = reader.getString("success");
                                            String token = readerObject.getString("token");
                                            userObject=readerObject.getJSONObject("user");
                                            String user=userObject.getString("username");
                                            String userid=userObject.getString("id");
                                            if(userObject.has("avatar"))
                                            {
                                                avatar=userObject.getString("avatar");
                                            }
                                            boolean prof=userObject.getBoolean("profile");
                                            if(userObject.has("fullname")){
                                                fullname=userObject.getString("fullname");
                                            }

                                            sharedPreferences=getSharedPreferences(mypref, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor=sharedPreferences.edit();

                                            editor.putString("usernameKey",user);
                                            editor.putString(idkey,userid);
                                            editor.putString(tokenkey,token);
                                            if(!avatar.equals("")){
                                                editor.putString("avatarKey",avatar);
                                                Toast.makeText(LoginActivity.this, avatar, Toast.LENGTH_SHORT).show();
                                            }
                                            if(!fullname.equals("null")){
                                                editor.putString("nameKey",fullname);
                                            }
                                            editor.commit();

                                            mProgress.dismiss();

                                            if(userObject.has("fullname")){
                                                Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Intent intent = new Intent(getApplicationContext(), CompleteProfile.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        else if(readerObject.has("error")){
                                            mProgress.dismiss();
                                            String err = readerObject.getString("error");
                                            if(err.equals("invalid_username")){
                                                ed_user.requestFocus();
                                                ed_user.setError("Invalid Username");
                                                Toast.makeText(LoginActivity.this, "Invalid Username", Toast.LENGTH_LONG).show();
                                            }
                                            else if(err.equals("invalid_password")){
                                                ed_pass.requestFocus();
                                                ed_pass.setError("Invalid Password");
                                                Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        /*if(status.equals("true")){
                                            Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            String err = reader.getString("error");
                                            if(!err.isEmpty())
                                            Toast.makeText(LoginActivity.this, "Check Username or Password", Toast.LENGTH_LONG).show();
                                        }*/
                                        //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                                        //JSONObject userdata  = reader.getJSONObject("user");
                                        //String name = userdata.getString("username");
                                    } catch (JSONException e) {
                                        mProgress.dismiss();
                                        Toast.makeText(LoginActivity.this, "Check your connection and try again.", Toast.LENGTH_LONG).show();
                                    }
                                    //tv_response.setText("Success");
                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    //tv_response.setText("Error");
                                    mProgress.dismiss();
                                    Toast.makeText(LoginActivity.this, "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        }
        });

        tv_response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CompleteProfile.class);
                startActivity(intent);
            }
        });
    }

    public void signup(View view) {
        Intent intent=new Intent(this,SignupActivity.class);
        startActivity(intent);
    }

/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void login(View view) {
        ed_user=(EditText)findViewById(R.id.ed_username);
        ed_pass=(EditText)findViewById(R.id.ed_password);
        tv_response=(TextView)findViewById(R.id.tv_response);

        String user,pass;
        user=ed_user.getText().toString();
        pass=ed_pass.getText().toString();

        String urlParameters  = "username="+user+"&password="+pass;

        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = "http://campuscrowd.herokuapp.com/login";
        try {
            URL url = new URL(request);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write( postData );
                tv_response.setText(postData.toString());
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public void forgotpassword(View view) {
        Intent intent=new Intent(this,ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
        //return result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA,WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        //ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeexternal = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean camara = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeexternal && camara) {
                    //if (camara) {
                        Toast.makeText(this, "Permission Granted.", Toast.LENGTH_LONG).show();
                        //Bundle newBundle=new Bundle();
                        //onCreate(newBundle);
                        //Intent intent=new Intent(this,LoginActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivity(intent);
                        //finish();
                    }
                    else {
                        Toast.makeText(this, "Kindly Grant Permissions!", Toast.LENGTH_LONG).show();
                        requestPermission();
                    }
                }
                break;
        }
    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
