package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class LogErrorOrMetrics {

    private DataSourceErrorLogDto dataSourceErrorLogDto;

    private MetricsDto metricsDto;

}
