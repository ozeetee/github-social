package io.zeetee.githubsocial;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.concurrent.TimeUnit;

import io.zeetee.githubsocial.network.RequestInterceptor;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * By GT.
 */

public class GSApp extends Application {

    private static final String TAG = GSApp.class.getSimpleName();

    private static GSApp currentInstance;

    private OkHttpClient okClient;

    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;
        Fresco.initialize(this);
        UserManager.getSharedInstance().initialize(); // Initialize User Manager
    }

    public static GSApp getCurrentInstance(){
        return currentInstance;
    }

    public OkHttpClient getCustomOKClient(){
        if(okClient == null){

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(new RequestInterceptor())
                    .addInterceptor(logging)
                    .connectTimeout(GSConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(GSConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        return okClient;
    }
}
