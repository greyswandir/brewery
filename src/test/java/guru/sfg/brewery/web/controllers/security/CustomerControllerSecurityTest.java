package guru.sfg.brewery.web.controllers.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static guru.sfg.brewery.config.SecurityConfig.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerSecurityTest extends BaseSecurity {

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.security.BaseSecurity#getStreamAdminCustomer")
    void testListCustomersAuth(String user, String pwd) throws Exception {
        mockMvc.perform(get("/customers")
                .with(httpBasic(user, pwd)))
                .andExpect(status().isOk());
    }

    @Test
    void testListCustomersNoAuth() throws Exception {
        mockMvc.perform(get("/customers")
                        .with(httpBasic(USER_USER_SPRING_2, USER_PASS_TEST_2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListCustomersNoLogIn() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Add Customers")
    @Nested
    class AddCustomers {

        @Rollback
        @Test
        void processCreationForm() throws Exception{
            mockMvc.perform(post("/customers/new")
                            .param("customerName", "Foo Customer")
                            .with(httpBasic(ADMIN_USER_SPRING, ADMIN_PASS_TEST)))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.security.BaseSecurity#getStreamNotAdmin")
        void processCreationFormNOTAUTH(String user, String pwd) throws Exception{
            mockMvc.perform(post("/customers/new")
                            .param("customerName", "Foo Customer2")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNOAUTH() throws Exception{
            mockMvc.perform(post("/customers/new")
                            .param("customerName", "Foo Customer"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
