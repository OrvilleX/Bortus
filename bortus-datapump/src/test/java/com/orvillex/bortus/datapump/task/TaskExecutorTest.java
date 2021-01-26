package com.orvillex.bortus.datapump.task;

import com.orvillex.bortus.datapump.base.AbstractSpringMvcTest;
import com.orvillex.bortus.datapump.core.task.TaskContainer;
import com.orvillex.bortus.datapump.core.transport.channel.Channel;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TaskExecutorTest extends AbstractSpringMvcTest {
    @Autowired
    private Channel channel;

    @Test
    public void taskExecutorBaseTest() {
    }
}
