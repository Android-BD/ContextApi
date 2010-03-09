/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import uk.co.md87.android.sensorlogger.ExceptionHandler;

import uk.co.md87.android.sensorlogger.R;

/**
 *
 * @author chris
 */
public class IntroActivity extends BoundActivity implements OnClickListener {

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        Thread.setDefaultUncaughtExceptionHandler(
                new ExceptionHandler("http://chris.smith.name/android/upload",
                getVersionName(), getIMEI()));

        setContentView(R.layout.intro);

        ((Button) findViewById(R.id.introstart)).setOnClickListener(this);
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException ex) {
            return "Unknown";
        }
    }

    public String getIMEI() {
        return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(final View arg0) {
        try {
            service.setState(1);
            startActivity(new Intent(this, CountdownActivity.class));
            finish();
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error setting state", ex);
        }
    }

}
