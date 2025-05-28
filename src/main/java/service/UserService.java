package service;
import dao.UserRepository;
public class UserService {
    private final UserRepository repository = new UserRepository();
    //3. UserService accediendo a la clase LoginController
    // ui.LoginController loginController = new ui.LoginController();
    public void registerUser(String name) {
       repository.save(name);
    }
    public void getUserInfo(String name) {
        System.out.println("User info for: " + name);
    }
}