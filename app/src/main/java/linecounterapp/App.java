package linecounterapp;

import java.io.File;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class App extends Application {
	TableView<CountValue> methodTable;
	TableView<CountValue> controlTable;
	private FileChooser filePicker;
	private Button button; //Choose file button
	private Text result;
	private Stage mainstage;

	@Override
	public void start(Stage stage) {
		mainstage = stage;
		initPicker();
		initTables();
		
		VBox box = new VBox();
		
		Label itemLabel = new Label("Pick a file:");
		button = new Button("Browse");
		
		result = new Text();
		result.setWrappingWidth(1200);
		
		setButtonListener();
		box.getChildren().addAll(itemLabel, button, result, methodTable, controlTable);

        Scene scene = new Scene(box, 640, 480);
        
        stage.setTitle("Java File Line Counter");
        stage.setScene(scene);
        stage.show();
	}
	
	
	private void initPicker() {
		filePicker = new FileChooser();
		filePicker.setTitle("Pick a .java file");
		filePicker.getExtensionFilters().add(new ExtensionFilter(".java", "*.java") );
	}
	
	private void initTables() {
		methodTable = new TableView<>();
		methodTable.setEditable(false);
		controlTable = new TableView<>();
		controlTable.setEditable(false);
		
		TableColumn<CountValue, String> methodNameCol = new TableColumn<>("Method Name");
		methodNameCol.setMinWidth(150);
		TableColumn<CountValue, Integer> methodLengthCol = new TableColumn<>("Number of Lines");
		methodLengthCol.setMinWidth(150);
		methodTable.getColumns().addAll(methodNameCol, methodLengthCol);
		
		TableColumn<CountValue, String> controlNameCol = new TableColumn<>("Control Type");
		controlNameCol.setMinWidth(150);
		TableColumn<CountValue, Integer> controlLengthCol = new TableColumn<>("Count");
		controlLengthCol.setMinWidth(150);
		controlTable.getColumns().addAll(controlNameCol, controlLengthCol);
	}
	
	
	private void setButtonListener() {
		EventHandler<ActionEvent> listener = new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
    			File file = filePicker.showOpenDialog(mainstage);
    			if (file != null) {
    				LineCounter counter = new LineCounter();
            		result.textProperty().set(counter.analyzeFile(file));
            		
            		ObservableList<CountValue> methodList = counter.getMethodsList();
            		ObservableList<CountValue> controlList = counter.getControlList();
            		
            		methodTable.setItems(methodList);
            		controlTable.setItems(controlList);
            		
            		methodTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>(methodList.get(0).nameProperty().getName()));
            		methodTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>(methodList.get(0).countProperty().getName()));

            		controlTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>(controlList.get(0).nameProperty().getName()));
            		controlTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>(controlList.get(0).countProperty().getName()));
    			}
    		}
    	};
    	
    	button.setOnAction(listener);
	}

}
