package guru.sfg.brewery.web.controllers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static guru.sfg.brewery.config.SecurityConfig.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseSecurity {
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Value("${spring.security.user.name}")
    public String login;

    @Value("${spring.security.user.password}")
    public String password;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    public static Stream<Arguments> getStreamAllUsers() {
        return Stream.of(Arguments.of(ADMIN_USER_SPRING , ADMIN_PASS_TEST),
                Arguments.of(CUSTOMER_USER_SCOTT, CUSTOMER_PASS_TIGER),
                Arguments.of(CUSTOMER_USER_USER, CUSTOMER_PASS_PASSWORD),
                Arguments.of(USER_USER_SPRING_2, USER_PASS_TEST_2));
    }

    public static Stream<Arguments> getStreamNotAdmin() {
        return Stream.of(Arguments.of(CUSTOMER_USER_SCOTT, CUSTOMER_PASS_TIGER),
                Arguments.of(USER_USER_SPRING_2, USER_PASS_TEST_2));
    }
}
