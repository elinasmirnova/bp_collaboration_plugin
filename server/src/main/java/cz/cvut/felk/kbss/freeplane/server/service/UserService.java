package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;

import java.util.List;

/**
 * User service interface
 */
public interface UserService {

    void createUser(User user);

    void deleteUser(User user);

    User getUserByEmail(String email);

    User getUserById(long id);

    List<User> getAllUsers();

    boolean existsByEmail(String email);

    User convertToEntity(UserDto userDto);

    UserDto convertToUserDto(User user);
}
