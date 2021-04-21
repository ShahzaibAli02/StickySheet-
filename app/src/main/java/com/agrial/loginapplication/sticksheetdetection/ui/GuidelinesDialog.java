package com.agrial.loginapplication.sticksheetdetection.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.agrial.loginapplication.R;

/**
 * Created by Junaid Ali on 04,March,2020
 */
public class GuidelinesDialog extends Dialog {

    private Button gotItButton;

    private Context context;
    private GuidelinesDialogListener guidelinesDialogListener;

    public GuidelinesDialog(@NonNull Context context,GuidelinesDialogListener guidelinesDialogListener) {
        super(context);
        this.context = context;
        this.guidelinesDialogListener = guidelinesDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.dialog_guidelines,null);
        setContentView(view);

        gotItButton = view.findViewById(R.id.btn_got_it);

        if (getWindow()!=null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // to adjust the dialog when soft keyboard appears
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guidelinesDialogListener.onGotItButtonClick();
                dismiss();
            }
        });

        setCancelable(false);
    }

    public interface GuidelinesDialogListener{
        void onGotItButtonClick();
    }
}
