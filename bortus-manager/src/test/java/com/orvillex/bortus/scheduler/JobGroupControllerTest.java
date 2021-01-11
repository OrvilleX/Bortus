package com.orvillex.bortus.scheduler;

import com.orvillex.bortus.base.AbstractSpringMvcTest;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.rest.JobGroupController;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;



public class JobGroupControllerTest extends AbstractSpringMvcTest {

    @Autowired
    private JobGroupController jobGroupController;
    
    @Test
    public void testPageList() throws Exception {
        ResponseEntity<BasePage<JobGroup>> result = jobGroupController.pageList(new JobGroupCriteria(), PageRequest.of(0, 10));
        Assert.notEmpty(result.getBody().getContent(), "执行器空");
    }

    @Test
    public void testSave() throws Exception {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName("bortus-job-test");
        jobGroup.setTitle("bortus-job-title");
        jobGroup.setAddressType(1);
        jobGroup.setAddressList("192.168.1.1,192.168.1.2");
        ResponseEntity<Object> result = jobGroupController.create(jobGroup);
        Assert.isTrue(result.getStatusCode() == HttpStatus.CREATED, "执行器创建失败");
    }

    @Test(expected = BadRequestException.class)
    public void testSaveAppNameError() {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName("low");
        jobGroupController.create(jobGroup);
    }
}
