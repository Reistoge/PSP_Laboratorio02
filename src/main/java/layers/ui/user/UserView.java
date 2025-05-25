package layers.ui.user;



public class UserView {
    private final layers.service.user.UserService service = new layers.service.user.UserService();
    // 8. Dependencia cruzada en la capa ui
    // layers.ui.login.LoginController loginController = new layers.ui.login.LoginController();
    public void showUser(String name) {
        service.getUserInfo(name);
    }
}