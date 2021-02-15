package com.tilitili.job.job;

import com.tilitili.common.emnus.RedisKeyEnum;
import com.tilitili.common.entity.BatchTask;
import com.tilitili.common.entity.Owner;
import com.tilitili.common.entity.query.OwnerQuery;
import com.tilitili.common.manager.TaskManager;
import com.tilitili.common.mapper.OwnerMapper;
import com.tilitili.common.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.tilitili.common.emnus.TaskReason.RE_SPIDER_All_OWNER;
import static com.tilitili.common.emnus.TaskType.AutoBatchSpiderVideo;

@Slf4j
@Component
public class SpiderAllOwnerJob {
    private final TaskManager taskManager;
    private final OwnerMapper ownerMapper;
    private final RedisCache redisCache;

    @Autowired
    public SpiderAllOwnerJob(TaskManager taskManager, OwnerMapper ownerMapper, RedisCache redisCache) {
        this.taskManager = taskManager;
        this.ownerMapper = ownerMapper;
        this.redisCache = redisCache;
    }

    @Async
    @Scheduled(cron = "0 40 0/2 * * ?")
    protected void executeInternal() {
        Integer start = (Integer) redisCache.get(RedisKeyEnum.SPIDER_ALL_OWNER_REDIS_KEY.getKey());
        int count = ownerMapper.count(new OwnerQuery());
        if (start == null || start > count) {
            start = 0;
        }
        List<Owner> ownerList = ownerMapper.list(new OwnerQuery().setPageSize(1000).setStart(start));
        BatchTask batchTask = new BatchTask().setType(AutoBatchSpiderVideo.value).setReason(RE_SPIDER_All_OWNER.value);
        List<String> valueList = ownerList.stream().map(Owner::getUid).map(String::valueOf).collect(Collectors.toList());
        taskManager.batchSpiderVideo(batchTask, valueList);
        redisCache.set(RedisKeyEnum.SPIDER_ALL_OWNER_REDIS_KEY.getKey(), start);
    }

}