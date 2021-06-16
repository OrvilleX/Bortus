package com.orvillex.bortus.manager.httpclient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import com.orvillex.bortus.manager.utils.PosPalUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Retrofit拦截器
 */
@Component
public class PosPalInterceptor extends BasePathMatchInterceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Value("${pospal.appkey}")
    private String appKey;

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        Long timestamp = System.currentTimeMillis();
        Request request = chain.request();
        RequestBody requestBody = request.body();
        String body = null;
        String dataSignature = null;

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.readString(charset);
        }

        try {
            dataSignature = PosPalUtil.encryptToMd5String(body, appKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Request newrequest = request.newBuilder()
            .header("time-stamp", timestamp.toString())
            .header("data-signature", dataSignature)
            .build();
            
        return chain.proceed(newrequest);
    }
}
