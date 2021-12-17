package cz.cvut.felk.kbss.freeplane.server.security.utils;

import cz.cvut.felk.kbss.freeplane.server.security.model.UserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.transaction.Transactional;

public class SecurityUtils {

    public static UserDetails getCurrentUserDetails() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().getPrincipal() instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }
}
