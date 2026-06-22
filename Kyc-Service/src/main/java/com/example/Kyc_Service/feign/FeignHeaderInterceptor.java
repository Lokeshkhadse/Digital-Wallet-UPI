//package com.example.Action_Service.feign;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Component
//public class FeignHeaderInterceptor implements RequestInterceptor {
//
//    private final HttpServletRequest request;
//
//    public FeignHeaderInterceptor(HttpServletRequest request) {
//        this.request = request;
//    }
//
////    @Override
////    public void apply(RequestTemplate template) {
////        String user = request.getHeader("X-USER");
////        String roles = request.getHeader("X-ROLES");
////
////        if (user != null) template.header("X-USER", user);
////        if (roles != null) template.header("X-ROLES", roles);
////    }
//
//    @Override
//    public void apply(RequestTemplate template) {
//
//        RequestAttributes attrs =
//                RequestContextHolder.getRequestAttributes();
//
//        if (attrs instanceof ServletRequestAttributes servletAttrs) {
//
//            HttpServletRequest request =
//                    servletAttrs.getRequest();
//
//            String user = request.getHeader("X-USER");
//            String roles = request.getHeader("X-ROLES");
//
//            if (user != null)
//                template.header("X-USER", user);
//
//            if (roles != null)
//                template.header("X-ROLES", roles);
//
//        } else {
//
//            // Scheduler call
//            template.header("X-USER", "SYSTEM");
//            template.header("X-ROLES", "ROLE_SYSTEM");
//        }
//    }
//}

package com.example.Kyc_Service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignHeaderInterceptor implements RequestInterceptor {

    private final HttpServletRequest request;

    public FeignHeaderInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {

        RequestAttributes attrs =
                RequestContextHolder.getRequestAttributes();

        if (attrs instanceof ServletRequestAttributes servletAttrs) {

            HttpServletRequest request =
                    servletAttrs.getRequest();

            String user = request.getHeader("X-USER");
            String roles = request.getHeader("X-ROLES");

            if (user != null)
                template.header("X-USER", user);

            if (roles != null)
                template.header("X-ROLES", roles);

        } else {

            // Scheduler / async / non-request thread

            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()) {

                template.header("X-USER", auth.getName());

                String roles = auth.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

                template.header("X-ROLES", roles);

            } else {
                template.header("X-USER", "SYSTEM");
                template.header("X-ROLES", "ROLE_SYSTEM");
            }
        }
    }
}