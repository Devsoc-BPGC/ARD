package in.ac.bits_pilani.goa.ard;

import android.app.Application;
import android.content.Context;

/**
 * Custom class extending the Application class.
 * @author vikramaditya
 */

public class ARD extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
