package com.agrial.loginapplication.Model;

public class Member {


    private String fieldnr;
    private  String pathnr;
    private String datumtijd;
    private String imgview_id_1;
    private String imgview_id_2="NAN";
    private String Potato_Aphid="NAN";
    private String Horn_Worms="NAN";
    private String Flee_Beetles="NAN";
    private String Trips="NAN";
    private String email="NAN";
    private String Tuta="NAN";

    public String getNewStickyPlate() {
        return NewStickyPlate;
    }

    public void setNewStickyPlate(String newStickyPlate) {
        NewStickyPlate = newStickyPlate;
    }

    private String NewStickyPlate="NAN";

    public String getLeafDisease() {
        return LeafDisease;
    }

    public void setLeafDisease(String leafDisease) {
        LeafDisease = leafDisease;
    }

    private String LeafDisease="NAN";


    public String getGreenHouseName() {
        return GreenHouseName;
    }

    public void setGreenHouseName(String greenHouseName) {
        GreenHouseName = greenHouseName;
    }

    public String getGreenHouseSection() {
        return GreenHouseSection;
    }

    public void setGreenHouseSection(String greenHouseSection) {
        GreenHouseSection = greenHouseSection;
    }

    private String GreenHouseName;
    private String GreenHouseSection;

    public String getScoutingType() {
        return ScoutingType;
    }

    public void setScoutingType(String scoutingType) {
        ScoutingType = scoutingType;
    }

    private String ScoutingType="Insects";



    public String getImgview_id_1()
    {
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



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getFlee_Beetles() {
        return Flee_Beetles;
    }

    public void setFlee_Beetles(String flee_Beetles) {
        Flee_Beetles = flee_Beetles;
    }



    public String getTuta() {
        return Tuta;
    }

    public void setTuta(String tuta) {
        Tuta = tuta;
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




    public String getTrips() {
        return Trips;
    }

    public void setTrips(String trips) {
        Trips = trips;
    }




    public String getPathnr() {
        return pathnr;
    }

    public void setPathnr(String pathnr) {
        this.pathnr = pathnr;
    }







    public String getFieldnr() {
        return fieldnr;
    }

    public void setFieldnr(String fieldnr) {
        this.fieldnr = fieldnr;
    }




    public String getDatumtijd() {
        return datumtijd;
    }

    public void setDatumtijd(String datumtijd) {
        this.datumtijd = datumtijd;
    }



    public Member() {
    }
}
