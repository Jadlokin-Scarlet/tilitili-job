package com.tilitili.job.receive;

import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class SpiderVideoTagDLQTaskReceive {

    private final TaskMapper taskMapper;

    @Autowired
    public SpiderVideoTagDLQTaskReceive(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @JmsListener(destination = "DLQ.SpiderVideoTagTaskMessage")
    public void receiveSpiderVideoTagDLQTask(TaskMessage taskMessage) {
        taskMapper.updateStatusById(taskMessage.getId(), TaskStatus.WAIT.getValue(), TaskStatus.TIMEOUT.getValue());
    }

}
