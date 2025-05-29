package ui;
public class UserView {
    private final service.UserService service = new service.UserService();
    // 1. Capa en UI accediendo direactmante en la capa dao
    //private final dao.UserRepository repository = new dao.UserRepository();
    // 2. Clase UserView accediendo a la clase ConnectionManager
    //dao.ConnectionManager connection = new dao.ConnectionManager();
    //3. Dependencia cruzada dentro de la capa ui accediendo a la clase MainController
    // UserView -> MainController-> LoginController -> UserView
    ui.MainController mainController = new ui.MainController();
    public void showUser(String name) {
        service.getUserInfo(name);
    }
}