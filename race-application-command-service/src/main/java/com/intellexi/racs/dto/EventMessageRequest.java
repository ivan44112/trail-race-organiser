package com.intellexi.racs.dto;


import com.intellexi.racs.utils.OperationType;
import com.intellexi.racs.utils.Subscriber;
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
