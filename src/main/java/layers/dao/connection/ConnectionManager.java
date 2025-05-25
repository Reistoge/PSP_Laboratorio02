package layers.dao.connection;
public class ConnectionManager {
    // 1. dependencia cruzada dentro de la capa dao con ConnectionManager,
    // ya que UserRepository ya hace uso de ConnectionManager -> ciclo de dependencias entre clases
    layers.dao.user.UserRepository userRepository = new layers.dao.user.UserRepository();
    // 2. Accediendo a la capa ui desde la capa dao -> dependencia cruzada entre capas
    layers.ui.main.MainController mainController = new layers.ui.main.MainController();
    public void connect() {
        System.out.println("Database connected.");
    }
}


