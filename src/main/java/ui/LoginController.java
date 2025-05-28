package ui;

import service.AuthService;

public class LoginController {
    private final AuthService authService = new AuthService();
    // 7. uso de la capa dao en la capa ui -> violacion de arquitectura en capas
    // UserRepository userRepository = new UserRepository();
    // 8. Dependencia cruzada en la capa ui
   // MainController mainController = new MainController();

    MainController mainController = new MainController();
    public boolean login(String username, String password) {

        return authService.authenticate(username, password);
    }
}