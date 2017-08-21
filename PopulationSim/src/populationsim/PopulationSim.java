/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Ryan
 */
public class PopulationSim extends Application {
    
    ArrayList<Image> frameBuffer;
    Boolean cont;
    long seed;
    StringValue display;
    StringValue buttonInstruction;
    Random rand = new Random();
    Coordinate mouseClickCoord;
    StringValue clickInstruction;
    @Override
    public void start(Stage primaryStage) {
        display= new StringValue("Terrain");
        buttonInstruction = new StringValue("Nothing");
        mouseClickCoord = new Coordinate();
        clickInstruction = new StringValue("Nothing");
        PointPopInfo ppI = new PointPopInfo();
        primaryStage.setMaximized(true);
        Pane root = new Pane();
        //Canvas
        Canvas canvas = new Canvas();
        canvas.setHeight(800);
        canvas.setWidth(1000);
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        
        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked((MouseEvent event) -> {
            mouseClickCoord.x = event.getX();
            mouseClickCoord.y = event.getY();
            clickInstruction.string = "Canvas";
        });
        
        //Resume button
        Button resumeBtn = new Button();
        resumeBtn.setText("Resume");
        resumeBtn.setLayoutX(0);
        resumeBtn.setLayoutY(800);
        resumeBtn.setOnAction((ActionEvent event) -> {
            resumeSim();
        });
        root.getChildren().add(resumeBtn);
        //End of resume button
        
        //Pause button
        Button pauseBtn = new Button();
        pauseBtn.setText("Pause");
        pauseBtn.setLayoutX(100);
        pauseBtn.setLayoutY(800);
        pauseBtn.setOnAction((ActionEvent event) -> {
            pauseSim();
        });
        root.getChildren().add(pauseBtn);
        //End of pause button
        
        //Kill Off low pop button
        Button killBtn = new Button();
        killBtn.setText("Kill pops with low size");
        killBtn.setLayoutX(150);
        killBtn.setLayoutY(800);
        killBtn.setOnAction((ActionEvent event) -> {
            buttonInstruction.string = "KillLowPop";
        });
        root.getChildren().add(killBtn);
        //End of pause button
        
        //View Buttons
        //Height View
        Button heightBtn = new Button();
        heightBtn.setText("Height Map");
        heightBtn.setLayoutX(1000);
        heightBtn.setLayoutY(0);
        heightBtn.setOnAction((ActionEvent event) -> {
            display.string ="Height";
        });
        root.getChildren().add(heightBtn);
        //Moisture View
        Button moistureBtn = new Button();
        moistureBtn.setText("Moisture Map");
        moistureBtn.setLayoutX(1000);
        moistureBtn.setLayoutY(30);
        moistureBtn.setOnAction((ActionEvent event) -> {
            display.string ="Moisture";
        });
        root.getChildren().add(moistureBtn);
        //Heat View
        Button heatBtn = new Button();
        heatBtn.setText("Heat Map");
        heatBtn.setLayoutX(1000);
        heatBtn.setLayoutY(60);
        heatBtn.setOnAction((ActionEvent event) -> {
            display.string ="Heat";
        });
        root.getChildren().add(heatBtn);
        
        //Terrain View
        Button terrainBtn = new Button();
        terrainBtn.setText("Terrain Map");
        terrainBtn.setLayoutX(1000);
        terrainBtn.setLayoutY(90);
        terrainBtn.setOnAction((ActionEvent event) -> {
            display.string ="Terrain";
        });
        root.getChildren().add(terrainBtn);
        
        //Nutrients View
        Button nutrientsBtn = new Button();
        nutrientsBtn.setText("Nutrients Map");
        nutrientsBtn.setLayoutX(1000);
        nutrientsBtn.setLayoutY(120);
        nutrientsBtn.setOnAction((ActionEvent event) -> {
            display.string ="Nutrients";
        });
        root.getChildren().add(nutrientsBtn);
        
        //First in tile View
        Button firstInBtn = new Button();
        firstInBtn.setText("First In Tile Map");
        firstInBtn.setLayoutX(1000);
        firstInBtn.setLayoutY(150);
        firstInBtn.setOnAction((ActionEvent event) -> {
            display.string ="FirstIn";
        });
        root.getChildren().add(firstInBtn);
        
        //First in veg tile View
        Button firstInVegBtn = new Button();
        firstInVegBtn.setText("First In Tile Vegetation Map");
        firstInVegBtn.setLayoutX(1000);
        firstInVegBtn.setLayoutY(180);
        firstInVegBtn.setOnAction((ActionEvent event) -> {
            display.string ="FirstInVeg";
        });
        root.getChildren().add(firstInVegBtn);
        
        //First in veg tile View
        Button firstInLandBtn = new Button();
        firstInLandBtn.setText("First In Tile Land Map");
        firstInLandBtn.setLayoutX(1000);
        firstInLandBtn.setLayoutY(210);
        firstInLandBtn.setOnAction((ActionEvent event) -> {
            display.string ="FirstInLand";
        });
        root.getChildren().add(firstInLandBtn);
        
        //Info
        //Title
        Label mapPointInfo = new Label("MapPoint Information:");
        mapPointInfo.setLayoutX(800);
        mapPointInfo.setLayoutY(1080);
        root.getChildren().add(mapPointInfo);
        //Coord
        Label coord = new Label("Coord:");
        coord.setLayoutX(1920);
        coord.setLayoutY(1090);
        root.getChildren().add(coord);
        Label coordInfo = new Label();
        coordInfo.setLayoutX(1970);
        coordInfo.setLayoutY(1090);
        root.getChildren().add(coordInfo);
        
        Scene scene = new Scene(root, 1920, 1080);
        
        primaryStage.setTitle("Simulator");
        primaryStage.setScene(scene);
        
        frameBuffer = new ArrayList<Image>();
        seed = rand.nextLong();
        
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception{
                World world = new World(frameBuffer,(int)canvas.getWidth(),(int)canvas.getHeight(),seed,display,buttonInstruction, mouseClickCoord, clickInstruction, ppI);
                
                return null;
                
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        
        LongValue lastNanoTime = new LongValue( System.nanoTime() );
        DoubleValue timeCount = new DoubleValue(0);
        cont = true;
        AnimationTimer aTimer = new AnimationTimer(){
            @Override
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime-lastNanoTime.value)/1000000000.0;
                lastNanoTime.value = currentNanoTime;
                timeCount.value += elapsedTime;
                if((!frameBuffer.isEmpty())&&timeCount.value>=(1/60)&&cont){
                    display();
                    updateInfo();
                }
                
            }

            private void display() {
                gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight()); 
                gc.drawImage(frameBuffer.remove(0),0,0);
            }

            private void updateInfo() {
                coordInfo.setText("("+ppI.coord.x+","+ppI.coord.y+")");
            }
        };
        aTimer.start();
        
        primaryStage.show();
    }


    public void resumeSim(){
        cont= true;
    }
    
    public void pauseSim(){
        cont = false;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    
}

