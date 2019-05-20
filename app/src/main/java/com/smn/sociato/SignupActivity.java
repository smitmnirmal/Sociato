package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    EditText ed_user,ed_email,ed_pass,ed_repass;
    Button btn_signup;

    String user,email,pass,repass;

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ed_user=(EditText)findViewById(R.id.ed_su_username);
        ed_email=(EditText)findViewById(R.id.ed_su_email);
        ed_pass=(EditText)findViewById(R.id.ed_su_password);
        ed_repass=(EditText)findViewById(R.id.ed_su_repassword);

        btn_signup=(Button)findViewById(R.id.btn_signup);

        AndroidNetworking.initialize(getApplicationContext());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Signing up...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=ed_user.getText().toString();
                email=ed_email.getText().toString();
                pass=ed_pass.getText().toString();
                repass=ed_repass.getText().toString();

                if(user.isEmpty()){
                    ed_user.requestFocus();
                    ed_user.setError("Enter Username");
                }
                else if(email.isEmpty()){
                    ed_email.requestFocus();
                    ed_email.setError("Enter Email");
                }
                else if(pass.isEmpty()){
                    ed_pass.requestFocus();
                    ed_pass.setError("Enter Password");
                }
                else if(repass.isEmpty()){
                    ed_repass.requestFocus();
                    ed_repass.setError("Re-Enter Password");
                }
                else if(!(pass.equals(repass))){
                    Toast.makeText(SignupActivity.this, "Password and Re-Password must be same.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //if(pass.equals(repass)){}
                    mProgress.show();

                    String urlString = "http://campuscrowd.herokuapp.com/users/register";

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username",user);
                        jsonObject.put("email",email);
                        jsonObject.put("password",pass);
                    } catch (JSONException e) {
                        mProgress.dismiss();
                        Toast.makeText(SignupActivity.this, "Something went wrong! Try again.", Toast.LENGTH_SHORT).show();
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
                                    JSONObject readerObject = null;
                                    try {
                                        readerObject = new JSONObject(response.toString());
                                        if(readerObject.has("success")){
                                            mProgress.dismiss();
                                            Toast.makeText(SignupActivity.this, "SignUp successful, "+user+". Now login", Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(readerObject.has("error")){
                                            mProgress.dismiss();
                                            String err = readerObject.getString("error");
                                            if(err.equals("invalid_username")){
                                                ed_user.requestFocus();
                                                ed_user.setError("Username already exist.");
                                            }
                                            else if(err.equals("invalid_email")){
                                                ed_email.requestFocus();
                                                //ed_email.setText("");
                                                ed_email.setError("Email already exist.");
                                            }
                                        }
                                        //tv_response.setText(name);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        mProgress.dismiss();
                                        Toast.makeText(SignupActivity.this, "Something went wrong! Try again.", Toast.LENGTH_LONG).show();
                                        //tv_response.setText("Invalid Login");
                                    }
                                    //tv_response.setText("Success");
                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    //tv_response.setText("Error");
                                    mProgress.dismiss();
                                    Toast.makeText(SignupActivity.this, "Something went wrong! Try again.", Toast.LENGTH_LONG).show();
                                }
                            });

                    /*Toast.makeText(SignupActivity.this, "Sign Up Successfull !", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);*/
                }
            }
        });
    }
}
