package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepo;
import guru.sfg.brewery.repositories.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static guru.sfg.brewery.config.SecurityConfig.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataLoader implements CommandLineRunner {
    private final AuthorityRepo authorityRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepo.count() == 0) {
            loadSecurityDate();
        }
    }

    private void loadSecurityDate() {
        Authority admin = authorityRepo.save(Authority.builder().role("ADMIN").build());
        Authority user = authorityRepo.save(Authority.builder().role("USER").build());
        Authority customer = authorityRepo.save(Authority.builder().role("CUSTOMER").build());

        userRepo.save(User.builder()
                        .username(ADMIN_USER_SPRING)
                        .password(passwordEncoder.encode(ADMIN_PASS_TEST))
                        .authority(admin)
                .build());

        userRepo.save(User.builder()
                        .username(ADMIN_USER_SPRING_2)
                        .password(passwordEncoder.encode(ADMIN_PASS_TEST_2))
                        .authority(user)
                .build());

        userRepo.save(User.builder()
                        .username(USER_USER_USER)
                        .password(passwordEncoder.encode(USER_PASS_PASSWORD))
                        .authority(customer)
                .build());

        log.debug("Users Loaded: " + userRepo.count());
    }
}
