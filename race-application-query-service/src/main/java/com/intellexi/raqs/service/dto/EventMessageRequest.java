package com.intellexi.raqs.service.dto;

import com.intellexi.raqs.utils.OperationType;
import com.intellexi.raqs.utils.Subscriber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventMessageRequest<T> {
    private OperationType operationType;
    private T payload;
    private Subscriber subscriber;
}