package io.zeetee.githubsocial.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * By GT.
 */

public class RequestInterceptor implements Interceptor{

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder().build();
        return chain.proceed(request);
    }
}
