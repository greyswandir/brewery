package guru.sfg.brewery.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class RestHeaderAuthFilter extends RestAuthFilterBase {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    protected String getSecret(HttpServletRequest request) {
        return request.getHeader("Api-Secret");
    }

    protected String getUserName(HttpServletRequest request) {
        return request.getHeader("Api-Key");
    }
}
