package cz.cvut.felk.kbss.freeplane.server.rest;

import cz.cvut.felk.kbss.freeplane.server.exception.ApiResponseStatusException;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;
import cz.cvut.felk.kbss.freeplane.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User REST controller
 */
@RestController
@CrossOrigin
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creating a new user endpoint
     *
     * @param userDto user DTO request body
     * @return HttpStatus code
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto) {
        if (this.userService.existsByEmail(userDto.getEmail())) {
            throw new ApiResponseStatusException(HttpStatus.CONFLICT, "");
        }
        try {
            this.userService.createUser(this.userService.convertToEntity(userDto));
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Deleting user endpoint
     *
     * @param id user's id path variable
     * @return HttpStatus code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        final User user = this.userService.getUserById(id);
        if (user == null) {
            throw new ApiResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + "does not exist");
        }
        this.userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Fetching user endpoint
     *
     * @param id user's id path variable
     * @return user DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        final User user = this.userService.getUserById(id);
        if (user == null) {
            throw new ApiResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + "does not exist");
        }
        final UserDto userDto = this.userService.convertToUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Fetching all the users endpoint
     *
     * @return collection of user DTO
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        final List<UserDto> userDtoList;
        try {
            userDtoList = this.userService.getAllUsers().stream().map(userService::convertToUserDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }
}
