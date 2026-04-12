package com.example.esDemo.controller;

import com.example.esDemo.es.EsManager;
import com.example.esDemo.model.AppResult;
import com.example.esDemo.model.Product;
import com.example.esDemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private EsManager esManager;

    @GetMapping(value = "/users")
    public AppResult<List<User>> getUserList(){
        User user = new User();
        user.setName("ahhaha");
        user.setId(123L);
        return AppResult.isSuccess(Collections.singletonList(user));
    }

    @GetMapping(value = "/users/search")
    public AppResult<List<User>> searchUsers(@RequestParam(required = false) String name) {
        // 模拟数据，实际应从数据库或ES查询
        User user1 = new User();
        user1.setId(1L);
        user1.setName("张三");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("李四");
        List<User> users = new java.util.ArrayList<>();
        users.add(user1);
        users.add(user2);
        if (name != null && !name.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getName().contains(name))
                    .collect(java.util.stream.Collectors.toList());
        }
        return AppResult.isSuccess(users);
    }

    @GetMapping(value = "/products")
    public AppResult<List<Product>> getProductList() throws Exception {
        List<Product> list = esManager.searchByKeyword();
        return AppResult.isSuccess(list);
    }

    @GetMapping(value = "/users/{id}")
    public AppResult<User> getUserById(@PathVariable Long id) {
        // 模拟数据，实际应从数据库或ES查询
        User user = new User();
        user.setId(id);
        user.setName("用户" + id);
        return AppResult.isSuccess(user);
    }
}
