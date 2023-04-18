package guru.sfg.brewery.web.controllers.security;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class OpenEndpointsSecurityTest extends BaseSecurity {

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
    void testGetIndexSlash() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
