package com.recipebook.web.filter;

import com.recipebook.service.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author - AvanishKishorPandey
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer ";
    private final TokenProvider tokenProvider;

    public TokenAuthenticationFilter(final TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request - Defines an object to provide client request information to a servlet.
     * @param response - Defines an object to assist a servlet in sending a response to the client.
     * @param filterChain - chain of a filtered request for a resource.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = resolveToken(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Get Bearer token from request header.
     * @param httpServletRequest - Defines an object to provide client request information to a servlet.
     * @return - Bearer token
     */
    private String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(bearerToken) && bearerToken.trim().startsWith(AUTHENTICATION_SCHEME_BEARER)) {
            return bearerToken.substring(AUTHENTICATION_SCHEME_BEARER.length());
        }
        return null;
    }
}
