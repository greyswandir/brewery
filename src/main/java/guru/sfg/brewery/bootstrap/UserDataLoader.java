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

import java.util.Arrays;
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

    @Override
    public void run(String... args) throws Exception {
        if (authorityRepo.count() == 0) {
            loadSecurityDate();
        }
    }

    private void loadSecurityDate() {
        Authority createBeer = authorityRepo.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepo.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepo.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepo.save(Authority.builder().permission("beer.delete").build());

        Role adminRole = roleRepo.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepo.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepo.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer));
        customerRole.setAuthorities(Set.of(readBeer));
        userRole.setAuthorities(Set.of(readBeer));

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
