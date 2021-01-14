package com.orvillex.bortus.scheduler;

import com.orvillex.bortus.base.AbstractSpringMvcTest;
import com.orvillex.bortus.manager.exception.BadRequestException;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.rest.JobCodeController;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobLogGlueDto;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class JobCodeControllerTest extends AbstractSpringMvcTest {
    
    @Autowired
    private JobCodeController jobCodeController;

    @Test
    public void testDetail() {
        ResponseEntity<JobLogGlueDto> result = jobCodeController.detail(2l);
        JobLogGlueDto jobLogGlue = result.getBody();
        Assert.assertNotNull(jobLogGlue);
        JobInfo jobinfo = jobLogGlue.getJobInfo();
        Assert.assertNotNull(jobinfo);
        Assert.assertEquals(jobinfo.getId(), Long.valueOf(2));
        Assert.assertEquals(jobinfo.getJobDesc(), "测试任务2");
        Assert.assertEquals(jobLogGlue.getJobLogGlues().size(), 0);
    }

    @Test(expected = BadRequestException.class)
    public void testDetailNotExisted() {
        jobCodeController.detail(404l);
    }

    @Test
    public void testCreate() {
        jobCodeController.update(2l, "import", "i14564");
        ResponseEntity<JobLogGlueDto> result = jobCodeController.detail(2l);
        Assert.assertEquals(result.getBody().getJobLogGlues().size(), 1);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateNoGlueRemark() {
        jobCodeController.update(2l, "import", "1");
    }
}
