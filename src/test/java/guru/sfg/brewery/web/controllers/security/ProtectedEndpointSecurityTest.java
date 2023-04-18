package guru.sfg.brewery.web.controllers.security;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static guru.sfg.brewery.config.SecurityConfig.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ProtectedEndpointSecurityTest extends BaseSecurity {

    @Autowired
    BeerRepository beerRepository;

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.security.BaseSecurity#getStreamNotAdmin")
    void deleteBeerHttpBasicNotAuth(String user, String pwd) throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                        .with(httpBasic(user, pwd)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm{

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.security.BaseSecurity#getStreamAllUsers")
        void initCreationFormAuth(String user, String pwd) throws Exception {

            mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Find By UPC")
    class FindByUPC {
        @Test
        void findBeerByUpc() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/" + beerToDelete().getUpc()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.security.BaseSecurity#getStreamAllUsers")
        void findBeerByUpcAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/" + beerToDelete().getUpc())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

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
