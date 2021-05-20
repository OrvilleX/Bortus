package com.orvillex.bortus.datapump.httpclient;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

@RetrofitClient(baseUrl = "${spring.retrofit.url}", connectTimeoutMs = 2000)
public interface DeviceApi {
    // /**
    //  * 获取设备账单
    //  */
    // @GET("own/device/{identification}")
    // DeviceLedgerVo getLedger(@Path("identification") String identification);

    // /**
    //  * 扣除金额
    //  */
    // @POST("own/device/deduct/amount")
    // Void deductAmount(@Body DeductAmountVo amount);

    // /**
    //  * 更新设备状态
    //  */
    // @PUT("own/device/state")
    // Void updateDeviceState(@Body DeviceStateVo state);
}
