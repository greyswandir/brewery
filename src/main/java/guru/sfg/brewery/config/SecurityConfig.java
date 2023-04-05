package guru.sfg.brewery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_USER_SPRING_2 = "spring2";
    public static final String ADMIN_PASS_TEST_2 = "test2";
    public static final String USER_USER_USER = "user";
    public static final String USER_PASS_PASSWORD = "password";

    @Value("${spring.security.user.name}")
    private String login;

    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests((authorize) -> {
                            authorize
                                    .antMatchers("/").permitAll()
                                    .antMatchers("/beers/find").permitAll()
                                    .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                                    .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(login)
                .password(password)
                .roles("ADMIN")
                .and()
                .withUser(ADMIN_USER_SPRING_2)
                .password(ADMIN_PASS_TEST_2)
                .roles("ADMIN")
                .and()
                .withUser(USER_USER_USER)
                .password(USER_PASS_PASSWORD)
                .roles("USER");
    }

    /*@Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(login)
                .password(password)
                .roles("ADMIN")
                .build();

        UserDetails admin2 = User.withDefaultPasswordEncoder()
                .username(ADMIN_USER_SPRING_2)
                .password(ADMIN_PASS_TEST_2)
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username(USER_USER_USER)
                .password(USER_PASS_PASSWORD)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, admin2, user);
    }*/

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
