package com.cowerling.pmn.config;

import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.security.PasswordEncoderService;
import com.cowerling.pmn.security.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    private static final int TOKEN_TIME = 3600;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new SecurityUserService(userRepository)).passwordEncoder(passwordEncoderService.getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        http
                .addFilterBefore(characterEncodingFilter, CsrfFilter.class)
                .formLogin().loginPage("/login")
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                .httpBasic().realmName("PMNResultManage")
                .and()
                .authorizeRequests().antMatchers("/resources/**", "/login", "/user/register", "/user/registerSuccess", "/geoservice/analyzekey").permitAll().anyRequest().authenticated()
                .and()
                .authorizeRequests().antMatchers("/temporary/**").denyAll().anyRequest().authenticated()
                .and()
                .rememberMe().tokenValiditySeconds(TOKEN_TIME).key("PMNResultManageKey");
    }
}
