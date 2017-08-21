/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author Ryan
 */
public class World {
    
    final double waterLevel = 0.08;
    final int numOfClimateSimSteps = 50;
    ArrayList<Image> frameBuffer;
    int imgWidth;
    int imgHeight;
    long seed;
    MapPoint[][] map;
    StringValue display;
    StringValue buttonInstruction;
    Coordinate mouseClickCoord;
    StringValue clickInstruction;
    Coordinate infoLocation;
    PointPopInfo ppI;
    long id;
    Random rand = new Random();
    
    World(ArrayList<Image> frames,int width, int height, long s,StringValue d,StringValue bI, Coordinate mCC, StringValue cI,PointPopInfo ppi) throws InterruptedException{
        
        id = 0;
        frameBuffer=frames;
        imgWidth = width;
        imgHeight = height;
        seed = s;
        display=d;
        buttonInstruction = bI;
        mouseClickCoord = mCC;
        clickInstruction = cI;
        infoLocation = new Coordinate(0,0);
        ppI = ppi;
        createWorld();
        simulate();
    }
    
    public void step(){
        
        stepPopSize();
    }

    private void simulate() {
        for(;;){
            step();
            makeFrame();
            System.out.println(id);
            checkButtonInstruction();
            checkClickInstruction();
        }
    }

    private void createWorld() throws InterruptedException {
        initializeMap();
        HeightMap height= new HeightMap(seed,imgWidth,imgHeight);
        height.start();
        height.join();
        MoistureMap moisture = new MoistureMap(seed+1, height, waterLevel, imgWidth, imgHeight);
        moisture.start();
        HeatMap heat = new HeatMap(height,waterLevel);
        heat.start();
        moisture.join();
        heat.join();
        applyMaps(height, moisture, heat);
        spawnVegetation();
        spawnHerbivour();
    }

    private void stepPopSize() {
        //Go to each map point and feed each pop
        spreadPop();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y].growPop();
            }
        }
        killZeroPop();
        
    }

    private void spreadPop() {
        //Look at min pop to spread then spread if condition is met
        boolean found;
        Population aPop;
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y].checkConditions();
                for(Population p :map[x][y].populations){
                    if(p.popSize>=p.minPopForSpread){
                        Boolean moved = false;
                        long aTM = p.popSize-p.minPopForSpread;
                        while(!moved){
                            int tile = rand.nextInt(4);
                            
                            switch(tile){
                                case 0:{
                                    //left
                                    if(x-1>=0){
                                        found = false;
                                        for(Population aP:map[x-1][y].populations){
                                            if(aP.id == p.id){
                                                found = true;
                                                aP.popSize = aP.popSize+aTM;
                                                moved = true;
                                            }
                                        }
                                        
                                        if(!found){
                                            if(map[x-1][y].height<=waterLevel){
                                                p.popSize=p.popSize+aTM;
                                            }else{
                                                aPop = p.clone();
                                                if(rand.nextDouble()<=aPop.mutationChance){
                                                    aPop.mutate(id);
                                                    id++;
                                                }
                                                map[x-1][y].populations.add(aPop);
                                                moved = true;
                                            }
                                        }
                                        
                                    }else{
                                        moved = false;
                                    }
                                    break;
                                        
                                }
                                case 1:{
                                    //up
                                    if(y-1>=0){
                                        found = false;
                                        for(Population aP:map[x][y-1].populations){
                                            if(aP.id == p.id){
                                                found = true;
                                                aP.popSize = aP.popSize+aTM;
                                                moved = true;
                                            }
                                        }
                                        if(!found){
                                            if(map[x][y-1].height<=waterLevel){
                                                p.popSize=p.popSize+aTM;
                                            }else{
                                                aPop = p.clone();
                                                if(rand.nextDouble()<=aPop.mutationChance){
                                                    aPop.mutate(id);
                                                    id++;
                                                }
                                                map[x][y-1].populations.add(aPop);
                                                moved = true;
                                            }
                                        }
                                        
                                    }else{
                                        moved = false;
                                    }
                                    break;
                                        
                                }
                                case 2:{
                                    //right
                                    if(x+1<imgWidth){
                                        found = false;
                                        for(Population aP:map[x+1][y].populations){
                                            if(aP.id == p.id){
                                                found = true;
                                                aP.popSize = aP.popSize+aTM;
                                                moved = true;
                                            }
                                        }
                                        if(!found){
                                            if(map[x+1][y].height<=waterLevel){
                                                p.popSize=p.popSize+aTM;
                                            }else{
                                                aPop = p.clone();
                                                if(rand.nextDouble()<=aPop.mutationChance){
                                                    aPop.mutate(id);
                                                    id++;
                                                }
                                                map[x+1][y].populations.add(aPop);
                                                moved = true;
                                            }
                                        }
                                        
                                    }else{
                                        moved = false;
                                    }
                                    break;
                                        
                                }
                                case 3:{
                                    //down
                                    if(y+1<imgHeight){
                                        found = false;
                                        for(Population aP:map[x][y+1].populations){
                                            if(aP.id == p.id){
                                                found = true;
                                                aP.popSize = aP.popSize+aTM;
                                                moved = true;
                                            }
                                        }
                                        if(!found){
                                            if(map[x][y+1].height<=waterLevel){
                                                p.popSize=p.popSize+aTM;
                                            }else{
                                                aPop = p.clone();
                                                if(rand.nextDouble()<=aPop.mutationChance){
                                                    aPop.mutate(id);
                                                    id++;
                                                }
                                                map[x][y+1].populations.add(aPop);
                                                moved = true;
                                            }
                                        }
                                        
                                    }else{
                                        moved = false;
                                    }
                                    break;
                                        
                                }
                                default:
                                    
                                    moved = false;
                                    break;
                            }
                        }
                    }
                }
            }
            
        }
    }

    private void applyMaps(HeightMap height, MoistureMap moisture, HeatMap heat) {
        applyHeightMap(height);
        applyMoistureMap(moisture);
        applyHeatMap(heat);
    }

    private void applyHeightMap(HeightMap height) {
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y].height = height.heightMap[x][y];
            }
        }
    }

    private void applyMoistureMap(MoistureMap moist) {
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y].moisture = moist.moistureMap[x][y];
            }
        }
    }

    private void applyHeatMap(HeatMap heat) {
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y].heat = heat.heat[x][y];
            }
        }
    }

    private void initializeMap() {
        map = new MapPoint[imgWidth][imgHeight];
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y] = new MapPoint(x,y);
            }
        }
    }
    
    private void makeFrame(){
        Image frame;
        if(frameBuffer.size()<1){
                       
            switch (display.string){
                case "Height": 
                    frameBuffer.add(getHeightMapFrame());
                    break;
                case "Moisture":
                    frameBuffer.add(getMoistureMapFrame());
                    break;
                case "Heat":
                    frameBuffer.add(getHeatMapFrame());
                    break;
                case "Terrain":
                    frameBuffer.add(getTerrainFrame());
                    break;
                case "Nutrients":
                       frameBuffer.add(getNutrientsFrame());
                    break;
                case "FirstIn":
                    frameBuffer.add(getFirstInFrame());
                    break;
                case "FirstInVeg":
                    frameBuffer.add(getFirstInVegFrame());
                    break;
                case "FirstInLand":
                    frameBuffer.add(getFirstInLandFrame());
                    break;
                default:
                    break;
            } 
       }else{
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    private Image getHeightMapFrame() {
        Color c;
        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                c = Color.rgb((int) (map[x][y].height*255),(int) (map[x][y].height*255),(int) (map[x][y].height*255));
                pw.setColor(x,y,c);
            }
        }
        
        return frame;
    }

    private Image getMoistureMapFrame() {
        Color c;
        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                c = Color.rgb((int)(255-(map[x][y].moisture*255)),(int) (255-(map[x][y].moisture*255)),255);
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }

    private Image getHeatMapFrame() {
        Color c;
        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                if((map[x][y].heat*7)<=(1)){
                    c = Color.rgb(255, 0, 255);
                }else if(map[x][y].heat*7<=(2)){
                    c = Color.rgb(0, 0, 255);
                }else if(map[x][y].heat*7<=(3)){
                    c= Color.rgb(0, 255, 255);
                }else if(map[x][y].heat*7<=(4)){
                    c = Color.rgb(0,255,0);
                }else if(map[x][y].heat*7<=(5)){
                    c = Color.rgb(255,255,0);
                }else if(map[x][y].heat*7<=(6)){
                    c = Color.rgb(255,150,0);
                }else{
                    c=Color.rgb(255,0,0);
                }
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }

    private Image getTerrainFrame() {
        Color c;
        int s=0;//Shading

        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){  
                if(x!=0){
                    if(map[x][y].height<map[x-1][y].height){
                        s = 30;
                    }else{
                        s=0;
                    }
                }
                if(map[x][y].populations.size()>0&&map[x][y].vegetationAmount()>0){
                    c=Color.rgb(89-s,165-s,92-s);
                }else if(map[x][y].height<=waterLevel){
                    if((map[x][y].heat)>.5){
                        c = Color.rgb(0,0,255);//Water
                    }else{
                        c = Color.rgb(255, 255, 255);//Glacier
                    }
                }else{
                    if(map[x][y].heat<=.55 && map[x][y].moisture>=.075){
                        c=Color.rgb(255-s,255-s,255-s);//Snow
                    }else if(map[x][y].heat<=.55){
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }else if(map[x][y].heat>.55){
                        if(map[x][y].moisture>.1){
                            c=Color.rgb(99-s, 73-s, 30-s);//Dirt
                        }else{
                            c=Color.rgb(224-s,207-s,96-s);//Sand
                        }
                    }else{
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }
                }
                
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }
    
    private Image getFirstInFrame() {
        Color c;
        int s=0;//Shading

        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){  
                if(x!=0){
                    if(map[x][y].height<map[x-1][y].height){
                        s = 30;
                    }else{
                        s=0;
                    }
                }
                if(!map[x][y].populations.isEmpty() && map[x][y].populations.get(0).popSize>0){
                    c=map[x][y].populations.get(0).color;
                }else if(map[x][y].height<=waterLevel){
                    if((map[x][y].heat)>.5){
                        c = Color.rgb(0,0,255);//Water
                    }else{
                        c = Color.rgb(255, 255, 255);//Glacier
                    }
                }else{
                    if(map[x][y].heat<=.55 && map[x][y].moisture>=.075){
                        c=Color.rgb(255-s,255-s,255-s);//Snow
                    }else if(map[x][y].heat<=.55){
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }else if(map[x][y].heat>.55){
                        if(map[x][y].moisture>.1){
                            c=Color.rgb(99-s, 73-s, 30-s);//Dirt
                        }else{
                            c=Color.rgb(224-s,207-s,96-s);//Sand
                        }
                    }else{
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }
                }
                
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }
    
    private Image getFirstInVegFrame() {
        Color c;
        int s=0;//Shading

        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){  
                if(x!=0){
                    if(map[x][y].height<map[x-1][y].height){
                        s = 30;
                    }else{
                        s=0;
                    }
                }
                if(map[x][y].populations.size()>0 &&map[x][y].vegetationAmount()>0){
                    int i =0;
                    for(Population p : map[x][y].populations){
                        if(p.landWaterPlant.equals("Plant")){
                            break;
                        }else{
                            i++;
                        }
                    }
                    c=map[x][y].populations.get(i).color;
                }else if(map[x][y].height<=waterLevel){
                    if((map[x][y].heat)>.5){
                        c = Color.rgb(0,0,255);//Water
                    }else{
                        c = Color.rgb(255, 255, 255);//Glacier
                    }
                }else{
                    if(map[x][y].heat<=.55 && map[x][y].moisture>=.075){
                        c=Color.rgb(255-s,255-s,255-s);//Snow
                    }else if(map[x][y].heat<=.55){
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }else if(map[x][y].heat>.55){
                        if(map[x][y].moisture>.1){
                            c=Color.rgb(99-s, 73-s, 30-s);//Dirt
                        }else{
                            c=Color.rgb(224-s,207-s,96-s);//Sand
                        }
                    }else{
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }
                }
                
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }
    
    private Image getFirstInLandFrame() {
        Color c;
        int s=0;//Shading

        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){  
                if(x!=0){
                    if(map[x][y].height<map[x-1][y].height){
                        s = 30;
                    }else{
                        s=0;
                    }
                }
                if(map[x][y].populations.size()>0 &&map[x][y].landAmount()>0){
                    int i =0;
                    for(Population p : map[x][y].populations){
                        if(p.landWaterPlant.equals("Land")){
                            break;
                        }else{
                            i++;
                        }
                    }
                    c=map[x][y].populations.get(i).color;
                }else if(map[x][y].height<=waterLevel){
                    if((map[x][y].heat)>.5){
                        c = Color.rgb(0,0,255);//Water
                    }else{
                        c = Color.rgb(255, 255, 255);//Glacier
                    }
                }else{
                    if(map[x][y].heat<=.55 && map[x][y].moisture>=.075){
                        c=Color.rgb(255-s,255-s,255-s);//Snow
                    }else if(map[x][y].heat<=.55){
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }else if(map[x][y].heat>.55){
                        if(map[x][y].moisture>.1){
                            c=Color.rgb(99-s, 73-s, 30-s);//Dirt
                        }else{
                            c=Color.rgb(224-s,207-s,96-s);//Sand
                        }
                    }else{
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }
                }
                
                pw.setColor(x,y,c);
            }
        }
        return frame;
    }

    private void spawnVegetation() {
        int vegMutateChance = 1000000;//Set back to 100000
        int initialSpawnAmount = 50;
        int xPos;
        int yPos;
        Boolean repeat;
        for(int q =0;q<initialSpawnAmount;q++){
            repeat=true;
            while(repeat){
                xPos =(int)(rand.nextInt((int)(imgWidth)));
                yPos = rand.nextInt(imgHeight-(int)(imgHeight*.5))+(int)(imgHeight*.25);
                if(map[xPos][yPos].height>waterLevel){ 
                    //First pop in tile
                    ArrayList<String> food = new ArrayList<String>();
                    food.add("Nutrients");                    
                    Population pop = new Population(0,0,map[xPos][yPos].heat,map[xPos][yPos].moisture,food,"Plant",rand.nextInt(1000)+1,rand.nextDouble()/vegMutateChance,(rand.nextDouble()+0.1),rand.nextDouble(),100L,rand.nextDouble(),id);
                    id++;                  
                    map[xPos][yPos].addPop(pop); 
                    repeat = false;                  
                }else{
                    repeat = true;
                }
            }
        }
        for(int k=0;k<10;k++){
            step();
        }    
    }

    private void spawnHerbivour() {
        
        int herbiMutateChance = 300000;
        int initialSpawnAmount = 25;
        int xPos;
        int yPos;
        Boolean repeat;
        for(int q =0;q<initialSpawnAmount;q++){
            repeat=true;
            while(repeat){
                xPos =(int)(rand.nextInt((int)(imgWidth)));
                yPos = rand.nextInt(imgHeight-(int)(imgHeight*.5))+(int)(imgHeight*.25);
                if(map[xPos][yPos].populations.size()>0&&map[xPos][yPos].populations.get(0).landWaterPlant.equals("Plant")&&map[xPos][yPos].height>waterLevel){ 
                    ArrayList<String> food = new ArrayList<String>();
                    food.add("Vegetation");                    
                    Population pop = new Population(0,0,map[xPos][yPos].heat,map[xPos][yPos].moisture,food,"Land",rand.nextInt(1000)+1,rand.nextDouble()/herbiMutateChance,(rand.nextDouble()+0.00001),rand.nextDouble(),10L,rand.nextDouble(),id);
                    id++;                  
                    map[xPos][yPos].addPop(pop); 
                    repeat = false;                  
                }else{
                    repeat = true;
                }
            }
        }
        
        for(int k=0;k<5;k++){
            step();
        }
    }
    
    private Image getNutrientsFrame() {
        Color c;

        WritableImage frame = new WritableImage(imgWidth,imgHeight);
        PixelWriter pw = frame.getPixelWriter();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                if(map[x][y].nutrients>0){
                    c = Color.rgb(0,0,0);
                    
                }else{
                    c = Color.rgb(255,255,255);
                }
                pw.setColor(x,y,c);
            }
        }
        
        return frame;
    }
    
    

    private void checkButtonInstruction() {
        switch(buttonInstruction.string){
            case "Nothing":
                break;
            case "KillLowPop":
                    killLowPop();
                    buttonInstruction.string ="Nothing";
                    break;
            default:
                buttonInstruction.string ="Nothing";
                break;
        }
    }

    private void killLowPop() {
        for(int x=0;x<imgWidth;x++){
            for(int y=0;y<imgHeight;y++){
               map[x][y].killLowPop();
            }
        }
    }
    
    private void killZeroPop() {
        for(int x=0;x<imgWidth;x++){
            for(int y=0;y<imgHeight;y++){
               map[x][y].killZeroPop();
            }
        }
    }

    private void checkClickInstruction() {
        switch(clickInstruction.string){
            case "Canvas":
               updateInfo();
               clickInstruction.string = "Nothing";
               break;
            default:
                break;
        }
    }

    private void updateInfo() {
        int x=(int)mouseClickCoord.x;
        int y=(int)mouseClickCoord.y;
        MapPoint m= map[x][y];
        ppI.coord.x = mouseClickCoord.x;
        ppI.coord.y = mouseClickCoord.y;
        ppI.moisture = m.moisture;
        ppI.heat = m.heat;
        ppI.nutrients = m.nutrients;
        ppI.idCount = id;
        if(!m.populations.isEmpty()){
            ppI.id = m.populations.get(0).id;
            ppI.popSize = m.populations.get(0).popSize;
            ppI.swimDistance = m.populations.get(0).swimDistance;
            ppI.flyDistance = m.populations.get(0).flyDistance;
            ppI.preferredHeat = m.populations.get(0).preferredHeat;
            ppI.preferredMositure = m.populations.get(0).preferredMoisture;
        }
        
        
    }

    

    



}
