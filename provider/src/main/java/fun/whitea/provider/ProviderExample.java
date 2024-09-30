package fun.whitea.provider;


import fun.whitea.common.service.UserService;
import fun.whitea.easyrpc.bootstrap.ProviderBootstrap;
import fun.whitea.easyrpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
        serviceRegisterInfoList.add(new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class));

        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
