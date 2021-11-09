package com.susstore.config.security;

import com.susstore.config.security.*;
import com.susstore.filter.AuthenticationFilter;
import com.susstore.filter.JwtAuthenticationTokenFilter;
import com.susstore.filter.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsUtils;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomizeAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomizeAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomizeAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomizeLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private CustomizeAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    private HttpSecurity httpSecurity;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        this.httpSecurity = http;
        //配置验证码
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/code/image").permitAll();

        http
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin()
                .loginProcessingUrl("/login")
                .usernameParameter("email").passwordParameter("password")
                .and()
                .authorizeRequests()
                .antMatchers("/","/index","/login").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/account","/user/**","/user_picture_default.png").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui",
                        "/swagger-resources", "/swagger-resources/configuration/security",
                        "/swagger-ui.html", "/webjars/**").permitAll()
                //.anyRequest().authenticated()
                .and()/*.rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(3600)*/
                .logout().logoutSuccessUrl("/index").permitAll();

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and().formLogin().permitAll()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and().logout().permitAll()
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .clearAuthentication(true);
        http.addFilterAt(authenticationFilter(),UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.httpBasic();
        http.cors().and().csrf().disable().authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .and().authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();

        http.headers().cacheControl();
        http.headers().frameOptions().disable();

    }

    AuthenticationFilter authenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用BCrypt加密密码
        return new BCryptPasswordEncoder();
    }

}
