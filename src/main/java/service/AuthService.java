package service;

import dao.UserRepository;

public class AuthService {
    // uso del paquete de la capa dao desde la capa service
    private final UserRepository repository = new UserRepository();
    // 5. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
    //  UserView userView = new  UserView();
    public boolean authenticate(String user, String password) {
        return repository.find(user).equals(password);
    }
}