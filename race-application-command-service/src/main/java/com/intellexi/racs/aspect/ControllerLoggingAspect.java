package com.intellexi.racs.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.invoke.MethodHandles;

@Aspect
@Component
public class ControllerLoggingAspect {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Before(value = "@within(com.intellexi.racs.aspect.LogRequests)")
    public void logRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getMethod()).append(" request --> ").append(request.getRequestURI());
        String queryParams = request.getQueryString();
        if (queryParams != null) {
            stringBuilder.append("?").append(queryParams);
        }

        LOG.info(stringBuilder.toString());
    }

}