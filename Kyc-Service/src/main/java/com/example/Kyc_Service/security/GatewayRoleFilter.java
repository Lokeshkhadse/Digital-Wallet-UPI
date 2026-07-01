    package com.example.Kyc_Service.security;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;

    import java.io.IOException;
    import java.util.Arrays;
    import java.util.List;


@Component
public class GatewayRoleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userHeader = request.getHeader("X-USER");
        String rolesHeader = request.getHeader("X-ROLES");

        System.out.println("User: " + userHeader);
        System.out.println("Roles: " + rolesHeader);

        if (rolesHeader != null && !rolesHeader.isEmpty()) {

            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(rolesHeader.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userHeader,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}