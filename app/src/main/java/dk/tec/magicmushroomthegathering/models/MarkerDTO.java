package dk.tec.magicmushroomthegathering.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

public class MarkerDTO {
    private String id;
    private GeoPoint location;
    private String userId;

    public MarkerDTO(GeoPoint location, String userId){
        this.location = Objects.requireNonNull(location);
        this.userId = Objects.requireNonNull(userId);
    }

    public GeoPoint getLocation(){
        return this.location;
    }
    public String getUserId(){
        return this.userId;
    }
}
