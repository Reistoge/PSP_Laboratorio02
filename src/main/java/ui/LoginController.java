package ui;
import service.AuthService;
public class LoginController {
    private final AuthService authService = new AuthService();
    //3. Dependencia cruzada en capa ui accediendo a la clase UserView
    // UserView -> MainController-> LoginController -> UserView
    ui.UserView userView = new ui.UserView();
    public boolean login(String username, String password) {
        return authService.authenticate(username, password);
    }
}