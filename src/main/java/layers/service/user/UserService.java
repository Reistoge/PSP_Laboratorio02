package layers.service.user;



public class UserService {
    private final layers.dao.user.UserRepository repository = new layers.dao.user.UserRepository();
    // 6. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
    //  layers.ui.login.LoginController loginController = new layers.ui.login.LoginController();
    public void registerUser(String name) {
        repository.save(name);
    }

    public void getUserInfo(String name) {
        System.out.println("User info for: " + name);
    }
}