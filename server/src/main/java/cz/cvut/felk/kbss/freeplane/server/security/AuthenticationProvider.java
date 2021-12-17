package cz.cvut.felk.kbss.freeplane.server.security;

import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.security.model.UserDetails;
import cz.cvut.felk.kbss.freeplane.server.service.UserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    public AuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String email = authentication.getName();

        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final User user = userDetails.getUser();
        final String credentials = authentication.getCredentials().toString();

        if (user == null || credentials == null || !passwordEncoder.matches(credentials, user.getPassword())) {
            throw new BadCredentialsException("Username/Password does not match for " + authentication.getPrincipal());
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
