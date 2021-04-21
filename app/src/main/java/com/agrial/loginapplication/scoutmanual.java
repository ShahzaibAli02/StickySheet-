package com.agrial.loginapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class scoutmanual extends AppCompatActivity {




    static  CheckBox Potato_Aphid,Horn_Worms,CutWorms,Trips,Tatu;
    static boolean isRun=false;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoutmanual);

        ///////// SCOUT MANUAL ///////////////////
        Potato_Aphid=findViewById(R.id.Potato_Aphid);
        Horn_Worms=findViewById(R.id.Horm_Worms);
        CutWorms=findViewById(R.id.Cut_Worms);
        Trips=findViewById(R.id.Trips);
        Tatu=findViewById(R.id.Tatu);
        isRun=true;
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(scoutmanual.this,SendFieldActivity.class));
    }
}
