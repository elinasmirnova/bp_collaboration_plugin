package cz.cvut.felk.kbss.freeplane.server.service;

import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;

import java.util.List;

public interface UserService {

    User createUser(User user);

    void deleteUser(User user);

    User getUserByEmail(String email);

    User getUserById(long id);

    List<User> getAllUsers();

    void changePassword(User user);

    void updateUser(User user);

    boolean existsByEmail(String email);

    User convertToEntity(UserDto userDto);

    UserDto convertToUserDto(User user);

}
