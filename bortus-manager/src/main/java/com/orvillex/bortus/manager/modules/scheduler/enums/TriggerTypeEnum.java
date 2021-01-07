package com.orvillex.bortus.manager.modules.scheduler.enums;

import com.orvillex.bortus.manager.utils.I18nUtil;

/**
 * 任务触发类型
 * @author y-z-f
 * @version 0.1
 */
public enum TriggerTypeEnum {

    MANUAL(I18nUtil.getString("jobconf_trigger_type_manual")),
    CRON(I18nUtil.getString("jobconf_trigger_type_cron")),
    RETRY(I18nUtil.getString("jobconf_trigger_type_retry")),
    PARENT(I18nUtil.getString("jobconf_trigger_type_parent")),
    API(I18nUtil.getString("jobconf_trigger_type_api"));

    private TriggerTypeEnum(String title){
        this.title = title;
    }
    
    private String title;

    public String getTitle() {
        return title;
    }
}
