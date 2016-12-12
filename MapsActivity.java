package io.maincraft.mapaeffects;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener {


    private GoogleMap mMap = null;
    private AnimationSet breadingAnimations;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(mMap == null) {
            setContentView(R.layout.activity_maps);
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }



        breadingAnimations = new AnimationSet(false);
    }


    private LatLng GEO_Position;
    private List<LatLng> GEO_Position_List = new ArrayList<LatLng>();
    private LinkedHashMap<Marker, Boolean> Marker_List = new LinkedHashMap<Marker, Boolean>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        GEO_Position = new LatLng(48.708435, 44.510749);
        GEO_Position_List.add(new LatLng(48.708463, 44.509150));
        GEO_Position_List.add(new LatLng(48.709596, 44.510770));
        GEO_Position_List.add(new LatLng(48.708505, 44.512230));
        GEO_Position_List.add(new LatLng(48.707514, 44.510577));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(GEO_Position));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(GEO_Position, 16);
        mMap.animateCamera(cameraUpdate);

        setupMarkers();

        CustomCoordinator.map = Marker_List;
        CustomCoordinator.mMap = mMap;
        CustomCoordinator.redrawLines();
    }


    private Marker marker;
    private void setupMarkers() {
        addMarker();

        for(Iterator<LatLng> i = GEO_Position_List.iterator(); i.hasNext(); ) {
            LatLng item = i.next();
            Marker itemMarker = mMap.addMarker(
                    new MarkerOptions().position(item)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.point_ripple))
            );
            Marker_List.put(itemMarker , false);
        }
    }

    private void addMarker() {
        FloatingActionButton floatButton = (FloatingActionButton) findViewById(R.id.add_marker);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image = (ImageView) findViewById(R.id.loader);
                int imageX = Math.round(image.getX());
                int imageY = Math.round(image.getY());

                Projection projection = mMap.getProjection();
                Point imagePoint = new Point();
                imagePoint.set(imageX, imageY);
                LatLng latLng = projection.fromScreenLocation(imagePoint);

                Marker itemMarker = mMap.addMarker(
                        new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.point_ripple))
                );

                Marker_List.put(itemMarker , false);
                CustomCoordinator.redrawLines();
            }
        });
    }

    ImageView image;


    @Override
    public void onCameraMove() {

    }
}
