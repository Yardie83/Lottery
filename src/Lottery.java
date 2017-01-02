import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Hermann Grieder on 12.10.2016.
 *
 * Entry point for the application. Creates the model, the view and the controller for the application
 */
public class Lottery extends Application {

    public static void main(String[] args){
        launch( args );
    }

    @Override
    public void start( Stage stage ) throws Exception {
        Model model = new Model();
        View view = new View(stage);
        new Controller(view, model).start();
    }
}
