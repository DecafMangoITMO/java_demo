package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricsDto;
import ru.t1.java.demo.kafka.KafkaMetricProducer;

import java.util.concurrent.atomic.AtomicLong;

@Async
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TrackingAspect {

    private static final AtomicLong START_TIME = new AtomicLong();

    private final KafkaMetricProducer kafkaMetricProducer;

    @Before("@annotation(ru.t1.java.demo.aop.Track)")
    public void logExecTime(JoinPoint joinPoint) throws Throwable {
        log.info("Старт метода: {}", joinPoint.getSignature().toShortString());
        START_TIME.addAndGet(System.currentTimeMillis());
    }

    @After("@annotation(ru.t1.java.demo.aop.Track)")
    public void calculateTime(JoinPoint joinPoint) {
        long afterTime = System.currentTimeMillis();
        log.info("Время исполнения: {} ms", (afterTime - START_TIME.get()));
        START_TIME.set(0L);
    }

    @Around("@annotation(ru.t1.java.demo.aop.Track)")
    public Object logExecTime(ProceedingJoinPoint pJoinPoint) {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();//Important
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        log.info("Время исполнения: {} ms", (afterTime - beforeTime));
        return result;
    }

    @Around("@annotation(metric)")
    public Object logExecTimeBounded(ProceedingJoinPoint pJoinPoint, Metric metric) throws Throwable {
        log.info("Вызов метода: {}", pJoinPoint.getSignature().toShortString());
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = pJoinPoint.proceed();
        } finally {
            long afterTime = System.currentTimeMillis();
            long execTime = afterTime - beforeTime;
            log.info("Время исполнения: {} ms", execTime);

            try {
                if (execTime > metric.intervalInMillis()) {
                    MetricsDto metricDto = MetricsDto.builder()
                            .execTime(execTime)
                            .methodName(pJoinPoint.getSignature().getDeclaringTypeName() + "." + pJoinPoint.getSignature().getName())
                            .args(pJoinPoint.getArgs())
                            .build();

                    kafkaMetricProducer.send(metricDto);
                }
            } finally {
                return result;
            }
        }
    }

}
