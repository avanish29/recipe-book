package com.recipebook.config;

import com.recipebook.service.RecipeUserDetailsService;
import com.recipebook.service.TokenProvider;
import com.recipebook.web.filter.TokenAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author - AvanishKishorPandey
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableScheduling
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] AUTH_WHITELIST = {"/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js",
            "/swagger-resources/**", "/swagger-ui.html**", "/v3/api-docs", "/webjars/**"};

    @Autowired
    private RecipeUserDetailsService userDetailsService;

    @Autowired
    private TokenProvider tokenProvider;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenProvider tokenProvider) {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .csrf()
            .disable()
            .formLogin()
            .disable()
            .httpBasic()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint((httpServletRequest, httpServletResponse, authException) -> {
                log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage());
            })
        .and()
            .authorizeRequests()
            .antMatchers(AUTH_WHITELIST)
            .permitAll()
            .antMatchers("/signin/**", "/signup/**", "/refresh/**", "/swagger-ui/**")
            .permitAll()
            .anyRequest()
            .authenticated();

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
