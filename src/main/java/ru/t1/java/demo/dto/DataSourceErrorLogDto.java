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
public class DataSourceErrorLogDto {
    @JsonProperty("stack_trace")
    private String stackTrace;
    private String message;
    @JsonProperty("method_signature")
    private String methodSignature;
}
