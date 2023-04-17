package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import guru.sfg.brewery.security.UrlParamAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public UrlParamAuthFilter urlParamAuthFilter(AuthenticationManager authenticationManager) {
        UrlParamAuthFilter filter = new UrlParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http.addFilterBefore(urlParamAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class);

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
        PasswordEncoder passwordEncoder = passwordEncoder();
        auth.inMemoryAuthentication()
                .withUser(login)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN")
                .and()
                .withUser(ADMIN_USER_SPRING_2)
                .password(passwordEncoder.encode(ADMIN_PASS_TEST_2))
                .roles("ADMIN")
                .and()
                .withUser(USER_USER_USER)
                .password(passwordEncoder.encode(USER_PASS_PASSWORD))
                .roles("USER");

        auth.inMemoryAuthentication().withUser("scott").password(passwordEncoder.encode("tiger")).roles("CUSTOMER");
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
