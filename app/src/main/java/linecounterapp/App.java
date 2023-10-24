package linecounterapp;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class App extends Application {
	private FileChooser filePicker;
	private Button button; //Choose file button
	private Text result;
	private Stage mainstage;

	@Override
	public void start(Stage stage) {
		mainstage = stage;
		initPicker();
		
		VBox box = new VBox();
		
		Label itemLabel = new Label("Pick a file:");
		button = new Button("Browse");
		
		result = new Text();
		result.setWrappingWidth(600);
		
		setButtonListener();
		box.getChildren().addAll(itemLabel, button, result);

        Scene scene = new Scene(box, 640, 480);
        
        stage.setTitle("Java File Line Counter");
        stage.setScene(scene);
        stage.show();
	}
	
	
	private void initPicker() {
		filePicker = new FileChooser();
		filePicker.setTitle("Pick a .txt file");
		filePicker.getExtensionFilters().add(new ExtensionFilter(".java", "*.java") );
	}
	
	
	private void setButtonListener() {
		EventHandler<ActionEvent> listener = new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
    			File file = filePicker.showOpenDialog(mainstage);
        		result.textProperty().set("Hi!");
    		}
    	};
    	
    	button.setOnAction(listener);
	}

}
