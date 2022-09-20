package com.icebear.stay.config;

import com.icebear.stay.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;


// let spring to generate all objects that Spring Security required
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    // Spring Boot automatically create dataSource for us
    private DataSource dataSource;

    @Autowired
    private JwtFilter jwtFilter;



    // authorization
    // check if a user have authority
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // antMatchers: HttpSecurity will only be invoked when matching the provided ant pattern
                // permitAll(): all requests are allowed on that particular path.
                .antMatchers(HttpMethod.POST, "/register/*").permitAll()
                .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
                // only host can send request of /stays/
                .antMatchers("/stays").hasAuthority("ROLE_HOST")
                // only host can send request of /stays/xxx
                .antMatchers("/stays/*").hasAuthority("ROLE_HOST")

                // visit after authentication
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // add our jwtFilter before Spring Security's filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
    // authenticate
    // check if a user can log in or nor
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // AuthenticationManager has a method: authenticate().
        // we will use authenticate() in AuthenticationService
        return super.authenticationManagerBean();
    }
}
