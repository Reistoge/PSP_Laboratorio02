package ui;

import service.UserService;

public class MainController {
    private final UserService userService = new UserService();
    // 8. Dependencia cruzada en la capa ui
    UserView userView = new UserView();

    public void run() {
        userService.registerUser("Alice");
    }
}