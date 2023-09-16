package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepo;
import guru.sfg.brewery.repositories.security.LoginSuccessRepo;
import guru.sfg.brewery.repositories.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationListener {

    private final LoginFailureRepo loginFailureRepo;

    private final LoginSuccessRepo loginSuccessRepo;

    private final UserRepo userRepo;

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        log.debug("User has been successfully logged in");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();
                builder.user(user);

                log.debug("User name logged in: " + user.getUsername());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginSuccess loginSuccess = loginSuccessRepo.save(builder.build());
            log.debug("Login success saved. Id: " + loginSuccess.getId());
        }
    }

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

        String username = (String) token.getPrincipal();
        log.debug("Following user failed to log in: " + username);

        LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

        builder.username(username);
        userRepo.findByUsername(username).ifPresent(builder::user);

        LoginFailure failure = loginFailureRepo.save(builder.build());

        if (failure.getUser() != null) {
            lockUserAccount(failure.getUser());
        }
    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failures = loginFailureRepo.findAllByUserAndCreatedDateAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if (failures.size() > 3) {
            log.debug("Locking User Account ...");
            user.setAccountNonLocked(false);
            userRepo.save(user);
        }
    }
}
