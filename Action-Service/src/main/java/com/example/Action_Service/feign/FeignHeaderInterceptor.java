package com.example.Action_Service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class FeignHeaderInterceptor implements RequestInterceptor {

    private final HttpServletRequest request;

    public FeignHeaderInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {
        String user = request.getHeader("X-USER");
        String roles = request.getHeader("X-ROLES");

        if (user != null) template.header("X-USER", user);
        if (roles != null) template.header("X-ROLES", roles);
    }
}