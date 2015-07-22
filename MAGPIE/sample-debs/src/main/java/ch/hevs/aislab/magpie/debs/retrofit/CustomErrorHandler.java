package ch.hevs.aislab.magpie.debs.retrofit;


import android.content.Context;
import android.util.Log;

import java.net.ConnectException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomErrorHandler implements ErrorHandler {

    final String TAG = getClass().getName();

    private final Context context;

    public CustomErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public Throwable handleError(RetrofitError error) {
        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
            if (error.getCause() instanceof ConnectException) {
                return error.getCause();
            }
        } else {
            Response r = error.getResponse();
            if (r != null) {
                Log.e(TAG, "Error code: " + r.getStatus());
            }
        }
        return error;
    }
}
