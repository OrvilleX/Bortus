package com.orvillex.bortus.manager.alarm.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.manager.alarm.JobAlarm;
import com.orvillex.bortus.manager.config.scheduler.SchedulerProperties;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobLog;
import com.orvillex.bortus.manager.modules.scheduler.service.JobGroupService;
import com.orvillex.bortus.manager.utils.I18nUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于邮件实现的告警
 * @author y-z-f
 * @version 0.1
 */
@Component
@Slf4j
public class EmailJobAlarm implements JobAlarm {

    @Autowired
    private JobGroupService jobGroupService;
    
    @Resource
    private SchedulerProperties schedulerProperties;

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailUserName;

    public boolean doAlarm(JobInfo info, JobLog jobLog){
        boolean alarmResult = true;

        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {

            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>TriggerMsg=<br>" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode()>0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>HandleCode=" + jobLog.getHandleMsg();
            }

            JobGroup group = jobGroupService.findById(info.getJobGroup());
            String personal = I18nUtil.getString("admin_name_full");
            String title = I18nUtil.getString("jobconf_monitor");
            String content = MessageFormat.format(loadEmailJobAlarmTemplate(),
                    group!=null?group.getTitle():"null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);

            Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
            for (String email: emailSet) {
                try {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();

                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setFrom(emailUserName, personal);
                    helper.setTo(email);
                    helper.setSubject(title);
                    helper.setText(content, true);

                    mailSender.send(mimeMessage);
                } catch (Exception e) {
                    log.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }
            }
        }
        return alarmResult;
    }

    private static final String loadEmailJobAlarmTemplate(){
        String mailBodyTemplate = "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td width=\"20%\" >"+ I18nUtil.getString("jobinfo_field_jobgroup") +"</td>\n" +
                "         <td width=\"10%\" >"+ I18nUtil.getString("jobinfo_field_id") +"</td>\n" +
                "         <td width=\"20%\" >"+ I18nUtil.getString("jobinfo_field_jobdesc") +"</td>\n" +
                "         <td width=\"10%\" >"+ I18nUtil.getString("jobconf_monitor_alarm_title") +"</td>\n" +
                "         <td width=\"40%\" >"+ I18nUtil.getString("jobconf_monitor_alarm_content") +"</td>\n" +
                "      </tr>\n" +
                "   </thead>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{0}</td>\n" +
                "         <td>{1}</td>\n" +
                "         <td>{2}</td>\n" +
                "         <td>"+ I18nUtil.getString("jobconf_monitor_alarm_type") +"</td>\n" +
                "         <td>{3}</td>\n" +
                "      </tr>\n" +
                "   </tbody>\n" +
                "</table>";

        return mailBodyTemplate;
    }
}
