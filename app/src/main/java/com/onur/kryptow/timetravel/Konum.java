package com.onur.kryptow.timetravel;

public class Konum {
    public Double baslangicLatitude;
    public Double baslangicLongitude;
    public Double bitisLatitude;
    public Double bitisLongitude;
    public String s_time;
    public String locationName;


    public Konum(){

    }

    public Konum (Double baslangicLatitude,Double baslangicLongitude,Double bitisLatitude,Double bitisLongitude,String s_time,String locationName){
        this.baslangicLatitude=baslangicLatitude;
        this.baslangicLongitude=baslangicLongitude;
        this.bitisLatitude=bitisLatitude;
        this.bitisLongitude=bitisLongitude;
        this.s_time=s_time;
        this.locationName=locationName;

    }
}
