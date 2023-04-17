package guru.sfg.brewery.web.controllers.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static guru.sfg.brewery.config.SecurityConfig.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProtectedEndpointSecurityTest extends BaseSecurity {
    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .with(httpBasic(ADMIN_USER_SPRING, ADMIN_PASS_TEST)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .header("Api-Key", "spring").header("Api-Secret", "testXXXX"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBasicByUrlParams() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .param("username", "spring")
                        .param("password", "test"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerBasicByUrlParamsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .param("username", "spring")
                        .param("password", "testXXX"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerHttpBasicUserRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .with(httpBasic("spring2", "test2")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBreweriesForbiddenForAdmin() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic(ADMIN_USER_SPRING, ADMIN_PASS_TEST)))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void getBreweriesForbiddenForUser() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic(USER_USER_SPRING_2, USER_PASS_TEST_2)))
                .andExpect(status().isForbidden());
    }
    @Test
    void getBreweriesIndxForbiddenForAdmin() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic(ADMIN_USER_SPRING, ADMIN_PASS_TEST)))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void getBreweriesIndxForbiddenForUser() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic(USER_USER_SPRING_2, USER_PASS_TEST_2)))
                .andExpect(status().isForbidden());
    }
}
