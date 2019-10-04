package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import modeles.Constants;
import modeles.StateManager;
import javafx.scene.Parent;
import javafx.scene.Scene;



public class Main extends Application {


	public final static String PROPERTY_FILE = "src/remember.properties";
	/**
	 * the main stage of app
	 */
	public static Stage mainStage;
	public static boolean checkPropertiesFile() {
		File f = new File(PROPERTY_FILE);
		if ( f.exists()) {
			return true;
		} else {
			return false;
		}
	}
	@Override
	public void start(Stage primaryStage) {
		Screen screen = Screen.getPrimary();
	    Rectangle2D bounds = screen.getVisualBounds();
		Parent root = null;
		double h = 0;
		double w = 0;
		mainStage=primaryStage;

		try {

				root = FXMLLoader.load(getClass().getResource(Constants.MAIN_SCREEN));
				w=bounds.getWidth();
				h=bounds.getHeight();
				primaryStage.setScene(new Scene(root,w,h));

				primaryStage.setTitle("ADE FX TEST");
			    primaryStage.setMaximized(true);
		        primaryStage.show();

		        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {


					@Override
					public void handle(WindowEvent event) {
						//Save App state
						StateManager.getInstance().saveState();
						primaryStage.close();
					}
				});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
