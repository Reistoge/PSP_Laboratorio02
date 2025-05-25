package layers.ui.login;
public class LoginController {
    private final layers.service.auth.AuthService authService = new layers.service.auth.AuthService();
    // 7. uso de la capa dao en la capa ui -> violacion de arquitectura en capas
    //layers.dao.user.UserRepository userRepository = new layers.dao.user.UserRepository();
    // 8. Dependencia cruzada en la capa ui
   // layers.ui.main.MainController mainController = new layers.ui.main.MainController();
    public boolean login(String username, String password) {

        return authService.authenticate(username, password);
    }
}