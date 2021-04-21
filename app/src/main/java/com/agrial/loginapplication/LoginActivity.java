package com.agrial.loginapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agrial.loginapplication.Model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btn_Login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        if (Build.VERSION.SDK_INT >= 23)
        {
            if((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                return;
            }



        }


        if(SharedPreff.getRemVal(LoginActivity.this))
        {

            if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            {
                finish();
                startActivity(new Intent(LoginActivity.this,SendFieldActivity.class));
                return;
            }

        }

       // EnablePersisitance();
        etEmail = findViewById(R.id.edt_user_name);
        etPassword = findViewById(R.id.edt_password);
        btn_Login = findViewById(R.id.btn_sign_in);
        findViewById(R.id.txtNewAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAccountMessage();
            }
        });
        mAuth = FirebaseAuth.getInstance();


        if(Math.ceil(getBackCameraResolutionInMp())<10)
        {
            showLowMpWarning();
        }




        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText( LoginActivity.this,  "Fill up all the entries", Toast.LENGTH_SHORT).show();
                }else
                    mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                msgRemeberMe();
                            } else {
                                Toast.makeText(LoginActivity.this,  "Login failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            }
        });
    }

    public float getBackCameraResolutionInMp()
    {
        int noOfCameras = Camera.getNumberOfCameras();
        float maxResolution = -1;
        long pixelCount = -1;
        for (int i = 0;i < noOfCameras;i++)
        {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                Camera camera = Camera.open(i);;
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0;j < cameraParams.getSupportedPictureSizes().size();j++)
                {
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution = ((float)pixelCountTemp) / (1024000.0f);
                    }
                }

                camera.release();
            }
        }

        return maxResolution;
    }
    private Bitmap loadImageFromStorage(String path)
    {

        try {

            System.out.println("PATH  : "+ path);
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;

    }
    public boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        //connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
        return  false;
    }


    private void showLowMpWarning()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Warning");
        builder.setMessage("Your CellPhone Camera Has Low MegaPixel < 10");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void newAccountMessage()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Contact Us");
        builder.setMessage("If you do not have an account, please contact us by mail info@agriai.com or visit www.agriai.nl.");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void msgRemeberMe()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Do You Wants Agrial To Remember Your Email And Password ? ");
        builder.setCancelable(true);
        builder.setPositiveButton("Remember Me", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SharedPreff.setRemVal(LoginActivity.this,true);
                finish();
                startActivity(new Intent(LoginActivity.this,SendFieldActivity.class));
            }
        });
        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreff.setRemVal(LoginActivity.this,false);
                finish();
                startActivity(new Intent(LoginActivity.this,SendFieldActivity.class));
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied Exiting", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
