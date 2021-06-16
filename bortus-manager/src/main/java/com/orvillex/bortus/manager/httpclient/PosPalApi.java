package com.orvillex.bortus.manager.httpclient;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.orvillex.bortus.manager.httpclient.vo.BaseResVo;
import com.orvillex.bortus.manager.httpclient.vo.QueryAllUserReqVo;
import com.orvillex.bortus.manager.httpclient.vo.QueryAllUserResVo;
import com.orvillex.bortus.manager.httpclient.vo.QueryCustomerPagesResVo;
import com.orvillex.bortus.manager.httpclient.vo.QueryTicketPagesReqVo;
import com.orvillex.bortus.manager.httpclient.vo.QueryTicketPagesResVo;

import retrofit2.http.Body;
import retrofit2.http.POST;

@RetrofitClient(baseUrl = "${spring.retrofit.url}", connectTimeoutMs = 2000)
@Intercept(handler = PosPalInterceptor.class)
public interface PosPalApi {
    /**
     * 获取所有门店信息
     */
    @POST("userOpenApi/queryAllUser")
    BaseResVo<QueryAllUserResVo> queryAllUser(@Body QueryAllUserReqVo req);

    /**
     * 分页查询全部会员
     */
    @POST("customerOpenApi/queryCustomerPages")
    BaseResVo<QueryCustomerPagesResVo> queryCustomerPages(@Body QueryAllUserReqVo req);

    /**
     * 分页查询所有单据
     */
    @POST("ticketOpenApi/queryTicketPages")
    BaseResVo<QueryTicketPagesResVo> queryTicketPages(@Body QueryTicketPagesReqVo req);
}
