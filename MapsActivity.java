package io.maincraft.mapaeffects;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
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

        //=======================

        //48.702976                     //48.704548
        //44.516639                     //44.519450

        //48.702176                     //48.703960
        //44.517691                     //44.520673

        //=======================

        //Camera
        //48.703443
        //44.519075

        //=======================

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

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                renderGif();
            }
        }, 1000);
        */
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
    private void renderGif() {

        image = (ImageView) findViewById(R.id.loader);
        Glide.with(this).load(R.raw.ripple_third).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        timer(px);
    }


    private float trueX;
    private float trueY;

    private void  timer(final float px) {



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Projection projection = mMap.getProjection();
                LatLng markerLocation = marker.getPosition();
                Point screenPosition = projection.toScreenLocation(markerLocation);

                float new_trueX = screenPosition.x - px + ((px/100)*50);
                float new_trueY = screenPosition.y - px + ((px/100)*40);

                if(new_trueX != trueX || new_trueY != trueY) {
                    image.animate().translationX(new_trueX).translationY(new_trueY).setDuration(500);
                }

                timer(px);
            }
        }, 500);
    }


    private void renderCircles() {

        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(100,100);
        d.setColor(Color.argb(100, 247,57,64));
        d.setStroke(1, Color.argb(100,247,57,64));

        Bitmap mbitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        Canvas canvas = new Canvas(mbitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);


        GradientDrawable s = new GradientDrawable();
        s.setShape(GradientDrawable.OVAL);
        s.setSize(100,100);
        s.setColor(Color.argb(50, 244,57,64));
        s.setStroke(1, Color.argb(100,247,57,64));
        Bitmap mbitmapSecond = Bitmap.createBitmap(s.getIntrinsicWidth()
                , s.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        Canvas canvasSecond = new Canvas(mbitmapSecond);
        s.setBounds(0, 0, canvas.getWidth(), canvasSecond.getHeight());
        s.draw(canvasSecond);

        final int radius = 100;
        final int duration = 3000;

        final GroundOverlay circle = mMap.addGroundOverlay(new GroundOverlayOptions()
                .position(GEO_Position, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(mbitmap)));

        final GroundOverlay circleSecond = mMap.addGroundOverlay(
                new GroundOverlayOptions()
                        .position(GEO_Position, 2 * (radius / 1.5f)).image(BitmapDescriptorFactory.fromBitmap(mbitmapSecond)));

        final ValueAnimator valueAnimator = new ValueAnimator();
        ValueAnimator.setFrameDelay(60);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(0, radius);
        valueAnimator.setDuration(duration);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());



        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setDimensions(animatedFraction * radius * 2);
                circle.setTransparency(animatedFraction);
            }
        });



        final ValueAnimator valueAnimatorSecond = new ValueAnimator();
        valueAnimatorSecond.setFrameDelay(60);
        valueAnimatorSecond.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimatorSecond.setRepeatMode(ValueAnimator.RESTART);
        valueAnimatorSecond.setIntValues(0, radius);
        valueAnimatorSecond.setDuration(duration);
        valueAnimatorSecond.setEvaluator(new FloatEvaluator());
        valueAnimatorSecond.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorSecond.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circleSecond.setDimensions(animatedFraction * (radius / 1.5f) * 2);
                circleSecond.setTransparency(animatedFraction);

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        valueAnimatorSecond.start();
                    }
                }, 800);

            }
        }, 1000);
    }


    @Override
    public void onCameraMove() {
        Log.v("====", "Camera move");
    }
}
