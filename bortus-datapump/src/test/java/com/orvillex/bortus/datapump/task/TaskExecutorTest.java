package com.orvillex.bortus.datapump.task;

import com.orvillex.bortus.datapump.base.AbstractSpringMvcTest;
import com.orvillex.bortus.datapump.core.task.TaskContainer;
import com.orvillex.bortus.datapump.core.transport.channel.Channel;
import com.orvillex.bortus.datapump.executor.drds.DrdsReader;
import com.orvillex.bortus.datapump.executor.drds.DrdsWriter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TaskExecutorTest extends AbstractSpringMvcTest {
    @Autowired
    private Channel channel;

    @Test
    public void taskExecutorBaseTest() {
        DrdsReader reader = new DrdsReader();
        DrdsWriter writer = new DrdsWriter();
        TaskContainer container = new TaskContainer(channel, reader, writer, Thread.currentThread().getName());
        container.start();
    }
}
