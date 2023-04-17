package guru.sfg.brewery.web.controllers.security;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static guru.sfg.brewery.config.SecurityConfig.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProtectedEndpointSecurityTest extends BaseSecurity {

    @Autowired
    BeerRepository beerRepository;

    public Beer beerToDelete() {
        Random rand = new Random();

        return beerRepository.saveAndFlush(Beer.builder()
                .beerName("Delete Me Beer")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(100)
                .upc(String.valueOf(rand.nextInt(9999999)))
                .build());
    }
    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
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

    @Test
    void getBeersForAdmin() throws Exception {
        mockMvc.perform(get("/beers")
                        .with(httpBasic(ADMIN_USER_SPRING, ADMIN_PASS_TEST)))
                .andExpect(status().isOk());
    }
}
