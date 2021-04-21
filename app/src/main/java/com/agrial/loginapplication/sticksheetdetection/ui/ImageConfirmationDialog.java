package com.agrial.loginapplication.sticksheetdetection.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.agrial.loginapplication.R;
import com.agrial.loginapplication.SendFieldActivity;

/**
 * Created by Junaid Ali on 05,March,2020
 */
public class ImageConfirmationDialog extends Dialog {

    private ImageView imageView,doneImageView,cancelImageView;

    private Context context;
    private ImageConfirmationDialogListener listener;
    private Bitmap bitmap;

    public ImageConfirmationDialog(@NonNull Context context, ImageConfirmationDialogListener listener,Bitmap bitmap) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.bitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.dialog_image_confirmation,null);
        setContentView(view);

        if (getWindow()!=null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // to adjust the dialog when soft keyboard appears
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        setCancelable(false);


        TextView label = view.findViewById(R.id.label_upload);

        imageView = view.findViewById(R.id.image_confirmation);
        doneImageView = view.findViewById(R.id.image_done);
        cancelImageView = view.findViewById(R.id.image_cancel);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(),bitmap);
        roundedBitmapDrawable.setCornerRadius(15.0f);

        imageView.setImageDrawable(roundedBitmapDrawable);

        doneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();


                listener.onDoneBtnClick(bitmap);
            }
        });

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelBtnClick();
                dismiss();
            }
        });

    }


    public interface ImageConfirmationDialogListener{
        void onDoneBtnClick(Bitmap bitmap);
        void onCancelBtnClick();
    }

}
