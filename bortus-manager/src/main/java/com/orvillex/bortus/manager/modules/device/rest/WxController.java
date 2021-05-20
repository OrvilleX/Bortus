package com.orvillex.bortus.manager.modules.device.rest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.orvillex.bortus.manager.annotation.AnonymousPostMapping;
import com.orvillex.bortus.manager.annotation.Log;
import com.orvillex.bortus.manager.config.security.LoginCode;
import com.orvillex.bortus.manager.config.security.TokenProvider;
import com.orvillex.bortus.manager.config.wx.WxPayProperties;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.security.service.OnlineUserService;
import com.orvillex.bortus.manager.modules.security.service.dto.JwtUserDto;
import com.orvillex.bortus.manager.modules.system.service.dto.DeptSmallDto;
import com.orvillex.bortus.manager.modules.system.service.dto.UserDto;
import com.orvillex.bortus.manager.utils.SecurityUtils;
import com.orvillex.bortus.manager.utils.WxPayUtil;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 供小程序调用
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("wx")
public class WxController {
    private final OnlineUserService onlineUserService;
    private final WxMaService wxMaService;
    private final WxPayService wxPayService;
    private final TokenProvider tokenProvider;

    private final WxPayProperties wxPayProperties;

    // @Log("微信登录")
    // @AnonymousPostMapping(value = "loginByCode")
    // public ResponseEntity<LoginCodeReply> loginByCode(@RequestBody LoginByCodeVo loginCode, HttpServletRequest request) {
        
    //     String openId = null;
    //     String sessionKey = null;
    //     String unionId = null;
    //     try {
    //         WxMaJscode2SessionResult result = wxMaService.getUserService().getSessionInfo(loginCode.getCode());
    //         sessionKey = result.getSessionKey();
    //         openId = result.getOpenid();
    //         unionId = result.getUnionid();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }

    //     if (sessionKey == null || openId == null) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }

    //     LoginCodeReply reply = new LoginCodeReply();
    //     TenantInfo tenantInfo = tenantInfoService.findByWxOpenId(openId);
    //     if (tenantInfo == null) {
    //         tenantInfo = new TenantInfo();
    //         tenantInfo.setWxOpenId(openId);
    //         tenantInfo.setCity(loginCode.getCity());
    //         tenantInfo.setEncryptedData(loginCode.getEncryptedData());
    //         tenantInfo.setGender(loginCode.getGender());
    //         tenantInfo.setHeadUrl(loginCode.getAvatarUrl());
    //         tenantInfo.setIv(loginCode.getIv());
    //         tenantInfo.setNickName(loginCode.getNickName());
    //         tenantInfo.setProvince(loginCode.getProvince());
    //         tenantInfo.setSource(1l);
    //         tenantInfo.setSessionKey(sessionKey);
    //         tenantInfoService.create(tenantInfo);
    //         reply.setNew(true);
    //     } else {
    //         tenantInfo.setSessionKey(sessionKey);
    //         tenantInfoService.update(tenantInfo);
    //         reply.setNew(false);
    //     }

    //     String token = tokenProvider.createToken("tenant", "#wx" + sessionKey);
    //     DeptSmallDto dept = new DeptSmallDto();
    //     dept.setName("微信");
    //     UserDto userDto = new UserDto();
    //     userDto.setId(tenantInfo.getId());
    //     userDto.setUsername(tenantInfo.getSessionKey());
    //     userDto.setAvatarPath(tenantInfo.getHeadUrl());
    //     userDto.setEmail(tenantInfo.getEmail());
    //     userDto.setNickName(tenantInfo.getNickName());
    //     userDto.setDept(dept);

    //     List<Long> dataScopes = new ArrayList<>();
    //     List<GrantedAuthority> auth = new ArrayList<>();
    //     JwtUserDto jwtUserDto = new JwtUserDto(
    //         userDto,
    //         dataScopes,
    //         auth
    //     );
    //     onlineUserService.save(jwtUserDto, token, request);

    //     reply.setToken(token);
    //     return new ResponseEntity<>(reply, HttpStatus.OK);
    // }

    // @Log("发起预充值单")
    // @PostMapping(value = "pay/unifiedorder")
    // public ResponseEntity<UnifiedOrderReplyVo> unifiedOrder(@RequestBody UnifiedOrderInputVo unifiedOrder) {
    //     WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
    //         .subOpenid(SecurityUtils.getCurrentUser().getUsername())
    //         .outTradeNo(rechargeDetail.getId().toString())
    //         .totalFee(WxPayUtil.yuanToFee(unifiedOrder.getAmount()))
    //         .body("费用充值")
    //         .spbillCreateIp(InetAddress.getLoopbackAddress().getHostAddress())
    //         .notifyUrl("http://www.jmcxai.com/wx/pay/order").build();
        
    //     WxPayUnifiedOrderResult wxPayUnifiedOrderResult = null;
    //     try {
    //         request.checkAndSign(wxPayService.getConfig());
    //          wxPayUnifiedOrderResult = wxPayService.unifiedOrder(request);
    //     } catch (Exception ex) {
    //         throw new BadRequestException("微信发起预支付单失败");
    //     }
        
    //     Map<String, String> paySignInfo = new HashMap<>(5);
    //     String timeStamp =  WxPayUtil.createTimestamp();
    //     String nonceStr = String.valueOf(System.currentTimeMillis());
    //     paySignInfo.put("appId", wxPayProperties.getSubAppId());
    //     paySignInfo.put("nonceStr", nonceStr);
    //     paySignInfo.put("timeStamp", timeStamp);
    //     paySignInfo.put("signType", "MD5");
    //     paySignInfo.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
    //     String paySign = SignUtils.createSign(paySignInfo, "MD5", wxPayProperties.getMchKey(), null);

    //     UnifiedOrderReplyVo returnPayInfoVO = new UnifiedOrderReplyVo();
    //     returnPayInfoVO.setAppId(wxPayProperties.getSubAppId());
    //     returnPayInfoVO.setTimeStamp(timeStamp);
    //     returnPayInfoVO.setNonceStr(nonceStr);
    //     returnPayInfoVO.setPaySign(paySign);
    //     returnPayInfoVO.setSignType("MD5");
    //     returnPayInfoVO.setOrderId(rechargeDetail.getId());
    //     returnPayInfoVO.setPrepayId(wxPayUnifiedOrderResult.getPrepayId());

    //     return new ResponseEntity<>(returnPayInfoVO, HttpStatus.OK);
    // }

    // @Log("支付回调通知处理")
    // @AnonymousPostMapping(value = "notify/order")
    // public String parseOrderNotifyResult(@RequestBody String xmlData) throws WxPayException {
    //     try {
    //         final WxPayOrderNotifyResult notifyResult = this.wxPayService.parseOrderNotifyResult(xmlData);
    //         Long rechargeId = Long.parseLong(notifyResult.getOutTradeNo());
    //         if (notifyResult.getReturnCode() == "SUCCESS") {
    //             if (notifyResult.getResultCode() == "SUCCESS") {
    //                 this.tenantRechargeDetailService.changeStatus(rechargeId, RechargeArrivalStatusEnum.RECEIVED, "完成");
    //             }
    //             else {
    //                 this.tenantRechargeDetailService.changeStatus(rechargeId, RechargeArrivalStatusEnum.NORECEIVED, notifyResult.getErrCodeDes());
    //             }
    //             this.tenantRechargeDetailService.changeOrderNo(rechargeId, notifyResult.getTransactionId());
    //         } else {
    //             log.warn("支付回调失败 对应错误消息为[{}]", notifyResult.getReturnMsg());
    //         }
    //         return WxPayNotifyResponse.success("成功");
    //     } catch (Exception ex) {
    //         log.error("支付回调异常", ex);
    //         return WxPayNotifyResponse.fail("回调失败");
    //     }
    // }
}
