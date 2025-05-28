package service;

import dao.UserRepository;

public class UserService {
    private final UserRepository repository = new UserRepository();
    // 6. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
    // LoginController loginController = new LoginController();
    public void registerUser(String name) {
        repository.save(name);
    }

    public void getUserInfo(String name) {
        System.out.println("User info for: " + name);
    }
}