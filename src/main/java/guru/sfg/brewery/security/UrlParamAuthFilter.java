package guru.sfg.brewery.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class UrlParamAuthFilter extends RestAuthFilterBase {

    public UrlParamAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    protected String getSecret(HttpServletRequest request) {
        return request.getParameter("password");
    }

    protected String getUserName(HttpServletRequest request) {
        return request.getParameter("username");
    }
}
