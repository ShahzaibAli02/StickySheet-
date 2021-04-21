package com.agrial.loginapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.agrial.loginapplication.Model.GreenHouse;
import com.agrial.loginapplication.Model.Member;
import com.agrial.loginapplication.Model.OfflineModel;
import com.agrial.loginapplication.OfflineHandler.OfflineHandler;
import com.agrial.loginapplication.sticksheetdetection.ui.LiveObjectDetectionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class SendFieldActivity extends AppCompatActivity {

    private EditText etField,etPath;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference reff;
    private String fieldnr ;
    private String datumtijd;
    private String imgview_id_1;
    private String imgview_id_2="NAN";

    private TextView btn_upload;
    private String pathToFile;
    private ImageView targetimg1,targetimg2;

    private  ImageView targetImg;
    private Bitmap bitmap;
    private String laatste_actie;


    private StorageReference mStorageRef;
    private Uri imguri_1;
    private Uri imguri_2;
    private StorageTask uploadTask;
    AlertDialog alertDialog;


    ImageView clearimg1,clearimg2;



    static boolean isShowWarning=true;

    EditText Potato_Aphid,Horn_Worms,CutWorms,Trips,Tatu;

    Button btn_SendCache;

    String userEmail;


    TextView txtgreenhosueid;
    TextView txtgreenhosuename;

    LinearLayout linearLayoutImage2;

    public  boolean isSelectedImage2=false;
    public   boolean isSelectedImage1=false;
    TextView txtLeafModus;
    Spinner spnrDisease;
    CardView crdLeafDisease;
    Switch newStickyPlate;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_field);
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        etField = findViewById(R.id.etField);
        btn_SendCache=findViewById(R.id.btn_SendCache);
        txtLeafModus=findViewById(R.id.txtLeafModus);
        spnrDisease=findViewById(R.id.spnrDisease);
        crdLeafDisease=findViewById(R.id.crdLeafDisease);
        etPath=findViewById(R.id.etPath);
        newStickyPlate=findViewById(R.id.newStickyPlate);
        reff= FirebaseDatabase.getInstance().getReference().child("Member");
        btn_upload = findViewById(R.id.txtUpload);
        targetimg1 = findViewById(R.id.targetimg1);
        targetimg2=findViewById(R.id.targetimg2);
        linearLayoutImage2=findViewById(R.id.linearLayoutImage2);
        imgview_id_1 = null;
        laatste_actie = null;
        isSelectedImage2=false;
        isSelectedImage1=false;
        clearimg1=findViewById(R.id.clearimg1);
        clearimg2=findViewById(R.id.clearimg2);


        Potato_Aphid=findViewById(R.id.Potato_Aphid);
        Horn_Worms=findViewById(R.id.Horm_Worms);
        CutWorms=findViewById(R.id.Cut_Worms);
        Trips=findViewById(R.id.Trips);
        Tatu=findViewById(R.id.Tatu);

        txtgreenhosueid=findViewById(R.id.txtgreenhosueid);
        txtgreenhosuename=findViewById(R.id.txtgreenhouse);


        setVals(spnrDisease);
        clearimg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                targetimg1.setImageDrawable(getResources().getDrawable(R.drawable.ic_default));
                isSelectedImage1=false;
                clearimg1.setVisibility(View.GONE);
            }
        });
        clearimg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetimg2.setImageDrawable(getResources().getDrawable(R.drawable.ic_default));
                isSelectedImage2=false;
                clearimg2.setVisibility(View.GONE);
            }
        });



        txtgreenhosuename.setText(SharedPreff.getGreenHouse(this).getGreenhousename());
        txtgreenhosueid.setText(SharedPreff.getGreenHouse(this).getSection());


        if(SharedPreff.getisLeafDisease(this))
        {
            SharedPreff.setTwoSidedOn(this,false);
            crdLeafDisease.setVisibility(View.VISIBLE);
            txtLeafModus.setText(getString(R.string.leaf_modus));
            newStickyPlate.setVisibility(View.GONE);
        }
        else
        {
            crdLeafDisease.setVisibility(View.GONE);
            newStickyPlate.setVisibility(View.VISIBLE);
            txtLeafModus.setText(getString(R.string.sticky_plate_modus));
        }

        if(SharedPreff.getOfflineVal(SendFieldActivity.this))
        {
            btn_SendCache.setVisibility(View.GONE);
            btn_upload.setText("Upload");
        }
        else
        {
            btn_SendCache.setVisibility(View.VISIBLE);
            btn_upload.setText("Store picture");
        }

        if(SharedPreff.getisTwoSidedOn(SendFieldActivity.this))
        {
            targetimg2.setVisibility(View.VISIBLE);
            linearLayoutImage2.setVisibility(View.VISIBLE);
        }
        else
        {
            targetimg2.setVisibility(View.GONE);
            linearLayoutImage2.setVisibility(View.GONE);
        }

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        else
            finish();

        configureEditTexts();



        targetimg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                targetImg=targetimg1;
                dispatchPictureTakerAction(1);
            }
        });
        targetimg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                targetImg=targetimg2;
                dispatchPictureTakerAction(2);
            }
        });

        btn_SendCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if(connectionType().equalsIgnoreCase("Nan"))
                {
                    Toast.makeText(SendFieldActivity.this,"No Internet | Cannot Upload Without Internet",Toast.LENGTH_LONG).show();
                    return;
                }
                if(connectionType().equalsIgnoreCase("wifi"))
                {
                    OfflineHandler.UploadtoServer(SendFieldActivity.this);
                    return;
                }
                if(connectionType().equalsIgnoreCase("Data"))
                {
                    showWarningMsg();
                }


            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                if (SharedPreff.getOfflineVal(SendFieldActivity.this) && connectionType().equalsIgnoreCase("Nan")) {
                    Toast.makeText(SendFieldActivity.this, "No Internet | Cannot Upload Without Internet", Toast.LENGTH_LONG).show();
                    return;
                }

                fieldnr = etField.getText().toString();
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(SendFieldActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else if (fieldnr.isEmpty()) {
                    etField.setError("Please Fill Out This");
                    etField.requestFocus();
                } else if (TextUtils.isEmpty(etPath.getText().toString())) {
                    etPath.setError("Please Fill Out This");
                    etPath.requestFocus();
                } else
                    if (!isSelectedImage1)
                    {
                    Toast.makeText(SendFieldActivity.this, "Please select a image or take a picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                    else
                    if (targetimg2.getVisibility() == View.VISIBLE && !isSelectedImage2)
                {
                    Toast.makeText(SendFieldActivity.this, "Please select 2nd image or take a picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    {
                        if(scoutmanual.isRun && !SharedPreff.getisLeafDisease(SendFieldActivity.this))
                        {

                            if(scoutmanual.Potato_Aphid.isChecked() && TextUtils.isEmpty(Potato_Aphid.getText().toString()))
                            {
                                Potato_Aphid.setError("This Field Is Required");
                                Potato_Aphid.requestFocus();
                                return;
                            }
                            if(scoutmanual.Horn_Worms.isChecked() && TextUtils.isEmpty(Horn_Worms.getText().toString()))
                            {
                                Horn_Worms.setError("This Field Is Required");
                                Horn_Worms.requestFocus();
                                return;
                            }
                            if(scoutmanual.CutWorms.isChecked() && TextUtils.isEmpty(CutWorms.getText().toString()))
                            {
                                CutWorms.setError("This Field Is Required");
                                CutWorms.requestFocus();
                                return;
                            }

                            if(scoutmanual.Trips.isChecked() && TextUtils.isEmpty(Trips.getText().toString()))
                            {
                                Trips.setError("This Field Is Required");
                                Trips.requestFocus();
                                return;
                            }

                            if(scoutmanual.Tatu.isChecked() && TextUtils.isEmpty(Tatu.getText().toString()))
                            {
                                Tatu.setError("This Field Is Required");
                                Tatu.requestFocus();
                                return;
                            }




                        }






                        if (laatste_actie.equals("selected_image_upload"))
                        {
                            imgview_id_1 = System.currentTimeMillis()+"."+getExtension(imguri_1);
                        }
                        else
                        {
                            imgview_id_1 = System.currentTimeMillis()+".jpeg";
                        }
                        if(targetimg2.getVisibility()==View.VISIBLE)
                        {
                            if (laatste_actie.equals("selected_image_upload"))
                            {
                                imgview_id_2 = new Random().nextInt(1000)+System.currentTimeMillis()+"."+getExtension(imguri_2);
                            }
                            else
                            {
                                imgview_id_2 = new Random().nextInt(1000)+System.currentTimeMillis()+new Random().nextInt(1000) +".jpeg";
                            }
                        }
                        else
                            imgview_id_2="NAN";




                        if(txtgreenhosuename.getText().toString().equalsIgnoreCase("NAN"))
                        {
                            Toast.makeText(SendFieldActivity.this, "Please Select Green House Name And Section From Menu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        datumtijd = new Date().toString();

                        // NEW CODE///
                        String potato_Aphid=TextUtils.isEmpty(Potato_Aphid.getText().toString())?"NAN":Potato_Aphid.getText().toString();
                        String horn_Worms=TextUtils.isEmpty(Horn_Worms.getText().toString())?"NAN":Horn_Worms.getText().toString();
                        String cutWorms=TextUtils.isEmpty(CutWorms.getText().toString())?"NAN":CutWorms.getText().toString();
                        String trips=TextUtils.isEmpty(Trips.getText().toString())?"NAN":Trips.getText().toString();
                        String tatu=TextUtils.isEmpty(Tatu.getText().toString())?"NAN":Tatu.getText().toString();


                        Member member=new Member();
                        member.setPotato_Aphid(potato_Aphid);
                        member.setHorn_Worms(horn_Worms);
                        member.setFlee_Beetles(cutWorms);
                        member.setTrips(trips);
                        member.setTuta(tatu);
                        if(!SharedPreff.getisLeafDisease(SendFieldActivity.this))
                        {
                            member.setNewStickyPlate(String.valueOf(newStickyPlate.isChecked()));
                        }

                        if(SharedPreff.getisLeafDisease(SendFieldActivity.this))
                            member.setLeafDisease(spnrDisease.getSelectedItem().toString());

                        member.setGreenHouseName(txtgreenhosuename.getText().toString());
                        member.setGreenHouseSection(txtgreenhosueid.getText().toString());

                        if(SharedPreff.getisLeafDisease(SendFieldActivity.this))
                            member.setScoutingType("Leaf Disease");
                        else
                            member.setScoutingType("Insects");


                        //////////////////////////

                        member.setFieldnr(fieldnr);
                        member.setEmail(userEmail);
                        member.setPathnr(etPath.getText().toString());
                        member.setDatumtijd(datumtijd);
                        member.setImgview_id_1(imgview_id_1);
                        member.setImgview_id_2(imgview_id_2);

                        decideWhereToSave(member);
                    }

            }
        });

    }

    private void setVals(Spinner spnrDisease)
    {


        FirebaseDatabase.getInstance().getReference("DropDownLeafDisease").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> leafdisease=new ArrayList<>();

                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    leafdisease.add(String.valueOf(child.getValue()));
                }

                spnrDisease.setAdapter(new ArrayAdapter<>(SendFieldActivity.this,R.layout.support_simple_spinner_dropdown_item,leafdisease));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public  void showWarningMsg()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Warning");
        dialog.setMessage("Are you sure that you want to send the cache without using WIFI?");
        dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                OfflineHandler.UploadtoServer(SendFieldActivity.this);

            }
        });
        dialog.setNegativeButton("Don't send the cache yet",null);
        dialog.show();
    }



    public  void showPopup()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        View child = getLayoutInflater().inflate(R.layout.popup, null);

        builder1.setView(child);
        builder1.setCancelable(true);
        final AlertDialog dialog = builder1.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        initPopup(child,dialog);
    }

    public  void showPopup_GreeHouse()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        View child = getLayoutInflater().inflate(R.layout.popup_greenhouse, null);
        builder1.setView(child);
        builder1.setCancelable(true);
        final AlertDialog dialog = builder1.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        initPopup_greenHouse(child,dialog);
    }

    private void initPopup_greenHouse(View child, AlertDialog dialog)
    {

        ArrayList<String> houseNames=new ArrayList<>();
        ArrayList<String> houseids=new ArrayList<>();


        Spinner spnrNames,spnrids;

        TextView txtSave;
        spnrNames=child.findViewById(R.id.spnrNames);
        spnrids=child.findViewById(R.id.spnrId);
        txtSave=child.findViewById(R.id.txtSave);


        getData(houseNames,houseids,spnrNames,spnrids);

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(houseNames.size()<1)
                    return;
                else
                {
                    String name=spnrNames.getSelectedItem().toString();
                    String id= spnrids.getSelectedItem().toString();
                    SharedPreff.setGreenHouse(SendFieldActivity.this,name,id);
                    Toast.makeText(SendFieldActivity.this,"Saved",Toast.LENGTH_SHORT).show();
                    txtgreenhosueid.setText(id);
                    txtgreenhosuename.setText(name);
                    dialog.dismiss();
                }
            }
        });

    }

    private void getData(ArrayList<String> houseNames, ArrayList<String> houseids, Spinner spnrNames, Spinner spnrids)
    {


        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            finish();



        ProgressDialog pb=new ProgressDialog(this);
        pb.setMessage("Hold On...");
        pb.setCancelable(false);
        pb.show();


        String userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();

//TEMP
        FirebaseDatabase.getInstance().getReference("dropdown_info").orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                pb.dismiss();



                for(DataSnapshot singleChild:dataSnapshot.getChildren())
                {
                    GreenHouse val=singleChild.getValue(GreenHouse.class);
                    if(!notInHouseName(houseNames,val.getGreenhousename()))
                    {
                        houseNames.add(val.getGreenhousename());
                    }
                    houseids.add(val.getSection());
                }

                if(houseNames.size()<1)
                {
                    Toast.makeText(SendFieldActivity.this, "No Data found On Your Email  "+userEmail, Toast.LENGTH_SHORT).show();
                }
                else

                {

                    spnrNames.setAdapter(new ArrayAdapter<>(SendFieldActivity.this,R.layout.support_simple_spinner_dropdown_item,houseNames));
                    spnrids.setAdapter(new ArrayAdapter<>(SendFieldActivity.this,R.layout.support_simple_spinner_dropdown_item,houseids));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private boolean notInHouseName(ArrayList<String> houseNames, String greenhousename)
    {

        for(String housName:houseNames)
        {
            if(housName.equalsIgnoreCase(greenhousename))
                return true;
        }

        return false;
    }

    private void initPopup(View child, AlertDialog dialog)
    {




        Button btnlogout=child.findViewById(R.id.btnLogout);
        Button btnOn=child.findViewById(R.id.btnOn);
        Button btnOff=child.findViewById(R.id.btnOff);
        Button btnOnTwoSided=child.findViewById(R.id.btnOnTwoSided);
        Button btnOffTwoSided=child.findViewById(R.id.btnOffTwoSided);
        Button btnBack=child.findViewById(R.id.btnback);


        Button btnStickyPlate=child.findViewById(R.id.btnStickyPlate);
        Button btnLeafeDisease=child.findViewById(R.id.btnLeafDisease);

        TextView txtManualScout=child.findViewById(R.id.txtManualScout);
        TextView txtGreenHouse=child.findViewById(R.id.txtChangeGreenHouse);

        TextView txtUserEmail=child.findViewById(R.id.txtUserEmail);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            txtUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());



        if(SharedPreff.getisTwoSidedOn(SendFieldActivity.this))
        {
            btnOnTwoSided.setBackground(getResources().getDrawable(R.drawable.rounder_green));
            btnOnTwoSided.setTextColor(Color.WHITE);


            btnOffTwoSided.setBackgroundColor(Color.WHITE);
            btnOffTwoSided.setTextColor(Color.BLACK);

        }
        else
        {
            btnOffTwoSided.setBackground(getResources().getDrawable(R.drawable.rounded_button));
            btnOffTwoSided.setTextColor(Color.WHITE);
            btnOnTwoSided.setBackgroundColor(Color.WHITE);
            btnOnTwoSided.setTextColor(Color.BLACK);
        }


        if(SharedPreff.getOfflineVal(SendFieldActivity.this))
        {
            btnOn.setBackground(getResources().getDrawable(R.drawable.rounder_green));
            btnOn.setTextColor(Color.WHITE);


            btnOff.setBackgroundColor(Color.WHITE);
            btnOff.setTextColor(Color.BLACK);

        }
        else
        {
            btnOff.setBackground(getResources().getDrawable(R.drawable.rounded_button));
            btnOff.setTextColor(Color.WHITE);


            btnOn.setBackgroundColor(Color.WHITE);
            btnOn.setTextColor(Color.BLACK);

        }


        if(SharedPreff.getisLeafDisease(SendFieldActivity.this))
        {
            btnLeafeDisease.setBackground(getResources().getDrawable(R.drawable.rounder_green));
            btnLeafeDisease.setTextColor(Color.WHITE);


            btnStickyPlate.setBackgroundColor(Color.WHITE);
            btnStickyPlate.setTextColor(Color.BLACK);

        }
        else
        {
            btnStickyPlate.setBackground(getResources().getDrawable(R.drawable.rounder_green));
            btnStickyPlate.setTextColor(Color.WHITE);


            btnLeafeDisease.setBackgroundColor(Color.WHITE);
            btnLeafeDisease.setTextColor(Color.BLACK);

        }



        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if(v==btnBack)
                     dialog.dismiss();



                if(v==btnOn)
                {
                    btnOn.setBackground(getResources().getDrawable(R.drawable.rounder_green));
                    btnOn.setTextColor(Color.WHITE);


                    btnOff.setBackgroundColor(Color.WHITE);
                    btnOff.setTextColor(Color.BLACK);

                    SharedPreff.setOfflineVal(SendFieldActivity.this,true);
                    isShowWarning=true;
                    btn_upload.setText("Upload");
                    btn_SendCache.setVisibility(View.GONE);
                }
                if(v==btnOff)
                {

                    btnOff.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    btnOff.setTextColor(Color.WHITE);


                    btnOn.setBackgroundColor(Color.WHITE);
                    btnOn.setTextColor(Color.BLACK);

                    SharedPreff.setOfflineVal(SendFieldActivity.this,false);
                    isShowWarning=true;
                    btn_upload.setText("Store picture");
                    btn_SendCache.setVisibility(View.VISIBLE);
                }

                if(v==btnOnTwoSided)
                {


                    if(SharedPreff.getisLeafDisease(SendFieldActivity.this))
                    {
                        Toast.makeText(SendFieldActivity.this,"Cant Enable Two Sides When Leaf Disease Scouting Is On ",Toast.LENGTH_LONG).show();
                        return;
                    }
                    btnOnTwoSided.setBackground(getResources().getDrawable(R.drawable.rounder_green));
                    btnOnTwoSided.setTextColor(Color.WHITE);


                    btnOffTwoSided.setBackgroundColor(Color.WHITE);
                    btnOffTwoSided.setTextColor(Color.BLACK);

                    SharedPreff.setTwoSidedOn(SendFieldActivity.this,true);
                    targetimg2.setVisibility(View.VISIBLE);
                    linearLayoutImage2.setVisibility(View.VISIBLE);
                }
                if(v==btnOffTwoSided)
                {

                    btnOffTwoSided.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    btnOffTwoSided.setTextColor(Color.WHITE);
                    btnOnTwoSided.setBackgroundColor(Color.WHITE);
                    btnOnTwoSided.setTextColor(Color.BLACK);
                    SharedPreff.setTwoSidedOn(SendFieldActivity.this,false);
                    targetimg2.setVisibility(View.GONE);
                    linearLayoutImage2.setVisibility(View.GONE);
                }

                if(v==txtManualScout)
                {

                    if(SharedPreff.getisLeafDisease(SendFieldActivity.this))
                    {
                        Toast.makeText(SendFieldActivity.this,"Cant Add Other Insects When Leaf Disease Scouting Is On ",Toast.LENGTH_LONG).show();
                        return;
                    }
                    finish();startActivity(new Intent(SendFieldActivity.this,scoutmanual.class));
                    dialog.dismiss();
                }
                if(v==txtGreenHouse)
                {
                    showPopup_GreeHouse();
                    dialog.dismiss();
                }


                if(v==btnlogout)
                {

                    FirebaseAuth.getInstance().signOut();
                    ActivityCompat.finishAffinity(SendFieldActivity.this);
                    startActivity(new Intent(SendFieldActivity.this,LoginActivity.class));

                }


                if(v==btnLeafeDisease)
                {

                    newStickyPlate.setVisibility(View.GONE);
                    btnOffTwoSided.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    btnOffTwoSided.setTextColor(Color.WHITE);
                    btnOnTwoSided.setBackgroundColor(Color.WHITE);
                    btnOnTwoSided.setTextColor(Color.BLACK);

                    targetimg2.setVisibility(View.GONE);
                    linearLayoutImage2.setVisibility(View.GONE);


                    crdLeafDisease.setVisibility(View.VISIBLE);
                    txtLeafModus.setText(getString(R.string.leaf_modus));


                    btnLeafeDisease.setBackground(getResources().getDrawable(R.drawable.rounder_green));
                    btnLeafeDisease.setTextColor(Color.WHITE);


                    btnStickyPlate.setBackgroundColor(Color.WHITE);
                    btnStickyPlate.setTextColor(Color.BLACK);



                    SharedPreff.setTwoSidedOn(SendFieldActivity.this,false);
                    SharedPreff.setLeafDisease(SendFieldActivity.this,true);



                    DisableOtherInsectsIfAny();


                }

                if(v==btnStickyPlate)
                {



                    crdLeafDisease.setVisibility(View.GONE);
                    txtLeafModus.setText(getString(R.string.sticky_plate_modus));
                    newStickyPlate.setVisibility(View.VISIBLE);

                    SharedPreff.setLeafDisease(SendFieldActivity.this,false);
                    btnStickyPlate.setBackground(getResources().getDrawable(R.drawable.rounder_green));
                    btnStickyPlate.setTextColor(Color.WHITE);
                    btnLeafeDisease.setBackgroundColor(Color.WHITE);
                    btnLeafeDisease.setTextColor(Color.BLACK);
                }



            }
        };
        btnOn.setOnClickListener(onClickListener);
        btnOff.setOnClickListener(onClickListener);
        btnOnTwoSided.setOnClickListener(onClickListener);
        btnOffTwoSided.setOnClickListener(onClickListener);
        txtManualScout.setOnClickListener(onClickListener);
        txtGreenHouse.setOnClickListener(onClickListener);
        btnlogout.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);

        btnLeafeDisease.setOnClickListener(onClickListener);
        btnStickyPlate.setOnClickListener(onClickListener);

    }

    private void DisableOtherInsectsIfAny()
    {

        if(!scoutmanual.isRun)
            return;


        for(int crdIds:new int[]{R.id.cardCountTuta,R.id.cardCountPotato,R.id.cardHornWorm,R.id.cardCountTrips,R.id.cardCountFlee})
        {
            CardView cardView = findViewById(crdIds);
            cardView.setVisibility(View.GONE);
        }
        for(int edittextids:new int[]{R.id.Cut_Worms,R.id.Potato_Aphid,R.id.Horm_Worms,R.id.Trips,R.id.Tatu})
        {
            EditText editText = findViewById(edittextids);
            editText.setText(null);
        }

    }


    public void configureEditTexts()
    {

        if(!scoutmanual.isRun  || SharedPreff.getisLeafDisease(SendFieldActivity.this))
            return;






        if(scoutmanual.Tatu.isChecked())
            findViewById(R.id.cardCountTuta).setVisibility(View.VISIBLE);
        if(scoutmanual.Potato_Aphid.isChecked())
            findViewById(R.id.cardCountPotato).setVisibility(View.VISIBLE);
        if(scoutmanual.Horn_Worms.isChecked())
            findViewById(R.id.cardHornWorm).setVisibility(View.VISIBLE);
        if(scoutmanual.Trips.isChecked())
            findViewById(R.id.cardCountTrips).setVisibility(View.VISIBLE);
        if(scoutmanual.CutWorms.isChecked())
            findViewById(R.id.cardCountFlee).setVisibility(View.VISIBLE);

    }



    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(((ContentResolver) cr).getType(uri));
    }



    private void dispatchPictureTakerAction(int requestCode)
    {



        if(SharedPreff.getisLeafDisease(this))
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createPhotoFile();
                } catch (IOException ex) {
                    Toast.makeText(SendFieldActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.agrial.loginapplication.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 1);
                }
                else
                {
                    startActivityForResult(takePictureIntent, 2);
                }

            }
        }
        else

        {
            Intent intent = new Intent(this, LiveObjectDetectionActivity.class);
            File photoFile;
            try {
                photoFile = createPhotoFile();
                intent.putExtra(LiveObjectDetectionActivity.FILE_PATH, photoFile.getAbsolutePath());
                startActivityForResult(intent, requestCode);
            }
            catch (IOException e){
                Toast.makeText(SendFieldActivity.this,"Error creating file",Toast.LENGTH_SHORT).show();
            }
        }





    }

    private File createPhotoFile() throws  IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        pathToFile = image.getAbsolutePath();
        return image;
    }


     void ShowDialog(Member member)
     {
         AlertDialog.Builder alert = new AlertDialog.Builder(SendFieldActivity.this);
         LinearLayout layout = new LinearLayout(SendFieldActivity.this);
         layout.setOrientation(LinearLayout.VERTICAL);
         String Message="";
         if(!SharedPreff.getOfflineVal(SendFieldActivity.this))
         {
             Message="You are currently using offline modus, the data is saved on your mobile device. The data can be send to our server when you are using wifi";
            Toast.makeText(SendFieldActivity.this,"You Are Getting This Message Because Offline Modus Is On",Toast.LENGTH_LONG).show();
         }
        else
         {
             Message="You are currently using online modus, the data is send immediately to our server";
             Toast.makeText(SendFieldActivity.this,"You Are Getting This Message Because Offline Modus Is Off",Toast.LENGTH_LONG).show();
         }
         final ImageView noInternetImage = new ImageView(SendFieldActivity.this);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
             noInternetImage.setAlpha(0.79f);
         }
         noInternetImage.setImageDrawable(getResources().getDrawable(R.drawable.warning));
         final TextView Title = new TextView(SendFieldActivity.this);
         final Button offline = new Button(SendFieldActivity.this);
         final Button uploadAnyway = new Button(SendFieldActivity.this);
         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.WRAP_CONTENT,
                 LinearLayout.LayoutParams.MATCH_PARENT
         );
         params.setMargins(0, 30, 0, 30);
         //offline.setLayoutParams(params);
         offline.setText("I understand, do not show me this message again");
         offline.setTextSize(13f);
         offline.setTextColor(Color.WHITE);
         offline.setLayoutParams(params);
         offline.setPadding(10,10,10,10);
         offline.setTypeface(Typeface.SERIF);
         offline.setBackground(getResources().getDrawable(R.drawable.rounded_button2));

         Title.setPadding(10, 10, 10, 10);
         Title.setText(Message);
         Title.setTypeface(Typeface.SERIF);
         Title.setTextSize(15f);
         Title.setGravity(Gravity.CENTER);
         layout.addView(noInternetImage);
         layout.addView(Title);
         //layout.addView(uploadAnyway);
         layout.addView(offline);
         layout.setPadding(30, 20, 30, 20);
         alert.setView(layout);
         alert.setCancelable(false);
         final AlertDialog testDialog = alert.create();
         testDialog.show();
         offline.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                 testDialog.dismiss();
                 if(!SharedPreff.getOfflineVal(SendFieldActivity.this))
                 {

                     saveInOfflineDataBase(member);

                 }
                 else
                 {

                     saveInOnlineServer(member);
                 }
             }
         });
    }
    public void saveInOfflineDataBase(Member member)
    {


        Bitmap bitmap = ((BitmapDrawable) targetimg1.getDrawable()).getBitmap();
        Bitmap bitmap2 = targetimg2.getVisibility()==View.VISIBLE?((BitmapDrawable) targetimg2.getDrawable()).getBitmap():null;
        OfflineModel obj=new OfflineModel();
        obj.setImgview_id_1(imgview_id_1);
        obj.setImgview_id_2(imgview_id_2);
        obj.setDatumtijd(datumtijd);
        obj.setFieldnr(member.getFieldnr());
        obj.setPathnr(member.getPathnr());
        obj.setNewStickyPlate(member.getNewStickyPlate());
        obj.setLeafdisease(member.getLeafDisease());
        obj.setGreenhousename(member.getGreenHouseName());
        obj.setGreenHouseSection(member.getGreenHouseSection());
        obj.setScoutingtype(member.getScoutingType());
        obj.setPotato_Aphid(member.getPotato_Aphid());
        obj.setHorn_Worms(member.getHorn_Worms());
        obj.setFlee_Beetles(member.getFlee_Beetles());
        obj.setTrips(member.getTrips());
        obj.setTuta(member.getTuta());
        obj.setEmail(member.getEmail());
        OfflineHandler.saveOffline(SendFieldActivity.this,obj,bitmap,bitmap2);

    }


    public void decideWhereToSave(Member member)
    {

        if(isShowWarning)
        {
            isShowWarning=false;
            ShowDialog(member);
        }
        else
        {
            if(!SharedPreff.getOfflineVal(SendFieldActivity.this))
            {

                saveInOfflineDataBase(member);

            }
            else
            {
                saveInOnlineServer(member);
            }

        }
    }


    private void saveInOnlineServer(Member member)
    {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Upload in progress");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        reff.push().setValue(member);

         // Upload 1st Image
        uploadImage(progressDialog,((BitmapDrawable) targetimg1.getDrawable()).getBitmap(),imgview_id_1,1);

    }

    public  void uploadImage(ProgressDialog progressDialog,Bitmap imageToUpload,String storageRef,int numImg)
    {
        StorageReference Ref = mStorageRef.child(storageRef);
        targetimg1.setDrawingCacheEnabled(true);
        targetimg1.buildDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageToUpload.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
         // Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        UploadTask uploadTask = Ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads


                Toast.makeText(SendFieldActivity.this, "Failed To  Upload", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                if(targetimg2.getVisibility()==View.VISIBLE && numImg!=2)
                {
                    uploadImage(progressDialog,((BitmapDrawable) targetimg2.getDrawable()).getBitmap(),imgview_id_2,2);
                }
                else
                {
                    progressDialog.dismiss();
                    clearTheData();
                    Toast.makeText(SendFieldActivity.this, "Image Uploaded to the database", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void clearTheData()
    {
        finish();
        startActivity(getIntent());
    }


    public String connectionType()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null)
            return "Nan";
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED )
        {
            return "Data";
        }
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            return  "Wifi";
        }
        return  "Nan";
    }
    private void Filechooser(int requestCode)
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode==2)
        {
            clearimg2.setVisibility(View.VISIBLE);
            isSelectedImage2=true;
        }
        if(resultCode == RESULT_OK && requestCode==1)
        {
            clearimg1.setVisibility(View.VISIBLE);
            isSelectedImage1=true;
        }

        if (resultCode == RESULT_OK  && data!=null &&  data.getData() !=null)
        {


            if(requestCode==2)
            {
                imguri_2=data.getData();
                targetImg.setImageURI(imguri_2);
            }
            if(requestCode==1)
            {
                imguri_1 = data.getData();
                targetImg.setImageURI(imguri_1);
            }

            laatste_actie = "selected_image_upload";
        }
        else if (resultCode == RESULT_OK  && pathToFile !=null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            targetImg.setImageBitmap(bitmap);
            laatste_actie = "file_upload";
        }

    }

    public void setImageback(Bitmap map)
    {

        clearimg2.setVisibility(View.VISIBLE);
        isSelectedImage2=true;
        targetImg.setImageBitmap(bitmap);
        laatste_actie = "file_upload";
    }
    public void setImagefront(Bitmap map)
    {

        clearimg1.setVisibility(View.VISIBLE);
        isSelectedImage1=true;
        targetImg.setImageBitmap(bitmap);
        laatste_actie = "file_upload";
    }

    public void openpopup(View view)
    {

            showPopup();
    }
}
