package com.susstore.config;

import com.susstore.service.UserDetailServiceImpl;
import com.susstore.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html").permitAll()
                .failureUrl("/login/error").permitAll()
                .usernameParameter("username").passwordParameter("password")
                .and()
                .authorizeRequests()
                .antMatchers("/","/index.html","/login").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/account").permitAll()
                .antMatchers("/register","/register.html","/pre-register").permitAll()
                .anyRequest().authenticated()
                .and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailService)
                .and()
                .logout().logoutSuccessUrl("/index").permitAll()
                .and().csrf().disable();



        //super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // 从数据库读取的用户进行身份认证
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());
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
