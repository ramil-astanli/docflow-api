package com.ramilastanli.docflow.security.filter;

import com.ramilastanli.docflow.config.ApplicationConstants;
import com.ramilastanli.docflow.entity.Role;
import com.ramilastanli.docflow.security.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER);

        if (null != authHeader && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                Environment env = getEnvironment();

                if (null != env) {
                    String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                            ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(secretKey)
                            .build()
                            .parseClaimsJws(jwt)
                            .getBody();

                    String email = String.valueOf(claims.get("email"));
                    String rolesStr = String.valueOf(claims.get("roles")); // Məsələn: "ROLE_ADMIN"
                    Long id = Long.valueOf(String.valueOf(claims.get("id")));

                    String cleanerRole = rolesStr.replace("ROLE_", "");
                    Role roleEnum = Role.valueOf(cleanerRole);

                    // 3. CustomUserDetails konstruktorunu 5 arqumentlə çağırırıq
                    CustomUserDetails userDetails = new CustomUserDetails(
                            id,
                            email,
                            null,
                            roleEnum,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(rolesStr)
                    );

                    // 4. Authentication obyektini yaradırıq
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (ExpiredJwtException exception) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token vaxtı bitib. Yenidən giriş edin.\"}");
                return;
            } catch (Exception exception) {
                logger.error("JWT Validation failed: " + exception.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Yanlış Token təqdim edildi!\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath(); // URI yerinə ServletPath daha etibarlıdır

        if (path.startsWith("/ws-notifications")) {
            return true;
        }

        // Siyahıdakı yollarla uyğunluğu yoxlayırıq
        return publicPaths.stream().anyMatch(p -> pathMatcher.match(p, path));
    }
}