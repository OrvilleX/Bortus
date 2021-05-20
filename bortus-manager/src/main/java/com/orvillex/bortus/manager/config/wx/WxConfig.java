package com.orvillex.bortus.manager.config.wx;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;

@Configuration
public class WxConfig {
    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private WxPayProperties wxPayProperties;

    @Bean
    public WxMaConfig wxMaConfig() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxProperties.getAppId());
        config.setSecret(wxProperties.getAppSecret());
        return config;
    }

    @Bean
    public WxMaService wxMaService(WxMaConfig wxMaConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(wxMaConfig);
        return service;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(this.wxPayProperties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(this.wxPayProperties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(this.wxPayProperties.getMchKey()));
        payConfig.setSubAppId(StringUtils.trimToNull(this.wxPayProperties.getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(this.wxPayProperties.getSubMchId()));
        payConfig.setTradeType("JSAPI");
        //payConfig.setKeyPath(StringUtils.trimToNull(this.wxPayProperties.getKeyPath()));
    
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
    
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
