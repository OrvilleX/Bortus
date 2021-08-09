package com.orvillex.bortus.manager.modules.log.rest;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.orvillex.bortus.manager.annotation.Log;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 连接池统计信息
 */
@RestController
@Api(tags = "Druid信息")
public class DruidStatController {

    @Log("Druid信息")
    @GetMapping("/druid/stat")
    @ApiOperation(value = "Druid信息")
    public Object druidStat(){
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
