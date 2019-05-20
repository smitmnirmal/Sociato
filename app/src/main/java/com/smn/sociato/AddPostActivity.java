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

public class AddPostActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    SharedPreferences sharedPreferences;

    ImageView iv_post;
    EditText ed_post_detail;
    Button btn_add_post;

    Bitmap bitmap=null;

    String id,username,avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setTitle("Add Post");

        AndroidNetworking.initialize(getApplicationContext());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Posting...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("idKey",null);
        username=sharedPreferences.getString("nameKey",null);
        avatar=sharedPreferences.getString("avatarKey",null);
        if(avatar.isEmpty()){
            avatar="null";
        }

        iv_post=(ImageView)findViewById(R.id.iv_add_post);
        ed_post_detail=(EditText)findViewById(R.id.ed_addPostDetail);
        btn_add_post=(Button)findViewById(R.id.btn_addPost);

        iv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
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

        btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                final String postDetail=ed_post_detail.getText().toString();

                if(postDetail.isEmpty() && bitmap==null){
                    ed_post_detail.setError("Enter some content");
                }
                else {
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
                        /*AndroidNetworking.upload("http://campuscrowd.herokuapp.com/users/uploadavatar")
                                .addMultipartParameter("key","avatar")
                                .addMultipartFile("value",file)
                                .setTag("uploadAvatar")
                                .setPriority(Priority.MEDIUM)
                                .build()*/
                                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                        AndroidNetworking.post("http://campuscrowd.herokuapp.com/users/uploadavatar")
                                .addFileBody(file)// posting any type of file
                                .setTag("test")
                                .setPriority(Priority.MEDIUM)
                                .build()
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
                                                Toast.makeText(AddPostActivity.this, avtar_filename, Toast.LENGTH_LONG).show();
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("body",postDetail);
                                                    jsonObject.put("id",id);
                                                    jsonObject.put("fullname",username);
                                                    if(!avatar.equals("null")){
                                                        jsonObject.put("avatar",avatar);
                                                    }else{
                                                        jsonObject.put("avatar","");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                AndroidNetworking.post("http://campuscrowd.herokuapp.com/posts/add")
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
                    else{
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("body",postDetail);
                            jsonObject.put("id",id);
                            jsonObject.put("fullname",username);
                            jsonObject.put("avatar","");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AndroidNetworking.post("http://campuscrowd.herokuapp.com/posts/add")
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
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                bitmap = (Bitmap) data.getExtras().get("data");
                iv_post.setImageBitmap(bitmap);
            }
            else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));
                iv_post.setImageBitmap(bitmap);
            }
        }
    }
}
