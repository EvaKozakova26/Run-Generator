package cz.uhk.fim.runhk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kozak on 23.06.2018.
 */

public class LocationModel implements Parcelable {

    public double latitude;
    public double longitude;

    //musi tu byt kvuli firebase
    public LocationModel() {
    }

    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private LocationModel(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public static final Parcelable.Creator<LocationModel> CREATOR = new Parcelable.Creator<LocationModel>() {
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        public LocationModel[] newArray(int size) {
            return new LocationModel[size];

        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
