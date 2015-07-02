package ch.hevs.aislab.magpie.android;

import android.app.Application;
import android.content.Context;

/**
 * Created by abrugues
 */
public class MagpieApp extends Application {

    private MagpieApp singleton;
    private Context context;

    public MagpieApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        context = getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

}
