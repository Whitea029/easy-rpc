package fun.whitea.consumer;

import cn.hutool.setting.yaml.YamlUtil;
import fun.whitea.RpcApplication;
import fun.whitea.common.model.User;
import fun.whitea.common.service.UserService;
import fun.whitea.config.RpcConfig;
import fun.whitea.porxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
//        UserService userService = null;
//        User user = new User();
//        userService = ServiceProxyFactory.getProxy(UserService.class);
//        user.setName("whitea");
//        User u = userService.getUser(user);
//        if (u != null) {
//            System.out.println(u.getName());
//        } else {
//            System.out.println("user not found");
//        }
        RpcConfig rpc = RpcApplication.getConfig();
        System.out.println(rpc);
    }
}
