package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.dao.UserRepository;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import java.util.List;

public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new UserDetails(user);
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {

        final User tUser = buildUserFromToken(token);
        final User dbUser = userRepository.getUserByEmail(tUser.getEmail());

        final User result;
        if (dbUser != null) {
            if (!token.getIdentityUrl().equals(dbUser.getAuthenticatorUri())) {
                throw new IllegalStateException("Identity url for this user can not change:" + token.getIdentityUrl());
            }
            result = dbUser;
        } else {
            try {
                tUser.setAuthenticationType("OPENID");
                tUser.setAuthenticatorUri(token.getIdentityUrl());

                return null;
                //result = userService.createUser(tUser, false, false);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

        }
        return new UserDetails(result);
    }

    private User buildUserFromToken(OpenIDAuthenticationToken token) {
        final User user = new User();

        String firstName = null;
        String lastName = null;
        String email = null;
        String fullName = null;

        final List<OpenIDAttribute> attributes = token.getAttributes();
        for (OpenIDAttribute attribute : attributes) {

            if (attribute.getName().equals("firstname")) {
                firstName = attribute.getValues().get(0);
            }

            if (attribute.getName().equals("lastname")) {
                lastName = attribute.getValues().get(0);
            }

            if (attribute.getName().equals("email")) {
                email = attribute.getValues().get(0);
            }

            if (attribute.getName().equals("fullname")) {
                fullName = attribute.getValues().get(0);
            }

        }
        if (lastName == null || firstName == null) {
            user.setFirstname(fullName);
            user.setLastname("");
        } else {
            user.setLastname(lastName);
            user.setFirstname(firstName);
        }
        user.setEmail(email);
        user.setPassword("");

        return user;
    }
}
