package com.tilitili.job.job;

import com.tilitili.common.emnus.TaskReason;
import com.tilitili.common.emnus.TaskType;
import com.tilitili.common.entity.BatchTask;
import com.tilitili.common.manager.TaskManager;
import com.tilitili.common.mapper.TouhouAllMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class VideoTagCheckJob {

    private final TouhouAllMapper touhouAllMapper;
    private final TaskManager taskManager;

    @Autowired
    public VideoTagCheckJob(TouhouAllMapper touhouAllMapper, TaskManager taskManager) {
        this.touhouAllMapper = touhouAllMapper;
        this.taskManager = taskManager;
    }

    @Async
    @Scheduled(cron ="0 0 1/2 * * ?")
    protected void executeInternal() {
        log.info("【VideoTagCheckJob】check video tag start");
        BatchTask batchTask = new BatchTask().setType(TaskType.AutoBatchSpiderVideo.getValue()).setReason(TaskReason.SUPPLEMENT_VIDEO_TAG.getValue());
        List<String> avList = touhouAllMapper.checkVideoTag().stream().map(String::valueOf).collect(Collectors.toList());
        if (avList.isEmpty()) {
            log.warn("【VideoTagCheckJob】check video tag empty");
            return;
        }
        taskManager.batchSpiderVideo(batchTask, avList);
        log.info("【VideoTagCheckJob】check video tag end");
    }

}
