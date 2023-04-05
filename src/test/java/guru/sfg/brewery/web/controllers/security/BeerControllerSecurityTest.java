package guru.sfg.brewery.web.controllers.security;

import guru.sfg.brewery.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class BeerControllerSecurityTest extends BaseSecurity {

    /**
     * Заглушает авторизацию, навязанную строкой ".apply(springSecurity())"
     */
    @WithMockUser
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
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
    void findBeersWithAnonymous() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic(
                SecurityConfig.USER_USER_USER, SecurityConfig.USER_PASS_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }
}