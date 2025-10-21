package org.biz.shopverse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    // Schedule 병렬 실행을 위한 설정
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 1) 스레드 풀 크기 설정
        //    - 풀에 생성될 스레드 최대 개수.
        //    - 동시성 처리량을 CPU 코어 수나 메모리 여유에 맞춰 조절 가능.
        scheduler.setPoolSize(determinePoolSize());

        // 2) 스레드 이름 접두어 설정
        //    - 디버깅/로깅 시 어떤 스레드가 스케줄러에서 생성된 것인지 쉽게 구분하기 위함.
        scheduler.setThreadNamePrefix("sched-");

        // 3) 애플리케이션 종료 시 종료 대기 시간 (초)
        //    - 이 시간 동안 남아있는 작업이 끝나기를 기다림.
        scheduler.setAwaitTerminationSeconds(60);

        // 4) 애플리케이션 종료 시 스케줄러에 할당된 작업을 완료할 때까지 기다릴지 여부
        //    - true: shutdown 시점에 대기하고, 지정된 awaitTerminationSeconds 안에 완료되지 않으면 강제 종료.
        //    - false: 즉시 스레드를 종료.
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        return scheduler;
    }

    /**
     * 내 컴퓨터(서버)의 CPU 코어 수에 기반해 스레드 풀 크기를 동적으로 결정하는 예시.
     * 필요에 따라 메모리, 환경변수 등을 함께 고려하도록 확장할 수도 있습니다.
     */
    private int determinePoolSize() {
        // 사용 가능한 프로세서(코어) 수
        int cores = Runtime.getRuntime().availableProcessors();

        // 코어 수 × 2 스레드, 최소 1개 보장
        int calculated = cores;
        return Math.max(calculated, 1);
    }
}