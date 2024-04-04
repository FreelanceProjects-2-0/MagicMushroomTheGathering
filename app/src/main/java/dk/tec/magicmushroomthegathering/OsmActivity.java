package dk.tec.magicmushroomthegathering;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OsmActivity extends AppCompatActivity {

    private MapView map = null;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private Location loc;

    private Boolean startup = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_osm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Configuration.getInstance().setUserAgentValue(BuildConfig.LIBRARY_PACKAGE_NAME); // This has to be placed before the contentView is set to load the map.

        setContentView(R.layout.activity_osm);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true); // This could be broken so if the map stops loading comment this line
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(18.0);

            findViewById(R.id.btn_addLocation).setOnClickListener(view -> {
            getLocation();
            setMarker(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                            centerMapOnLocation(location);
                            loc = location;
                        }
                );
    }

    void centerMapOnLocation(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    private void setMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);

        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_location_pin_48, null);
        setTint(icon, R.color.purple_700);

        marker.setIcon(icon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
    }
    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}