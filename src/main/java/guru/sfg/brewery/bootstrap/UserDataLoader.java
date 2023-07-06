package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepo;
import guru.sfg.brewery.repositories.security.RoleRepo;
import guru.sfg.brewery.repositories.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static guru.sfg.brewery.config.SecurityConfig.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataLoader implements CommandLineRunner {
    private final AuthorityRepo authorityRepo;
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepo.count() == 0) {
            loadSecurityDate();
        }
    }

    private void loadSecurityDate() {
        //beer auths
        Authority createBeer = authorityRepo.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepo.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepo.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepo.save(Authority.builder().permission("beer.delete").build());

        //customer auths
        Authority createCustomer = authorityRepo.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepo.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepo.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepo.save(Authority.builder().permission("customer.delete").build());

        //customer brewery
        Authority createBrewery = authorityRepo.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepo.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepo.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepo.save(Authority.builder().permission("brewery.delete").build());

        //beer order
        Authority createOrder = authorityRepo.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepo.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepo.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepo.save(Authority.builder().permission("order.delete").build());
        Authority createOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.delete").build());

        Role adminRole = roleRepo.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepo.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepo.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer,
                updateCustomer, deleteCustomer, createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder)));
        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery,
                createOrderCustomer, readOrderCustomer, updateOrderCustomer, deleteOrderCustomer)));
        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepo.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepo.save(User.builder()
                .username(ADMIN_USER_SPRING)
                .password(passwordEncoder.encode(ADMIN_PASS_TEST))
                .role(adminRole)
                .build());

        userRepo.save(User.builder()
                .username(CUSTOMER_USER_SCOTT)
                .password(passwordEncoder.encode(CUSTOMER_PASS_TIGER))
                .role(customerRole)
                .build());

        userRepo.save(User.builder()
                .username(CUSTOMER_USER_USER)
                .password(passwordEncoder.encode(CUSTOMER_PASS_PASSWORD))
                .role(customerRole)
                .build());

        userRepo.save(User.builder()
                .username(USER_USER_SPRING_2)
                .password(passwordEncoder.encode(USER_PASS_TEST_2))
                .role(userRole)
                .build());

        log.debug("Users Loaded: " + userRepo.count());
    }
}
