package org.biz.shopverse.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestSchedule {
    private static final Logger logger = LoggerFactory.getLogger(TestSchedule.class);

    // 1) 고정 간격(fixedRate): 메소드 시작 시점부터 N밀리초마다 실행
//    @Scheduled(fixedRate = 5000)
//    public void fixedRateTask() {
//        logger.info("1 fixedRate 실행 – {}", System.currentTimeMillis());
//    }

    // 2) 고정 지연(fixedDelay): 이전 실행 완료 시점부터 N밀리초 후에 실행
//    @Scheduled(fixedDelay = 7000)
//    public void fixedDelayTask() {
//        logger.info("2 fixedDelay 실행 – {}", System.currentTimeMillis());
//    }

    // 3) 초기 지연(initialDelay) + 고정 간격
//    @Scheduled(initialDelay = 2000, fixedRate = 10000)
//    public void initialDelayTask() {
//        logger.info("3 initialDelay 후 fixedRate 실행 – {}", System.currentTimeMillis());
//    }

    // 4) 크론 표현식(cron): UNIX cron 스타일
    //    ┌───────────── 초 (0–59)
    //    │ ┌───────────── 분 (0–59)
    //    │ │ ┌───────────── 시 (0–23)
    //    │ │ │ ┌───────────── 일 (1–31)
    //    │ │ │ │ ┌───────────── 월 (1–12)
    //    │ │ │ │ │ ┌───────────── 요일 (0–6) (일요일=0)
    //    │ │ │ │ │ │
    //    * * * * * *
//    @Scheduled(cron = "0 0/1 * * * *")  // 매 1분마다 실행
//    public void cronTask() {
//        logger.info("4 cron 실행 – {}", System.currentTimeMillis());
//    }
}
