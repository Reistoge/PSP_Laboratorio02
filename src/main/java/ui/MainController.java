package ui;

import service.UserService;
public class MainController {
    private final UserService userService = new UserService();
    //3. Dependencia cruzada en capa ui accediendo a la clase LoginController
    // UserView -> MainController-> LoginController -> UserView
    //ui.LoginController loginController = new ui.LoginController();
    public void run() {
        userService.registerUser("Alice");
    }
}