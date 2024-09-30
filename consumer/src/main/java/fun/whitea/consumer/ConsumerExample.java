package fun.whitea.consumer;

import fun.whitea.common.model.User;
import fun.whitea.common.service.UserService;
import fun.whitea.easyrpc.bootstrap.ConsumerBootstrap;
import fun.whitea.easyrpc.porxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("whitea");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
