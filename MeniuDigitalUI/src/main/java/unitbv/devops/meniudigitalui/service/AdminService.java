package unitbv.devops.meniudigitalui.service;

import unitbv.devops.meniudigitalui.entity.User;
import unitbv.devops.meniudigitalui.entity.UserRole;
import unitbv.devops.meniudigitalui.repository.UserRepository;
import java.util.List;

public class AdminService {
    private final UserRepository userRepository = new UserRepository();

    public List<User> getAllStaff() {
        return userRepository.findByRole(UserRole.STAFF);
    }

    public void addStaff(String username, String password) {
        User staff = new User(username, password, UserRole.STAFF);
        userRepository.save(staff);
    }

    public void deleteStaff(Integer userId) {
        userRepository.delete(userId);
    }

    public void updateStaff(User user) {
        userRepository.update(user);
    }
}

