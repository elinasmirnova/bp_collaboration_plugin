package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.AuthenticationRequestDto;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.AuthenticationResponse;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;
import cz.cvut.felk.kbss.freeplane.server.security.utils.SecurityUtils;
import cz.cvut.felk.kbss.freeplane.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;

    public AuthenticationController(UserService userService, AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.authenticationProvider = authenticationProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDto userDto) {
        Objects.requireNonNull(userDto, "Input user DTO must not be null");

        if (userService.existsByEmail(userDto.getEmail())) {
            throw new ApiResponseStatusException(HttpStatus.CONFLICT, "");
        }

        try {
            User user = userService.convertToEntity(userDto);
            userService.createUser(user);
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Some error occurred while persisting the new user");
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

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
        System.out.println(response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/state")
    public String getAuthenticationState() {
        final String response;
        if (SecurityUtils.getCurrentUserDetails() != null) {
            response = "logged";
        } else {
            response = "not logged";
        }

        return response;
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
