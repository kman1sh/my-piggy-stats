package com.manish.accountservice.client;

import com.manish.accountservice.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {


    @PostMapping(path = "/users")
    void createUser(User user);
}
