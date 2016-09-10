package com.example.lucas.esapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lucas on 27/07/2016.
 */
public class MarkedPlace implements Parcelable {
    private int id;
    private String name;
    private String descricao;
    private String photo;
    private double lat;
    private double log;
    private double distance;
    private double evaluation = 0.0;
    private String category;
    private String status;

    public MarkedPlace(int id, String type, String name, String info1, String info2, double lat, double log, double distance, double evaluation, String status){
        this.id = id;
        this.name = name;
        this.category = type;
        this.descricao = info1;
        this.photo = info2;
        this.lat = lat;
        this.log = log;
        this.distance = distance;
        this.evaluation = evaluation;
        this.status = status;
    }

    public MarkedPlace(double latitude, double longitude){
        this.name = "Name";
        this.category = "Category";
        this.descricao = "Descricao";
        this.photo = "Photo";
        this.evaluation = 0;
        this.lat = latitude;
        this.log = longitude;
    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getLat() {
        return lat;
    }

    public double getLog() {
        return log;
    }

    public int getId() {
        return id;
    }

    protected MarkedPlace(Parcel in){
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        descricao = in.readString();
        photo = in.readString();
        lat = in.readDouble();
        log = in.readDouble();
        distance = in.readDouble();
        evaluation = in.readDouble();
    }

    public static final Creator<MarkedPlace> CREATOR = new Creator<MarkedPlace>() {
        @Override
        public MarkedPlace createFromParcel(Parcel in) {
            return new MarkedPlace(in);
        }

        @Override
        public MarkedPlace[] newArray(int size) {
            return new MarkedPlace[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(category);
        parcel.writeString(descricao);
        parcel.writeString(photo);
        parcel.writeDouble(lat);
        parcel.writeDouble(log);
        parcel.writeDouble(distance);
        parcel.writeDouble(evaluation);
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
