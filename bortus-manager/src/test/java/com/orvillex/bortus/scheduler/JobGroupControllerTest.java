package com.orvillex.bortus.scheduler;

import com.orvillex.bortus.base.AbstractSpringMvcTest;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobGroup;
import com.orvillex.bortus.manager.modules.scheduler.rest.JobGroupController;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobGroupCriteria;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
}
