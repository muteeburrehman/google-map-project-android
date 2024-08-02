package io.xconn.excelfilereader;

public class LocationData {
    private String id;
    private double latitude;
    private double longitude;

    public LocationData(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
