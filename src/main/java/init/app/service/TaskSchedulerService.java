package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Hours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TaskSchedulerService {

    @Autowired
    TaskScheduler taskScheduler;
    @Inject
    TagService tagService;

    private static final long TRENDING_INTERVAL = TimeUnit.HOURS.toMillis(4);

    @PostConstruct
    private void resetActivity() {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay((Runnable) () -> {
            try {
                tagService.updateTagsTotalActivity();
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        }, TRENDING_INTERVAL);
    }
}
