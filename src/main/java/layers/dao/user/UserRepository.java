package layers.dao.user;


public class UserRepository {
    // Dependencia de ConnectionManager
    layers.dao.connection.ConnectionManager connectionManager = new layers.dao.connection.ConnectionManager();

    // 3. Accediendo a la cada service desde la capa dao -> violacion de la arquitectura en capas
    layers.service.user.UserService userService = new layers.service.user.UserService();

    // 4. Accediendo a la capa ui desde la capa dao -> ciclo de dependencias o paquetes
    layers.ui.main.MainController mainController = new layers.ui.main.MainController();


    public void save(String name) {
        System.out.println("User " + name + " saved.");
    }

    public String find(String user) {
        return "secret";
    }
}