package com.agrial.loginapplication.OfflineHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;

import com.agrial.loginapplication.Database.OfflineDatabase;
import com.agrial.loginapplication.Model.Member;
import com.agrial.loginapplication.Model.OfflineModel;
import com.agrial.loginapplication.OfflineDao.OfflineDao;
import com.agrial.loginapplication.SendFieldActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class OfflineHandler
{



    public static void  saveOffline(Context context, OfflineModel model, Bitmap bitmap, Bitmap bitmap2)
    {
        OfflineDatabase database = getDatabase(context);

        ProgressDialog builder = new ProgressDialog(context);
        builder.setMessage("Saving...");
        builder.setCancelable(false);
        builder.show();
        new Thread()
        {

            @Override
            public void run()
            {


                model.setImagepath1(saveToInternalStorage(context,bitmap));
                if(bitmap2!=null)
                    model.setImagepath2(saveToInternalStorage(context,bitmap2));

                database.offlineDao().insertAll(model);

                getActivity(context).runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        builder.dismiss();
                        Toast.makeText(context,"Saved Successfully",Toast.LENGTH_SHORT).show();
                        ((SendFieldActivity) context).clearTheData();
                    }
                });


            }
        }.start();


    }


    public static  OfflineDatabase getDatabase(Context mcontext)
    {
       return Room.databaseBuilder(mcontext,OfflineDatabase.class, "offlineDatabase").build();

    }

    private static String saveToInternalStorage(Context mcontext,Bitmap bitmapImage){

        if(bitmapImage==null)
            return  "NAN";

        ContextWrapper cw = new ContextWrapper(mcontext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        String fileName=System.currentTimeMillis()+".jpg";
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath()+"/"+fileName;
    }



    public static void UploadtoServer(Context mcontext)
    {

        OfflineDao offlineDao = getDatabase(mcontext).offlineDao();



        ProgressDialog pb=new ProgressDialog(mcontext);
        pb.setCancelable(false);
        pb.show();
        new Thread()
        {

            @Override
            public void run()
            {


                List<OfflineModel> all = offlineDao.getAll();
                if(all==null || all.size()<1)
                { getActivity(mcontext).runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        pb.dismiss();
                        Toast.makeText(mcontext, "No pictures in cache found. Add pictures to cache", Toast.LENGTH_LONG).show();
                    }
                });

                    return;
                }

                int uploaded=1;
                for(OfflineModel singleData:all)
                {

                    int finalUploaded = uploaded;
                    getActivity(mcontext).runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            pb.setMessage("Uploading  ( "+ finalUploaded +" of "+all.size()+" )");
                        }
                    });
                    uploadToDatabase(mcontext,singleData);
                    getDatabase(mcontext).offlineDao().delete(singleData);
                    uploaded++;
                }


                getActivity(mcontext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.dismiss();
                    }
                });
            }
        }.start();



    }

    public static Activity getActivity(Context mcontext)
    {
        return (Activity) mcontext;
    }

    private static void uploadToDatabase(Context mcontext, OfflineModel singleData)
    {


        Member member=new Member();
        member.setPotato_Aphid(singleData.getPotato_Aphid());
        member.setHorn_Worms(singleData.getHorn_Worms());
        member.setFlee_Beetles(singleData.getFlee_Beetles());
        member.setTrips(singleData.getTrips());
        member.setTuta(singleData.getTuta());
        member.setScoutingType(singleData.getScoutingtype());
        member.setGreenHouseName(singleData.getGreenhousename());
        member.setGreenHouseSection(singleData.getGreenHouseSection());

        member.setLeafDisease(singleData.getLeafdisease());
        member.setNewStickyPlate(singleData.getNewStickyPlate());
        //////////////////////////
        member.setFieldnr(singleData.getFieldnr());
        member.setEmail(singleData.getEmail());
        member.setPathnr(singleData.getPathnr());
        member.setFieldnr(singleData.getFieldnr());
        member.setDatumtijd(singleData.getDatumtijd());
        member.setImgview_id_1(singleData.getImgview_id_1());
        member.setImgview_id_2(singleData.getImgview_id_2());


        FirebaseDatabase.getInstance().getReference().child("Member").push().setValue(member);


        uploadImage(mcontext,loadImageFromStorage(singleData.getImagepath1()),singleData.getImgview_id_1());


        if(!singleData.getImgview_id_2().equalsIgnoreCase("NAN"))
        {
            uploadImage(mcontext,loadImageFromStorage(singleData.getImagepath2()),singleData.getImgview_id_2());
        }


    }
    public   static  void uploadImage(Context mcontext, Bitmap imageToUpload, String storageRef)
    {


        final boolean[] waiting = {true};
        StorageReference Ref = FirebaseStorage.getInstance().getReference("Images").child(storageRef);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageToUpload.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        UploadTask uploadTask = Ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                waiting[0] =false;
                Toast.makeText(mcontext, "Failed To  Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                waiting[0] =false;
                Toast.makeText(mcontext, "Image Uploaded to the database", Toast.LENGTH_LONG).show();

            }
        });

        while (waiting[0])
            System.out.println("Waiting....");



    }



    private static Bitmap loadImageFromStorage(String path)
    {

        try
        {

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


}
