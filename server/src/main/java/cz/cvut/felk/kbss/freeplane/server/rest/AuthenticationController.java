package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.AuthenticationRequestDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.AuthenticationResponse;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;
import cz.cvut.felk.kbss.freeplane.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Objects;

/**
 * Authentication REST controller
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;

    public AuthenticationController(UserService userService, AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * Registration endpoint
     * @param userDto user request body for registration
     * @return HttpStatus code
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDto userDto) {
        Objects.requireNonNull(userDto, "Input user DTO must not be null");

        if (userService.existsByEmail(userDto.getEmail())) {
            throw new ApiResponseStatusException(HttpStatus.CONFLICT, "User with this e-mail already exists");
        }

        try {
            User user = userService.convertToEntity(userDto);
            userService.createUser(user);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while persisting the new user");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * Authentication endpoint
     * @param requestDto user request body for authentication
     * @return authentication token + user's role
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequestDto requestDto) {
        final AuthenticationResponse response;

        try {
            String email = requestDto.getEmail();
            String password = requestDto.getPassword();
            this.authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            String role = this.userService.getUserByEmail(email).isAdmin() ? "ADMIN" : "USER";
            response = new AuthenticationResponse(basicAuth(email, password), role);
        } catch (AuthenticationException e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while authenticating");
        }
        LOGGER.info("User with e-mail " + requestDto.getEmail() + " was successfully logged in");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
