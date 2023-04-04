package guru.sfg.brewery.web.controllers.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests((authorize) -> {
                            authorize
                                    .antMatchers("/").permitAll()
                                    .antMatchers("/beers/find", "/beers*").permitAll()
                                    .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll();
                        }
                )
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> {
                            authorize.antMatchers("/").permitAll();
                        }
                )
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();

        return http.build();
    }*/
}
