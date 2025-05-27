# PSP_Laboratorio02

Construccion de una herramienta automatica de analisis estatico que permita verificar si un sistema Java, basada en el principio de capas, cumple con la  arquitectura planeada.
## La arquitectura planeada se conforma de 3 capas principales
 * Capa de Presentación (UI): contiene controladores que interactúan con los usuarios.
 * Capa de Servicios (Service): define la lógica del negocio.
 * Capa de Persistencia (DAO): se encarga del acceso a datos.

## Reglas de dependencias
 * Capa de Presentación (UI): contiene controladores que interactúan con los usuarios.
 * Capa de Servicios (Service): define la lógica del negocio.
 * Capa de Persistencia (DAO): se encarga del acceso a datos.

<p align="center">
<img width="596" alt="image" src="https://github.com/user-attachments/assets/9faa010a-21ed-466d-9e5a-80c7bcfe2abe" /> </p>

# Setup y consideraciones de arquitectura

Para garantizar el cumplimiento de las reglas de dependencia y mantener una arquitectura limpia y desacoplada, se han aplicado una serie de estrategias que se explican a continuación.

## Estructura del Proyecto
El código fuente se organiza bajo el paquete raíz layers, subdividido en tres capas principales:

````scss
layers
├───ui       (capa de presentación)
├───service  (capa de lógica de negocio)
└───dao      (capa de acceso a datos)
````
Cada clase concreta está ubicada en su propio subpaquete específico dentro de su capa, por ejemplo:
````scss
layers.ui.login.LoginController
layers.service.auth.AuthService
layers.dao.user.UserRepository
````
Para el apartado de testing y validacion de dependencias el codigo se organiza bajo el directorio test, el cual contiene dos clases
````scss
test
├───CrossDependenciesTest.java 
└───LayersRules.java 
 
````
CrossDependenciesTest: Define las reflas necesarias para que no existan dependencias cruzadas entre paquetes y clases.
LayersRules: Define las reglas necesarias para cumplir la arquitectura en capas UI->Service->DAO
 
## Validacion de dependencias 

Se utilizo principalmente la librería ArchUnit 

### Reglas de acceso entre capas:
 * Las clases de UI solo pueden acceder a clases de Service.
 * Las clases de Service solo pueden acceder a clases de DAO.
 * No se permite ningún acceso directo entre UI y DAO.

### Evitar dependencias cruzadas dentro de una misma capa:
Ademas de la deteccion de ciclos entre paquete se considero detectar ciclos de dependencia entre clases de una misma capa, se aplica slices() de ArchUnit sobre los subpaquetes individuales.
Esto permite validar que, aunque se permiten accesos dentro de una misma capa, no se generen ciclos, los cuales degradarían la mantenibilidad y claridad del diseño.

### Ejemplo válido:
````
A (Service.user) → B (Service.auth)
````
### Ejemplo inválido (ciclo):
````
A → B → A
````
## Acceso libre entre clases de capas inferiores:

Si una capa tiene permitido acceder a otra (por ejemplo, UI puede acceder a Service), no se limita el acceso a clases específicas, a menos que el modelo lo indique explícitamente.

Por lo tanto, una clase como LoginController (en UI) puede acceder a AuthService o UserService (en Service), ya que ambas están dentro de la capa permitida.
 ## Resultados del análisis de prueba.

 ### 1. Dependencia cruzada dentro de la capa dao con ConnectionManager
````java
package layers.dao.connection;
public class ConnectionManager {
    // UserRepository ya hace uso de ConnectionManager -> ciclo de dependencias entre clases
    layers.dao.user.UserRepository userRepository = new layers.dao.user.UserRepository();
    public void connect() {
        System.out.println("Database connected.");
    }
}
````
### Resultado de test CrossDependenciesTest.java
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'slices matching '..dao.(*)' should be free of cycles, because No debe haber dependencias cruzadas dentro de DAO' was violated (1 times):
Cycle detected: Slice connection -> 
                Slice user -> 
                Slice connection
  1. Dependencies of Slice connection
    - Field <layers.dao.connection.ConnectionManager.userRepository> has type <layers.dao.user.UserRepository> in (ConnectionManager.java:0)
    - Constructor <layers.dao.connection.ConnectionManager.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (ConnectionManager.java:8)
  2. Dependencies of Slice user
    - Field <layers.dao.user.UserRepository.connectionManager> has type <layers.dao.connection.ConnectionManager> in (UserRepository.java:0)
    - Constructor <layers.dao.user.UserRepository.<init>()> calls constructor <layers.dao.connection.ConnectionManager.<init>()> in (UserRepository.java:6)
````
 ### 2. Dependencia cruzada entre capas
````java
package layers.dao.connection;
public class ConnectionManager {
    // Accediendo a la capa ui desde la capa dao
    layers.ui.main.MainController mainController = new layers.ui.main.MainController();
    public void connect() {
        System.out.println("Database connected.");
    }
}
````
### Resultado de test CrossDependenciesTest.java
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'slices matching 'layers.(*)..' should be free of cycles, because No debe haber dependencias cruzadas entre capas' was violated (1 times):
Cycle detected: Slice dao -> 
                Slice ui -> 
                Slice service -> 
                Slice dao
  1. Dependencies of Slice dao
    - Field <layers.dao.connection.ConnectionManager.mainController> has type <layers.ui.main.MainController> in (ConnectionManager.java:0)
    - Constructor <layers.dao.connection.ConnectionManager.<init>()> calls constructor <layers.ui.main.MainController.<init>()> in (ConnectionManager.java:10)
  2. Dependencies of Slice ui
    - Field <layers.ui.login.LoginController.authService> has type <layers.service.auth.AuthService> in (LoginController.java:0)
    - Field <layers.ui.main.MainController.userService> has type <layers.service.user.UserService> in (MainController.java:0)
    - Field <layers.ui.user.UserView.service> has type <layers.service.user.UserService> in (UserView.java:0)
    - Constructor <layers.ui.login.LoginController.<init>()> calls constructor <layers.service.auth.AuthService.<init>()> in (LoginController.java:6)
    - Constructor <layers.ui.main.MainController.<init>()> calls constructor <layers.service.user.UserService.<init>()> in (MainController.java:6)
    - Constructor <layers.ui.user.UserView.<init>()> calls constructor <layers.service.user.UserService.<init>()> in (UserView.java:6)
    - Method <layers.ui.main.MainController.run()> calls method <layers.service.user.UserService.registerUser(java.lang.String)> in (MainController.java:10)
    - Method <layers.ui.user.UserView.showUser(java.lang.String)> calls method <layers.service.user.UserService.getUserInfo(java.lang.String)> in (UserView.java:10)
    - Method <layers.ui.login.LoginController.login(java.lang.String, java.lang.String)> calls method <layers.service.auth.AuthService.authenticate(java.lang.String, java.lang.String)> in (LoginController.java:15)
  3. Dependencies of Slice service
    - Field <layers.service.auth.AuthService.repository> has type <layers.dao.user.UserRepository> in (AuthService.java:0)
    - Field <layers.service.user.UserService.repository> has type <layers.dao.user.UserRepository> in (UserService.java:0)
    - Constructor <layers.service.user.UserService.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (UserService.java:6)
    - Constructor <layers.service.auth.AuthService.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (AuthService.java:7)
    - Method <layers.service.user.UserService.registerUser(java.lang.String)> calls method <layers.dao.user.UserRepository.save(java.lang.String)> in (UserService.java:10)
    - Method <layers.service.auth.AuthService.authenticate(java.lang.String, java.lang.String)> calls method <layers.dao.user.UserRepository.find(java.lang.String)> in (AuthService.java:14)
````
 ### 3. Violacion de la arquitectura en capas
````java
package layers.dao.user;
public class UserRepository {
    // Dependencia de ConnectionManager
     layers.dao.connection.ConnectionManager connectionManager = new layers.dao.connection.ConnectionManager();
    // 3. Accediendo a la capa service desde la capa dao -> violacion de la arquitectura en capas
    layers.service.user.UserService userService = new layers.service.user.UserService();
    public void save(String name) {
        System.out.println("User " + name + " saved.");
    }
    public String find(String user) {
        return "secret";
    }
}
````
### Resultado de test LayersRules.java
````console
 java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'no classes that reside in a package '..dao..' should access classes that reside in a package '..service..', because DAO no debe depender de Service' was violated (1 times):
Constructor <layers.dao.user.UserRepository.<init>()> calls constructor <layers.service.user.UserService.<init>()> in (UserRepository.java:7)
````


 ### 4. ciclo de dependencias o paquetes
````java
package layers.dao.user;
public class UserRepository {
    // Dependencia de ConnectionManager
     layers.dao.connection.ConnectionManager connectionManager = new layers.dao.connection.ConnectionManager();
    // 4. Accediendo a la capa ui desde la capa dao
    layers.ui.main.MainController mainController = new layers.ui.main.MainController();
    public void save(String name) {
        System.out.println("User " + name + " saved.");
    }
    public String find(String user) {
        return "secret";
    }
}
````
### Resultado de test CrossDependenciesTest.java
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'slices matching 'layers.(*)..' should be free of cycles, because No debe haber dependencias cruzadas entre capas' was violated (1 times):
Cycle detected: Slice dao -> 
                Slice ui -> 
                Slice service -> 
                Slice dao
  1. Dependencies of Slice dao
    - Field <layers.dao.user.UserRepository.mainController> has type <layers.ui.main.MainController> in (UserRepository.java:0)
    - Constructor <layers.dao.user.UserRepository.<init>()> calls constructor <layers.ui.main.MainController.<init>()> in (UserRepository.java:9)
  2. Dependencies of Slice ui
    - Field <layers.ui.login.LoginController.authService> has type <layers.service.auth.AuthService> in (LoginController.java:0)
    - Field <layers.ui.main.MainController.userService> has type <layers.service.user.UserService> in (MainController.java:0)
    - Field <layers.ui.user.UserView.service> has type <layers.service.user.UserService> in (UserView.java:0)
    - Constructor <layers.ui.login.LoginController.<init>()> calls constructor <layers.service.auth.AuthService.<init>()> in (LoginController.java:6)
    - Constructor <layers.ui.main.MainController.<init>()> calls constructor <layers.service.user.UserService.<init>()> in (MainController.java:6)
    - Constructor <layers.ui.user.UserView.<init>()> calls constructor <layers.service.user.UserService.<init>()> in (UserView.java:6)
    - Method <layers.ui.main.MainController.run()> calls method <layers.service.user.UserService.registerUser(java.lang.String)> in (MainController.java:10)
    - Method <layers.ui.user.UserView.showUser(java.lang.String)> calls method <layers.service.user.UserService.getUserInfo(java.lang.String)> in (UserView.java:10)
    - Method <layers.ui.login.LoginController.login(java.lang.String, java.lang.String)> calls method <layers.service.auth.AuthService.authenticate(java.lang.String, java.lang.String)> in (LoginController.java:15)
  3. Dependencies of Slice service
    - Field <layers.service.auth.AuthService.repository> has type <layers.dao.user.UserRepository> in (AuthService.java:0)
    - Field <layers.service.user.UserService.repository> has type <layers.dao.user.UserRepository> in (UserService.java:0)
    - Constructor <layers.service.user.UserService.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (UserService.java:6)
    - Constructor <layers.service.auth.AuthService.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (AuthService.java:7)
    - Method <layers.service.user.UserService.registerUser(java.lang.String)> calls method <layers.dao.user.UserRepository.save(java.lang.String)> in (UserService.java:10)
    - Method <layers.service.auth.AuthService.authenticate(java.lang.String, java.lang.String)> calls method <layers.dao.user.UserRepository.find(java.lang.String)> in (AuthService.java:14)

````

 ### 5. violacion de arquitectura en capas desde AuthService  
````java
package layers.service.auth;
public class AuthService {
    // uso del paquete de la capa dao desde la capa service
    private final layers.dao.user.UserRepository repository = new layers.dao.user.UserRepository();
    // 5. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
     layers.ui.user.UserView userView = new layers.ui.user.UserView();
    public boolean authenticate(String user, String password) {
        return repository.find(user).equals(password);
    }
}
````
### Resultado de test LayersRules.java
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'classes that reside in a package '..service..' should only access classes that reside in any package ['..service..', '..dao..', 'java..', 'javax..'], because Service solo debe acceder a DAO y clases propias o del JDK' was violated (1 times):
Constructor <layers.service.auth.AuthService.<init>()> calls constructor <layers.ui.user.UserView.<init>()> in (AuthService.java:6)
````
 ### 6. violacion de arquitectura en capas desde UserService 
````java
package layers.service.user;
public class UserService {
    private final layers.dao.user.UserRepository repository = new layers.dao.user.UserRepository();
    // 6. uso de la capa ui desde la capa service -> violacion de arquitectura en capas
    layers.ui.login.LoginController loginController = new layers.ui.login.LoginController();
    public void registerUser(String name) {
        repository.save(name);
    }
    public void getUserInfo(String name) {
        System.out.println("User info for: " + name);
    }
}
````
### Resultado de test LayersRules
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'classes that reside in a package '..service..' should only access classes that reside in any package ['..service..', '..dao..', 'java..', 'javax..'], because Service solo debe acceder a DAO y clases propias o del JDK' was violated (1 times):
Constructor <layers.service.user.UserService.<init>()> calls constructor <layers.ui.login.LoginController.<init>()> in (UserService.java:6)

````

 ### 7. Violacion de arquitectura en capas desde UserService (uso de capa DAO en capa UI)
````java
package layers.ui.login;
public class LoginController {
    private final layers.service.auth.AuthService authService = new layers.service.auth.AuthService();
    // 7. uso de la capa dao en la capa ui -> violacion de arquitectura en capas
    layers.dao.user.UserRepository userRepository = new layers.dao.user.UserRepository();
    public boolean login(String username, String password) {
        return authService.authenticate(username, password);
    }
}
````
### Resultado de test LayersRules
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'classes that reside in a package '..ui..' should only access classes that reside in any package ['..ui..', '..service..', 'java..', 'javax..'], because UI solo debe acceder a Service (además de Java estándar)' was violated (1 times):
Constructor <layers.ui.login.LoginController.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (LoginController.java:5)

java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'no classes that reside in a package '..ui..' should access classes that reside in a package '..dao..', because UI no debe depender directamente de DAO' was violated (1 times):
Constructor <layers.ui.login.LoginController.<init>()> calls constructor <layers.dao.user.UserRepository.<init>()> in (LoginController.java:5)


````

 ### 8. Dependencia cruzada en la capa UI
````java
package layers.ui.login;
public class LoginController {
    // dependencia LoginController hacia authService
    private final layers.service.auth.AuthService authService = new layers.service.auth.AuthService();

    // 8. Dependencia cruzada en la capa ui
    layers.ui.main.MainController mainController = new layers.ui.main.MainController();
    public boolean login(String username, String password) {
        return authService.authenticate(username, password);
    }
}
````
````java
package layers.ui.main;
public class MainController {
    private final layers.service.user.UserService userService = new layers.service.user.UserService();
    // 8. Dependencia cruzada en la capa ui
    layers.ui.user.UserView userView = new layers.ui.user.UserView();
    public void run() {
        userService.registerUser("Alice");
    }
}
````
````java
package layers.ui.user;
public class UserView {
    private final layers.service.user.UserService service = new layers.service.user.UserService();
    // 8. Dependencia cruzada en la capa ui
    layers.ui.login.LoginController loginController = new layers.ui.login.LoginController();
    public void showUser(String name) {
        service.getUserInfo(name);
    }
}
````
### Resultado de test CrossDependenciesTest.java
````console
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'slices matching '..ui.(*)' should be free of cycles, because No debe haber dependencias cruzadas dentro de UI' was violated (1 times):
Cycle detected: Slice login -> 
                Slice main -> 
                Slice user -> 
                Slice login
  1. Dependencies of Slice login
    - Field <layers.ui.login.LoginController.mainController> has type <layers.ui.main.MainController> in (LoginController.java:0)
    - Constructor <layers.ui.login.LoginController.<init>()> calls constructor <layers.ui.main.MainController.<init>()> in (LoginController.java:7)
  2. Dependencies of Slice main
    - Field <layers.ui.main.MainController.userView> has type <layers.ui.user.UserView> in (MainController.java:0)
    - Constructor <layers.ui.main.MainController.<init>()> calls constructor <layers.ui.user.UserView.<init>()> in (MainController.java:5)
  3. Dependencies of Slice user
    - Field <layers.ui.user.UserView.loginController> has type <layers.ui.login.LoginController> in (UserView.java:0)
    - Constructor <layers.ui.user.UserView.<init>()> calls constructor <layers.ui.login.LoginController.<init>()> in (UserView.java:8)
````

