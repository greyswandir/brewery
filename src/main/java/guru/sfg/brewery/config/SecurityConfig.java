package guru.sfg.brewery.config;

import guru.sfg.brewery.security.BasicAuthFilter;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import guru.sfg.brewery.security.UrlParamAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_USER = "USER";

    public static final String ADMIN_USER_SPRING = "spring";
    public static final String ADMIN_PASS_TEST = "test";
    public static final String CUSTOMER_USER_SCOTT = "scott";
    public static final String CUSTOMER_PASS_TIGER = "tiger";
    public static final String CUSTOMER_USER_USER = "user";
    public static final String CUSTOMER_PASS_PASSWORD = "password";
    public static final String USER_USER_SPRING_2 = "spring2";
    public static final String USER_PASS_TEST_2 = "test2";

    @Value("${spring.security.user.name}")
    private String login;

    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private final UserDetailsService userDetailsService;

    // need to use with SpringData JPA SpEL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public UrlParamAuthFilter urlParamAuthFilter(AuthenticationManager authenticationManager) {
        UrlParamAuthFilter filter = new UrlParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public BasicAuthFilter basicAuthFilter(AuthenticationManager authenticationManager) {
        BasicAuthFilter filter = new BasicAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().ignoringAntMatchers("/h2-console/**", "/api/v1/**");

        http.addFilterBefore(urlParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        /* Filter is ignoring @PreAuthorize conditions and passes through
        http.addFilterBefore(basicAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);*/

        http.authorizeRequests((authorize) -> {
                            authorize
                                    .antMatchers("/").permitAll()
                                    .antMatchers("/resources/**").permitAll()
                                    .antMatchers("/h2-console/**").permitAll();
                        }
                )
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(loginConfigurer -> {
                    loginConfigurer
                            .loginProcessingUrl("/login")
                            .loginPage("/").permitAll()
                            .successForwardUrl("/")
                            .defaultSuccessUrl("/")
                            .failureUrl("/?error");
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                            .logoutSuccessUrl("/?logout")
                            .permitAll();
                })
                .httpBasic()
                .and().rememberMe().key("sfg-key").userDetailsService(userDetailsService);

        http.headers().frameOptions().sameOrigin();
    }

/*    @Override
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
    }*/

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
