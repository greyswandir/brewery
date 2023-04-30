package guru.sfg.brewery.web.controllers.security;

import guru.sfg.brewery.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BeerControllerSecurityTest extends BaseSecurity {

    /**
     * Заглушает авторизацию, навязанную строкой ".apply(springSecurity())"
     */
    @WithMockUser
    @Test
    void findBeersWithAnonymous() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isForbidden());
    }

    /**
     * Проводит реальную авторизацию с доступами указанными в application.properties
     */
    @Test
    void findBeersWithBasicAuth() throws Exception {
        mockMvc.perform(get("/beers/find").with(httpBasic(login, password)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void createBeers() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic(
                SecurityConfig.ADMIN_USER_SPRING, SecurityConfig.ADMIN_PASS_TEST)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }
}