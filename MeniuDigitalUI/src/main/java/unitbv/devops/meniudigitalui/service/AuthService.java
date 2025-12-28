package unitbv.devops.meniudigitalui.service;

import unitbv.devops.meniudigitalui.entity.User;
import unitbv.devops.meniudigitalui.entity.UserRole;
import unitbv.devops.meniudigitalui.repository.UserRepository;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(u -> u.getPassword().equals(password));
    }

    public User register(String username, String password, UserRole role) {
        User user = new User(username, password, role);
        return userRepository.save(user);
    }
}
