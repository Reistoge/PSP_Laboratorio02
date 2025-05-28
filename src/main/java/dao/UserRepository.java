package dao;
public class UserRepository {
    // Dependencia de layers.dao.ConnectionManager
     ConnectionManager connectionManager = new ConnectionManager();
    // 3. Accediendo a la capa service desde la capa dao -> violacion de la arquitectura en capas

    //layers.service.user.UserService userService = new layers.service.user.UserService();
    // 4. Accediendo a la capa ui desde la capa dao -> ciclo de dependencias o paquetes
    // MainController mainController = new  MainController();
    public void save(String name) {
        System.out.println("User " + name + " saved.");
    }
    public String find(String user) {
        return "secret";
    }
}