package com.orvillex.bortus.scheduler;

import com.orvillex.bortus.base.AbstractSpringMvcTest;
import com.orvillex.bortus.manager.entity.BasePage;
import com.orvillex.bortus.manager.modules.scheduler.domain.JobInfo;
import com.orvillex.bortus.manager.modules.scheduler.rest.JobInfoController;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoCriteria;
import com.orvillex.bortus.manager.modules.scheduler.service.dto.JobInfoDto;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

public class JobInfoControllerTest extends AbstractSpringMvcTest {
    
    @Autowired
    private JobInfoController jobInfoController;

    @Test
    public void testIndex() {
        ResponseEntity<JobInfoDto> result = jobInfoController.index();
        JobInfoDto jobInfoDto = result.getBody();
        Assert.assertNotNull(jobInfoDto);
        Assert.assertEquals(jobInfoDto.getJobGroupList().size(), 2);
    }

    @Test
    public void testPageList() {
        ResponseEntity<BasePage<JobInfo>> result = jobInfoController.pageList(new JobInfoCriteria(), PageRequest.of(0, 10));
        BasePage<JobInfo> jobinfos = result.getBody();
        Assert.assertEquals(jobinfos.getTotalElements(), 2);
        Assert.assertEquals(jobinfos.getContent().size(), 2);
    }
}
