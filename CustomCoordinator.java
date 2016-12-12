package io.maincraft.mapaeffects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedHashMap;


public class CustomCoordinator extends FrameLayout {

    public static GoogleMap mMap;
    public static LinkedHashMap<Marker, Boolean> map = new LinkedHashMap<Marker, Boolean>();

    public CustomCoordinator(Context context) {
        super(context);
    }

    public CustomCoordinator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCoordinator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCoordinator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        Projection projection = mMap.getProjection();
        for(LinkedHashMap.Entry<Marker, Boolean> entry : map.entrySet()) {

            Marker marker = entry.getKey();

            LatLng markerLocation = marker.getPosition();
            Point screenPosition = projection.toScreenLocation(markerLocation);

            int cursorX = Math.round(event.getX());
            int cursorY = Math.round(event.getY());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    cursorX = Math.round(event.getX());
                    cursorY = Math.round(event.getY());

                    int MarkerX = screenPosition.x;
                    int MarkerY = screenPosition.y;

                    if(Math.abs(MarkerX-cursorX) <= 150 && Math.abs(MarkerY-cursorY) <=150 ) {
                        entry.setValue(true);
                        mMap.getUiSettings().setScrollGesturesEnabled(false);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    entry.setValue(false);
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    cursorX = Math.round(event.getX());
                    cursorY = Math.round(event.getY());
                    Point touchPoint = new Point();

                    touchPoint.set(cursorX, cursorY);
                    LatLng latLng = projection.fromScreenLocation(touchPoint);


                    if(entry.getValue()) {
                        entry.getKey().setPosition(latLng);
                        redrawLines();
                    }

                    break;
            }

        }

        return super.dispatchTouchEvent(event);
    }

    public static Polyline line;
    public static void redrawLines() {

        if(line != null) {
            line.remove();
        }
        PolylineOptions lineOptions = new PolylineOptions();

        for(LinkedHashMap.Entry<Marker, Boolean> entry : map.entrySet()) {

            lineOptions.add(entry.getKey().getPosition()).width(15).color(Color.GREEN);
        }

        lineOptions.add(map.entrySet().iterator().next().getKey().getPosition()).width(15).color(Color.GREEN);
        line =mMap.addPolyline(lineOptions);

    }


}
