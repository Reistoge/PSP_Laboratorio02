package layers.ui.main;



public class MainController {
    private final layers.service.user.UserService userService = new layers.service.user.UserService();
    // 8. Dependencia cruzada en la capa ui
    //  layers.ui.user.UserView userView = new layers.ui.user.UserView();
    public void run() {
        userService.registerUser("Alice");
    }
}