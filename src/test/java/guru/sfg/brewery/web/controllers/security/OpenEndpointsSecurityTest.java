package guru.sfg.brewery.web.controllers.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class OpenEndpointsSecurityTest extends BaseSecurity {

    @Test
    void testGetIndexSlash() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                .andExpect(status().isOk());
    }
    @Test
    void findBeersByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0675890930000"))
                .andExpect(status().isOk());
    }
}
