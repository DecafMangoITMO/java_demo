package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;

@Component
public class DataSourceErrorLogMapper {

    public static DataSourceErrorLog toEntity(DataSourceErrorLogDto dto) {
        if (dto.getStackTrace() == null)
            throw new NullPointerException("Stack trace is null");
        if (dto.getMessage() == null)
            throw new NullPointerException("Message is null");
        if (dto.getMethodSignature() == null)
            throw new NullPointerException("Method signature is null");

        return DataSourceErrorLog.builder()
                .stackTrace(dto.getStackTrace())
                .message(dto.getMessage())
                .methodSignature(dto.getMethodSignature())
                .build();
    }

    public static DataSourceErrorLogDto toDto(DataSourceErrorLog entity) {
        return DataSourceErrorLogDto.builder()
                .stackTrace(entity.getStackTrace())
                .message(entity.getMessage())
                .methodSignature(entity.getMethodSignature())
                .build();
    }

}
