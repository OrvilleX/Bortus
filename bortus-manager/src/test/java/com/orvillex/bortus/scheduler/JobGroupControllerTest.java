package com.orvillex.bortus.scheduler;

import java.util.HashSet;

import com.orvillex.bortus.base.AbstractSpringMvcTest;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.rest.JobGroupController;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



public class JobGroupControllerTest extends AbstractSpringMvcTest {

    @Autowired
    private JobGroupController jobGroupController;
    
    @Test
    public void testPageList() throws Exception {
        ResponseEntity<BasePage<JobGroup>> result = jobGroupController.pageList(new JobGroupCriteria(), PageRequest.of(0, 10));
        Assert.assertNotNull(result.getBody().getContent());
    }

    @Test
    public void testSave() throws Exception {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName("bortus-job-test");
        jobGroup.setTitle("bortus-job-title");
        jobGroup.setAddressType(1);
        jobGroup.setAddressList("192.168.1.1,192.168.1.2");
        ResponseEntity<Object> result = jobGroupController.create(jobGroup);
        Assert.assertEquals(result.getStatusCode(), HttpStatus.CREATED);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateAppNameError() {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName("low");
        jobGroupController.create(jobGroup);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateAddressListError() {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName("test");
        jobGroup.setAddressType(1);
        jobGroup.setAddressList("192, , ");
        jobGroupController.create(jobGroup);
    }

    @Test
    public void testUpdate() {
        JobGroup jobGroup = new JobGroup();
        jobGroup.setId(1l);
        jobGroup.setAppName("bortus-job-test");
        jobGroup.setTitle("bortus-job-title");
        jobGroup.setAddressType(1);
        jobGroup.setAddressList("192.168.1.5,165.15.12.1");
        jobGroupController.update(jobGroup);
    }

    @Test
    public void testRemove() {
        jobGroupController.remove(new HashSet<Long>() {{
            add(2l);
        }});
    }

    @Test
    public void testloadById() {
        ResponseEntity<JobGroup> result = jobGroupController.loadById(1l);
        JobGroup jobGroup = result.getBody();
        Assert.assertNotNull(jobGroup);
        Assert.assertEquals(jobGroup.getId(), Long.valueOf(1));
        Assert.assertEquals(jobGroup.getAppName(), "job-executor-sample1");
        Assert.assertEquals(jobGroup.getTitle(), "示例执行器1");
    }
}
