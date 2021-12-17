package cz.cvut.felk.kbss.freeplane.server.security;

import cz.cvut.felk.kbss.freeplane.server.model.enums.UserRoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cz.cvut.felk.kbss.freeplane.server.model.enums.UserRoleEnum.ROLE_ADMIN;
import static cz.cvut.felk.kbss.freeplane.server.model.enums.UserRoleEnum.ROLE_USER;

public class AuthenticationSuccess implements AuthenticationSuccessHandler {

    //protected Logger logger = Logger.(this.getClass());

    private final static String ROLE_USER_ENTRY_ENDPOINT = "rest/v1/homepage";
    private final static String ROLE_ADMIN_ENTRY_ENDPOINT = "rest/v1/admin/dashboard";


    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        saveCookie("authenticationState", "logged", httpServletResponse);

        String targetUrl = determineTargetUrl(authentication);

        if (httpServletResponse.isCommitted()) {
//            logger.debug
            return;
        }


        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
        clearAuthenticationAttributes(httpServletRequest);
    }

    private String determineTargetUrl(final Authentication authentication) {

        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put(ROLE_USER.name(), ROLE_USER_ENTRY_ENDPOINT);
        roleTargetUrlMap.put(ROLE_ADMIN.name(), ROLE_ADMIN_ENTRY_ENDPOINT);

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }
        throw new IllegalStateException();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    private static void saveCookie(String cookieName, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, value);
        //maxAge is one month: 30*24*60*60
        cookie.setMaxAge(2592000);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
