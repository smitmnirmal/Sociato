package com.smn.sociato;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompleteProfile extends AppCompatActivity {

    EditText ed_fname,ed_lname,ed_clg,ed_branch,ed_sem,ed_bio;
    Button createprofile,skipprofile;

    SharedPreferences sharedPreferences;
    private ProgressDialog mProgress;

    ImageView iv_user;
    Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        getSupportActionBar().setTitle("Complete Profile");

        AndroidNetworking.initialize(getApplicationContext());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Completing your profile...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final String id=sharedPreferences.getString("idKey",null);

        iv_user=(ImageView)findViewById(R.id.iv_cp_userimage);
        createprofile=(Button)findViewById(R.id.btn_createprofile);
        //skipprofile=(Button)findViewById(R.id.btn_skipprofile);
        ed_fname=(EditText)findViewById(R.id.ed_firstname);
        ed_lname=(EditText)findViewById(R.id.ed_lastname);
        ed_clg=(EditText)findViewById(R.id.ed_collegename);
        ed_branch=(EditText)findViewById(R.id.ed_branch);
        ed_sem=(EditText)findViewById(R.id.ed_currentsem);
        ed_bio=(EditText)findViewById(R.id.ed_bio);

        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(CompleteProfile.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            //File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            //pic = f;
                            startActivityForResult(intent, 1);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        /*skipprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                startActivity(intent);
                finish();
            }
        });*/

        createprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String fname,lname,clg,branch,sem,bio;

                mProgress.show();

                fname=ed_fname.getText().toString();
                lname=ed_lname.getText().toString();
                clg=ed_clg.getText().toString();
                branch=ed_branch.getText().toString();
                sem=ed_sem.getText().toString();
                bio=ed_bio.getText().toString();

                if(fname.isEmpty()){
                    ed_fname.requestFocus();
                    ed_fname.setError("Enter First Name");
                }
                else if(lname.isEmpty()){
                    ed_lname.requestFocus();
                    ed_lname.setError("Enter Last Name");
                }
                else if(clg.isEmpty()){
                    ed_clg.requestFocus();
                    ed_clg.setError("Enter College Name");
                }
                else if(branch.isEmpty()){
                    ed_branch.requestFocus();
                    ed_branch.setError("Enter Branch Name");
                }
                else if(sem.isEmpty()){
                    ed_sem.requestFocus();
                    ed_sem.setError("Enter Sem");
                }
                else{
                    if(!(bitmap==null)){
                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/profile_image");
                        myDir.mkdirs();
                        final String filename="profile.jpg";
                        File file = new File (myDir, filename);
                        if (file.exists ()) file.delete ();

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
/*
                        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/uploadavatar")
                                .addFileBody(file)// posting any type of file
                                .setTag("test")
                                .setPriority(Priority.MEDIUM)
                                .build()
*/

                        AndroidNetworking.upload("http://campuscrowd.herokuapp.com/users/uploadavatar")
                                .addMultipartParameter("key","avatar")
                                .addMultipartFile("value",file)
                                .setTag("uploadAvatar")
                                .setPriority(Priority.MEDIUM)
                                .build()
                                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        // do anything with progress
                                    }
                                })
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        JSONObject readerObject = null;
                                        try {
                                            readerObject = new JSONObject(response.toString());
                                            if(readerObject.has("success")){
                                                //boolean succ=readerObject.getBoolean("success");
                                                    String avtar_filename=readerObject.getString("filename");
                                                    Toast.makeText(CompleteProfile.this, avtar_filename, Toast.LENGTH_LONG).show();
                                                    JSONObject jsonObject = new JSONObject();
                                                    try {
                                                        jsonObject.put("userid",id);
                                                        jsonObject.put("firstname",fname);
                                                        jsonObject.put("lastname",lname);
                                                        jsonObject.put("college",clg);
                                                        jsonObject.put("branch",branch);
                                                        jsonObject.put("year",sem);
                                                        jsonObject.put("bio",bio);
                                                        jsonObject.put("avatar",avtar_filename);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/createprofile")
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
                                                                            sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                                                            editor.putString("nameKey",fname+" "+lname);
                                                                            editor.commit();
                                                                                mProgress.dismiss();
                                                                                Intent intent=new Intent(getApplicationContext(),Dashboard.class);
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
                                        catch (JSONException e) {
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
                    else {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("userid",id);
                            jsonObject.put("firstname",fname);
                            jsonObject.put("lastname",lname);
                            jsonObject.put("college",clg);
                            jsonObject.put("branch",branch);
                            jsonObject.put("year",sem);
                            jsonObject.put("bio",bio);
                            jsonObject.put("avatar","");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgress.dismiss();
                        }

                        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/createprofile")
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
                                                sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("nameKey",fname+" "+lname);
                                                editor.commit();
                                                    mProgress.dismiss();
                                                    Intent intent=new Intent(getApplicationContext(),Dashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                            }
                                        } catch (JSONException e) {
                                            mProgress.dismiss();
                                            Snackbar.make(v, "Something Went Wrong!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        mProgress.dismiss();
                                        Snackbar.make(v, "Something Went Wrong!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }
                                });
                    }

                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(photo);
        }*/
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                bitmap = (Bitmap) data.getExtras().get("data");
                iv_user.setImageBitmap(bitmap);
            }
            else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                // h=1;
                //imgui = selectedImage;
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                //Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));
                //Log.i("path of image from gallery......******************.........", picturePath+"");
                iv_user.setImageBitmap(bitmap);
            }
        }
    }
}
