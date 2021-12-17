package cz.cvut.felk.kbss.freeplane.server.service.impl;

import cz.cvut.felk.kbss.freeplane.server.dao.UserRepository;
import cz.cvut.felk.kbss.freeplane.server.model.User;
import cz.cvut.felk.kbss.freeplane.server.rest.dto.UserDto;
import cz.cvut.felk.kbss.freeplane.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }


    @Override
    @Transactional
    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void changePassword(User user) {
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepository.getUserByEmail(email) != null;
    }

    @Override
    public User convertToEntity(final UserDto userDto) {
        User user = new User();
        user.setFirstname(userDto.getFirstName());
        user.setLastname(userDto.getLastName());
        user.setAdmin(userDto.isAdmin());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setCreationDate(LocalDateTime.now());
        return user;
    }

    @Override
    public UserDto convertToUserDto(final User user) {
        UserDto userDto = new UserDto();
        userDto.setCollaboratorId(user.getCollaboratorId());
        userDto.setFirstName(user.getFirstname());
        userDto.setLastName(user.getLastname());
        userDto.setIsAdmin(user.isAdmin());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setCreationDate(user.getCreationDate());
        return userDto;
    }
}
