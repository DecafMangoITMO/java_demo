package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class MetricsDto {
    @JsonProperty("exec_time")
    private double execTime;
    @JsonProperty("method_name")
    private String methodName;
    private Object[] args;
}
