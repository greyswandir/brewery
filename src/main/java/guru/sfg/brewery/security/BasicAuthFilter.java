package guru.sfg.brewery.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthFilter extends RestAuthFilterBase {

    public BasicAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    protected String getSecret(HttpServletRequest request) {
        String loginPass = "";
        try {
            loginPass = new String(Base64.getDecoder().decode(request.getHeader("Authorization").substring("Basic".length()).trim()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
        return loginPass.split(":", 2)[1];
    }

    protected String getUserName(HttpServletRequest request) {
        String loginPass = "";
        try {
            loginPass = new String(Base64.getDecoder().decode(request.getHeader("Authorization").substring("Basic".length()).trim()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
        return loginPass.split(":", 2)[0];
    }
}
