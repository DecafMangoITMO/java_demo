package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class LogAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Pointcut("within(ru.t1.java.demo.*)")
    public void loggingMethods() {

    }

    @Before("@annotation(LogExecution)")
    @Order(1)
    public void logAnnotationBefore(JoinPoint joinPoint) {
        log.info("ASPECT BEFORE ANNOTATION: Call method: {}", joinPoint.getSignature().getName());
    }

//    @Before("execution(public * ru.t1.java.demo.service.client.ClientService.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        log.error("ASPECT BEFORE: Call method: {}", joinPoint.getSignature().getName());
//    }

    @AfterThrowing(pointcut = "@annotation(LogException)")
    @Order(0)
    public void logExceptionAnnotation(JoinPoint joinPoint) {
        System.err.println("ASPECT EXCEPTION ANNOTATION: Logging exception: {}" + joinPoint.getSignature().getName());
    }

    @AfterReturning(
            pointcut = "@annotation(HandlingResult)",
            returning = "result")
    public void handleResult(JoinPoint joinPoint, List<Client> result) {
        log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());
//        log.info("получен результат: {} ", result);
        log.info("Подробности: \n");

        result = isNull(result) ? List.of() : result;

    }

    @AfterThrowing(
            pointcut = "@annotation(LogDataSourceError)",
            throwing = "e"
    )
    public void logDataSourceError(JoinPoint joinPoint, Exception e) {
        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .stackTrace(Arrays.toString(e.getStackTrace()))
                .message(e.getMessage())
                .methodSignature(joinPoint.getSignature().getName())
                .build();

        dataSourceErrorLogRepository.save(dataSourceErrorLog);
    }

}
