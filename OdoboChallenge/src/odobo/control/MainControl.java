package odobo.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import odobo.calculate.RectGenerator;
import odobo.calculate.bean.GeneratorConfig;
import odobo.calculate.bean.OutputBean;
import odobo.calculate.bean.RectangleBean;
import odobo.control.component.InputPanel;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Main control class. Builds UI and calls calculatin
 * 
 * @author anna
 * 
 */
public class MainControl extends Application {
	
	private static final int CANVAS_SIZE = 600;
	private static final int SCENE_SIZE = 800;

	private Text textOutput = new Text("JSON to generate");
	private IntegerProperty numberOfRects = new SimpleIntegerProperty(GeneratorConfig.DEFAULT_NUMBER_OF_RECTS);
	private Stage primaryStage;
	private GraphicsContext gc;
	private GeneratorConfig config = new GeneratorConfig();

	/**
	 * Handler for file saving
	 */
	private EventHandler<ActionEvent> saveButtonHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog
			File file = fileChooser.showSaveDialog(primaryStage);

			if (file != null) {
				SaveFile(textOutput.getText(), file);
			}
		}
	};

	/**
	 * Handler for generating rects
	 */
	private EventHandler<ActionEvent> generateButtonHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			config.setNumberOfRects(numberOfRects.get());
			generate(false);
		}
	};
	
	/**
	 * Handler for generating rects
	 */
	private EventHandler<ActionEvent> loadButtonHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			generate(true);
		}
	};

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setTitle("Odobo challenge");
		Group root = new Group();

		Button buttonSave = ButtonBuilder.create().text("Save").build();
		buttonSave.setOnAction(saveButtonHandler);

		Button buttonGenerate = ButtonBuilder.create().text("Generate").build();
		buttonGenerate.setOnAction(generateButtonHandler);
		
		Button buttonFromFile = ButtonBuilder.create().text("Load from test file").build();
		buttonFromFile.setOnAction(loadButtonHandler);

		Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
		gc = canvas.getGraphicsContext2D();

		VBox vBoxGen = VBoxBuilder.create().children(new InputPanel("Ener number of boxes", numberOfRects), canvas)
				.build();

		VBox vBoxGenSave = VBoxBuilder.create().children(new TitledPane("JSON output", textOutput)).build();

		HBox hBox = HBoxBuilder.create().children(vBoxGen, vBoxGenSave).build();
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setStyle("-fx-border-color: #336699; -fx-border-width: 1;");

		HBox hboxButtons = HBoxBuilder.create().children(buttonGenerate, buttonFromFile, buttonSave).build();
		hboxButtons.setSpacing(10);
		hboxButtons.setPadding(new Insets(15, 12, 15, 12));
		hboxButtons.setStyle("-fx-background-color: #336699;");

		VBox vBox = VBoxBuilder.create().children(hboxButtons, hBox).build();

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(vBox);
		scroll.setPrefSize(SCENE_SIZE, SCENE_SIZE);
		root.getChildren().add(scroll);

		primaryStage.setScene(new Scene(root, SCENE_SIZE, SCENE_SIZE));
		primaryStage.show();

	}

	/**
	 * Generates rectangles and draws graph
	 */
	private void generate(boolean fromFile) {
		gc.clearRect(0, 0, SCENE_SIZE, SCENE_SIZE);
		gc.setStroke(Color.RED);
		gc.setLineWidth(1);
		List<RectangleBean> initList;
		
		if (fromFile){
			initList = readTest().getSourceRectangles();
		} else {
			initList = RectGenerator.generateInitialRectangles(config);
		}

		List<RectangleBean> resList = RectGenerator.reCutRectanglesInHorisontalDirection(initList);
		gc.setFill(Color.LIGHTSLATEGREY);
		for (RectangleBean rectangleBean : resList) {
			gc.fillRect(rectangleBean.getX(), rectangleBean.getY() - rectangleBean.getHeight(),
					rectangleBean.getWidth(), rectangleBean.getHeight());
			gc.strokeRect(rectangleBean.getX(), rectangleBean.getY() - rectangleBean.getHeight(),
					rectangleBean.getWidth(), rectangleBean.getHeight());
		}
		gc.setStroke(Color.GREEN);
		for (RectangleBean rectangleBean : initList) {
			gc.strokeRect(rectangleBean.getX(), rectangleBean.getY() - rectangleBean.getHeight(),
					rectangleBean.getWidth(), rectangleBean.getHeight());
		}

		OutputBean outputBean = new OutputBean();
		outputBean.setNumRects(config.getNumberOfRects());
		outputBean.setSourceRectangles(initList);
		outputBean.setRectangles(resList);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String res = gson.toJson(outputBean);
		textOutput.setText(res);
	}
	
	private OutputBean readTest() {
		OutputBean res = null;
		try {
			String text = new Scanner( new File("C:/Users/anna/Downloads/testq.txt"), "UTF-8" ).useDelimiter("\\A").next();
			Gson gson = new  Gson();
			res = gson.fromJson(text, OutputBean.class);
			System.out.println(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Saves output to file
	 * 
	 * @param content
	 *            output content
	 * @param file
	 *            File
	 */
	private void SaveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			Logger.getLogger(MainControl.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
