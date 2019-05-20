package com.smn.sociato;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText ed_email;
    Button btn_forgot;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ed_email=(EditText)findViewById(R.id.ed_fp_email);
        btn_forgot=(Button)findViewById(R.id.btn_forgotpassword);

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=ed_email.getText().toString();
                if(email.isEmpty()){
                    ed_email.requestFocus();
                    ed_email.setError("Enter email");
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Your password will be sent to your email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
