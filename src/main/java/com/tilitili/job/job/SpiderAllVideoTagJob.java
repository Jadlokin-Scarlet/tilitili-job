package com.tilitili.job.job;

import com.tilitili.common.emnus.RedisKeyEnum;
import com.tilitili.common.entity.BatchTask;
import com.tilitili.common.entity.query.BaseQuery;
import com.tilitili.common.manager.TaskManager;
import com.tilitili.common.mapper.TouhouAllMapper;
import com.tilitili.common.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.tilitili.common.emnus.TaskReason.RE_SPIDER_All_VIDEO_TAG;
import static com.tilitili.common.emnus.TaskType.AutoBatchSpiderVideo;

@Slf4j
@Component
public class SpiderAllVideoTagJob {
    private final TaskManager taskManager;
    private final TouhouAllMapper touhouALlMapper;
    private final RedisCache redisCache;

    @Autowired
    public SpiderAllVideoTagJob(TaskManager taskManager, TouhouAllMapper touhouALlMapper, RedisCache redisCache) {
        this.taskManager = taskManager;
        this.touhouALlMapper = touhouALlMapper;
        this.redisCache = redisCache;
    }

    @Async
    @Scheduled(cron = "0 30 1/2 * * ?")
    protected void executeInternal() {
        Integer start = (Integer) redisCache.get(RedisKeyEnum.SPIDER_ALL_VIDEO_TAG_REDIS_KEY.getKey());
        int count = touhouALlMapper.count(new BaseQuery<>());
        if (start == null || start > count) {
            start = 0;
        }
        BatchTask batchTask = new BatchTask().setType(AutoBatchSpiderVideo.value).setReason(RE_SPIDER_All_VIDEO_TAG.value);
        List<String> avList = touhouALlMapper.list(new BaseQuery<>().setPageSize(1000).setStart(start)).stream().map(String::valueOf).collect(Collectors.toList());
        taskManager.batchSpiderVideo(batchTask, avList);
        redisCache.set(RedisKeyEnum.SPIDER_ALL_VIDEO_TAG_REDIS_KEY.getKey(), start);
    }

}