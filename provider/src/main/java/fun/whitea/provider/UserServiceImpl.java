package fun.whitea.provider;

import fun.whitea.common.model.User;
import fun.whitea.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("username is " + user.getName());
        return user;
    }
}
