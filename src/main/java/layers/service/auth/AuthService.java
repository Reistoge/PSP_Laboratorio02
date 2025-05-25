package layers.service.auth;



public class AuthService {
    // uso del paquete de la capa dao desde la capa service
    private final layers.dao.user.UserRepository repository = new layers.dao.user.UserRepository();

    // 5. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
    // layers.service.user.UserView userView = new layers.service.user.UserView();


    public boolean authenticate(String user, String password) {
        return repository.find(user).equals(password);
    }
}