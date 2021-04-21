package com.agrial.loginapplication.Model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OfflineModel
{



    @PrimaryKey(autoGenerate = true)
    public int uid;


    @ColumnInfo(name = "fieldnr")
    private String fieldnr;


    @ColumnInfo(name = "pathnr")
    private  String pathnr;

    public String getLeafdisease() {
        return leafdisease;
    }

    public void setLeafdisease(String leafdisease) {
        this.leafdisease = leafdisease;
    }

    @ColumnInfo(name = "leafdisease")
    private  String leafdisease;


    public String getNewStickyPlate() {
        return newStickyPlate;
    }

    public void setNewStickyPlate(String newStickyPlate) {
        this.newStickyPlate = newStickyPlate;
    }

    @ColumnInfo(name = "newStickyPlate")
    private  String newStickyPlate;




    @ColumnInfo(name = "greenhousename")
    private String greenhousename;


    public String getGreenhousename() {
        return greenhousename;
    }

    public void setGreenhousename(String greenhousename) {
        this.greenhousename = greenhousename;
    }

    public String getGreenHouseSection() {
        return greenHouseSection;
    }

    public void setGreenHouseSection(String greenHouseSection) {
        this.greenHouseSection = greenHouseSection;
    }

    @ColumnInfo(name = "greenHouseSection")
    private  String greenHouseSection;


    public String getScoutingtype() {
        return scoutingtype;
    }

    public void setScoutingtype(String scoutingtype) {
        this.scoutingtype = scoutingtype;
    }

    @ColumnInfo(name = "scoutingtype")
    private  String scoutingtype;

    @ColumnInfo(name = "datumtijd")
    private String datumtijd;

    @ColumnInfo(name = "imgview_id_1")
    private String imgview_id_1;

    @ColumnInfo(name = "imgview_id_2")
    private String imgview_id_2="NAN";

    @ColumnInfo(name = "Potato_Aphid")
    private String Potato_Aphid="NAN";

    @ColumnInfo(name = "Horn_Worms")
    private String Horn_Worms="NAN";

    @ColumnInfo(name = "Flee_Beetles")
    private String Flee_Beetles="NAN";

    @ColumnInfo(name = "Trips")
    private String Trips="Trips";

    @ColumnInfo(name = "email")
    private String email="NAN";

    @ColumnInfo(name = "Tuta")
    private String Tuta="NAN";

    @ColumnInfo(name = "imagepath1")
    private  String imagepath1;



    @ColumnInfo(name = "imagepath2")
    private  String imagepath2;







    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFieldnr() {
        return fieldnr;
    }

    public void setFieldnr(String fieldnr) {
        this.fieldnr = fieldnr;
    }

    public String getPathnr() {
        return pathnr;
    }

    public void setPathnr(String pathnr) {
        this.pathnr = pathnr;
    }

    public String getDatumtijd() {
        return datumtijd;
    }

    public void setDatumtijd(String datumtijd) {
        this.datumtijd = datumtijd;
    }

    public String getImgview_id_1() {
        return imgview_id_1;
    }

    public void setImgview_id_1(String imgview_id_1) {
        this.imgview_id_1 = imgview_id_1;
    }

    public String getImgview_id_2() {
        return imgview_id_2;
    }

    public void setImgview_id_2(String imgview_id_2) {
        this.imgview_id_2 = imgview_id_2;
    }

    public String getPotato_Aphid() {
        return Potato_Aphid;
    }

    public void setPotato_Aphid(String potato_Aphid) {
        Potato_Aphid = potato_Aphid;
    }

    public String getHorn_Worms() {
        return Horn_Worms;
    }

    public void setHorn_Worms(String horn_Worms) {
        Horn_Worms = horn_Worms;
    }

    public String getFlee_Beetles() {
        return Flee_Beetles;
    }

    public void setFlee_Beetles(String flee_Beetles) {
        Flee_Beetles = flee_Beetles;
    }

    public String getTrips() {
        return Trips;
    }

    public void setTrips(String trips) {
        Trips = trips;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTuta() {
        return Tuta;
    }

    public void setTuta(String tuta) {
        Tuta = tuta;
    }

    public String getImagepath1() {
        return imagepath1;
    }

    public void setImagepath1(String imagepath1) {
        this.imagepath1 = imagepath1;
    }

    public String getImagepath2() {
        return imagepath2;
    }

    public void setImagepath2(String imagepath2) {
        this.imagepath2 = imagepath2;
    }




}
