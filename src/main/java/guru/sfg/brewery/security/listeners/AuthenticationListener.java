package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationListener {

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        log.debug("User has been successfully logged in");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();

                log.debug("User name logged in: " + user.getUsername());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                log.debug("Source IP: " + details.getRemoteAddress());
            }
        }
    }

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

        log.debug("Following user failed to log in: " + token.getPrincipal());
    }
}
