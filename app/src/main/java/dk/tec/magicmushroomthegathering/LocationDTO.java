package dk.tec.magicmushroomthegathering;

import android.location.Location;

import androidx.annotation.NonNull;

public class LocationDTO extends Location {
    private String description;

    public LocationDTO(@NonNull Location location) {
        super(location);
    }

    public LocationDTO(@NonNull Location location, String description) {
        super(location);
        setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
