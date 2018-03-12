package com.example.qiangke.task;

import com.example.qiangke.Manipulator.QiangKe2Manipulator;
import com.example.qiangke.Manipulator.QiangKeManipulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class QiangKeTask {

    private QiangKeManipulator manipulator;
    private QiangKe2Manipulator manipulator2;

    @Autowired
    public QiangKeTask(QiangKeManipulator manipulator,
                       QiangKe2Manipulator manipulator2) {
        this.manipulator = manipulator;
        this.manipulator2 = manipulator2;
    }

    @Scheduled(fixedDelay = 300)
    public void scheduler() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(manipulator:: task);
        pool.execute(manipulator2:: task);
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
