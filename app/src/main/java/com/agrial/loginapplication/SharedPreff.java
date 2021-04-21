package com.agrial.loginapplication;

import android.content.Context;
import android.content.SharedPreferences;

import com.agrial.loginapplication.Model.GreenHouse;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreff
{



    public static void setLeafDisease(Context context,boolean value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("Agrial", MODE_PRIVATE).edit();
        editor.putBoolean("leafDisease",value);
        editor.apply();
    }
    public static boolean getisLeafDisease(Context context)   // SHAREDPREFERENCES FOR SWITCH
    {

        SharedPreferences prefs = context.getSharedPreferences("Agrial", MODE_PRIVATE);
        return prefs.getBoolean("leafDisease", false);
    }
    // SHAREDPREFERENCES FOR SWITCH
    public static void setOfflineVal(Context context,boolean value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("Agrial", MODE_PRIVATE).edit();
        editor.putBoolean("Val",value);
        editor.apply();
    }
    public static boolean getOfflineVal(Context context)   // SHAREDPREFERENCES FOR SWITCH
    {

        SharedPreferences prefs = context.getSharedPreferences("Agrial", MODE_PRIVATE);
        return prefs.getBoolean("Val", true);
    }


    // SHAREDPREFERENCES FOR REMEMBER ME
    public static void setRemVal(Context context,boolean value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("Agrial", MODE_PRIVATE).edit();
        editor.putBoolean("Remember",value);
        editor.apply();
    }
    public static boolean getRemVal(Context context)   // SHAREDPREFERENCES FOR SWITCH
    {

        SharedPreferences prefs = context.getSharedPreferences("Agrial", MODE_PRIVATE);
        return prefs.getBoolean("Remember", false);
    }


    // SHAREDPREFERENCES FOR GREENHOUSE
    public static void setGreenHouse(Context context,String  name,String id)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("Agrial", MODE_PRIVATE).edit();
        editor.putString("Name",name);
        editor.putString("Id",id);
        editor.apply();
    }
    public static GreenHouse getGreenHouse(Context context)   // SHAREDPREFERENCES FOR SWITCH
    {

        SharedPreferences prefs = context.getSharedPreferences("Agrial", MODE_PRIVATE);
        String name=prefs.getString("Name","NAN");
        String ID=prefs.getString("Id","NAN");


        GreenHouse obj=new GreenHouse();

        obj.setGreenhousename(name);
        obj.setSection(ID);
        return obj;
    }


    // SHAREDPREFERENCES FOR SWITCH
    public static void setTwoSidedOn(Context context,boolean value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("Agrial", MODE_PRIVATE).edit();
        editor.putBoolean("twosided",value);
        editor.apply();
    }
    public static boolean getisTwoSidedOn(Context context)   // SHAREDPREFERENCES FOR SWITCH
    {

        SharedPreferences prefs = context.getSharedPreferences("Agrial", MODE_PRIVATE);
        return prefs.getBoolean("twosided", true);
    }

}
