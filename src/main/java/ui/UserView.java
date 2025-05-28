package ui;


import service.UserService;

public class UserView {
    private final UserService service = new UserService();
    // 8. Dependencia cruzada en la capa ui
    LoginController loginController = new LoginController();
    public void showUser(String name) {
        service.getUserInfo(name);
    }
}