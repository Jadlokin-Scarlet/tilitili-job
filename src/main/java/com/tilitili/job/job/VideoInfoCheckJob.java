package com.tilitili.job.job;

import com.tilitili.common.emnus.TaskReason;
import com.tilitili.common.emnus.TaskType;
import com.tilitili.common.entity.BatchTask;
import com.tilitili.common.manager.TaskManager;
import com.tilitili.common.mapper.TouhouAllMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableAsync
public class VideoInfoCheckJob {

    private final TouhouAllMapper touhouAllMapper;
    private final TaskManager taskManager;

    @Autowired
    public VideoInfoCheckJob(TouhouAllMapper touhouAllMapper, TaskManager taskManager) {
        this.touhouAllMapper = touhouAllMapper;
        this.taskManager = taskManager;
    }

    @Async
    @Scheduled(cron ="0 0 0/2 * * ?")
    protected void videoInfoCheck() {
        log.info("【VideoInfoCheckJob】check video info start");
        BatchTask batchTask = new BatchTask().setType(TaskType.AutoBatchSpiderVideo.getValue()).setReason(TaskReason.SUPPLEMENT_VIDEO_INFO.getValue());
        List<String> avList = touhouAllMapper.checkVideoInfo().stream().map(String::valueOf).collect(Collectors.toList());
        if (avList.isEmpty()) {
            log.warn("【VideoInfoCheckJob】check video info empty");
            return;
        }
        taskManager.batchSpiderVideo(batchTask, avList);
        log.info("【VideoInfoCheckJob】check video info end");
    }

}
