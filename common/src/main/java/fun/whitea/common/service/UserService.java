package fun.whitea.common.service;

import fun.whitea.common.model.User;

public interface UserService {

    User getUser(User user);

    default int getAge() {
        return 1;
    }

}
