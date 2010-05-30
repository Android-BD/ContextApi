/*
 * Copyright (c) 2009-2010 Chris Smith
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package uk.co.md87.android.placesdisplay;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import java.util.List;

import uk.co.md87.android.common.ExceptionHandler;
import uk.co.md87.android.common.model.Place;

/**
 * Activity which displays all known places on a map.
 * 
 * @author chris
 */
public class PlacesDisplay extends MapActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        FlurryAgent.onStartSession(this, "XXXXGE95S8R54R6M7S6X");

        final String pkg = "uk.co.md87.android.contextanalyser";

        try {
            getPackageManager().getApplicationInfo(pkg, 0);
        } catch (NameNotFoundException ex) {
            FlurryAgent.onEvent("analyser_not_installed");
            Builder builder = new Builder(this);
            builder.setTitle("Context Analyser needed");
            builder.setMessage("This application requires the 'Context Analyser'"
                    + " application to be installed. Would you like "
                    + "to install it now?");
            builder.setPositiveButton("Install", new OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    FlurryAgent.onEvent("launch_market");
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://search?q=pname:" + pkg)));
                    finish();
                }
            });

            builder.setNegativeButton("Quit", new OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    FlurryAgent.onEvent("abort_install");
                    finish();
                }
            });

            builder.show();
            return;
        }
        
        setContentView(R.layout.main);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        PlacesItemisedOverlay overlay = new PlacesItemisedOverlay(getResources()
                .getDrawable(R.drawable.icon));
        List<Overlay> mapOverlays = mapView.getOverlays();

        final Cursor cursor = managedQuery(Place.CONTENT_URI,
                new String[] { Place.LATITUDE, Place.LONGITUDE },
                null, null, null);

        if (cursor.moveToFirst()) {
            final int latitudeColumn = cursor.getColumnIndex(Place.LATITUDE);
            final int longitudeColumn = cursor.getColumnIndex(Place.LONGITUDE);
            do {
                final double latitude = cursor.getDouble(latitudeColumn);
                final double longitude = cursor.getDouble(longitudeColumn);

                GeoPoint point = new GeoPoint((int) (latitude * 1000000),
                        (int) (longitude * 1000000));
                OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");

                overlay.addOverlay(overlayitem);
            } while (cursor.moveToNext());
        }
        mapOverlays.add(overlay);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FlurryAgent.onEndSession(this);
    }

}
