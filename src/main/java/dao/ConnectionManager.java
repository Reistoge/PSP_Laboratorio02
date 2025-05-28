package dao;

public class ConnectionManager {
    // 1. dependencia cruzada dentro de la capa dao con layers.dao.ConnectionManager,
    // ya que UserRepository ya hace uso de layers.dao.ConnectionManager -> ciclo de dependencias entre clases
    //  UserRepository userRepository = new UserRepository();
    // 2. Accediendo a la capa ui desde la capa dao -> dependencia cruzada entre capas
    // MainController mainController = new  MainController();
    //UserRepository userRepository = new UserRepository();
    public void connect() {
        System.out.println("Database connected.");
    }
}


