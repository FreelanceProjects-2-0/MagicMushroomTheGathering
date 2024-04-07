package dk.tec.magicmushroomthegathering;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;

public class OsmActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private MapView map = null;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private Location loc;
    private Boolean startup = false;

    FirebaseAuth mauth;
    FirebaseUser user;

    FloatingActionButton btn_goBack;
    FloatingActionButton btn_addLocation;

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

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
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
                            if (!startup) {
                                startup = true;
                                centerMapOnLocation(location);
                            }
                            loc = location;
                        }
                );
    }

    void centerMapOnLocation(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    private void getMarkers(){
        db.collection("userData")
                .document(user.getUid())
                .collection("markers")
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       for (QueryDocumentSnapshot document : task.getResult()){
                           com.google.firebase.firestore.GeoPoint firestoreGeoPoint = document.getGeoPoint("coordinates");

                           if (firestoreGeoPoint != null){
                               setMarker(new GeoPoint(firestoreGeoPoint.getLatitude(), firestoreGeoPoint.getLongitude()));
                           }
                       }
                   }
                   else {
                       Log.d("FirestoreException", "Something went wrong while retrieving markers from db", task.getException());
                       // DO SOMETHING FUCKING COOL HERE :o
                   }
                });
    }

    private void saveMarkerToDb(GeoPoint geoPoint){
        Map<String, com.google.firebase.firestore.GeoPoint> coordinates = new HashMap<>();


        coordinates.put("coordinates", new com.google.firebase.firestore.GeoPoint(loc.getLatitude(), loc.getLongitude()));

        db.collection("userData").document(user.getUid()).collection("markers").add(coordinates);
        setMarker(geoPoint);
    }

    private void setMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);

        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_location_pin_48, null);
        setTint(icon, R.color.purple_700);

        marker.setIcon(icon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

//        Map<String, com.google.firebase.firestore.GeoPoint> coordinates = new HashMap<>();
//
//
//        coordinates.put("coordinates", new com.google.firebase.firestore.GeoPoint(loc.getLatitude(), loc.getLongitude()));
//
//        db.collection("userData").document(user.getUid()).collection("markers").add(coordinates);

        map.getOverlays().add(marker);
        map.invalidate();
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    private void init(){
        mauth = FirebaseAuth.getInstance();
        user = mauth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        getLayoutFields();
        initOnClickListeners();
        getMarkers();
    }

    //    btn_goBack
    private void getLayoutFields() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btn_goBack = findViewById(R.id.btn_goBack);
        btn_addLocation = findViewById(R.id.btn_addLocation);
    }

    private void initOnClickListeners() {
        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoggedInUser.class);
                startActivity(intent);
                finish();
            }
        });
        btn_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                saveMarkerToDb(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            }
        });
    }
}
