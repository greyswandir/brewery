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
                        .username("spring")
                        .password(passwordEncoder.encode("test"))
                        .authority(admin)
                .build());

        userRepo.save(User.builder()
                        .username("spring2")
                        .password(passwordEncoder.encode("test2"))
                        .authority(user)
                .build());

        userRepo.save(User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .authority(customer)
                .build());

        log.debug("Users Loaded: " + userRepo.count());
    }
}
