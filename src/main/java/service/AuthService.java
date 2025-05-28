package service;



public class AuthService {

    private final dao.UserRepository repository = new dao.UserRepository();

    public boolean authenticate(String user, String password) {
        // return repository.find(user).equals(password);
        return false;
    }
}