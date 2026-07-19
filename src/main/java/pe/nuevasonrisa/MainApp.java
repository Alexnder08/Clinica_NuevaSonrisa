package pe.nuevasonrisa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.util.AppLogger;

public class MainApp extends Application {

    private static final Logger LOGGER = AppLogger.getLogger(MainApp.class);

    @Override
    public void start(Stage stage) throws Exception {
        LOGGER.info("Inicio de la aplicación Nueva Sonrisa.");
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Nueva Sonrisa - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        LOGGER.info("Cierre de la aplicación Nueva Sonrisa.");
        DatabaseConnection.closePool();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
