/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

/**
 *
 * @author Ryan
 */
public class MoistureMap extends Thread{
    
    double[][] moistureMap;
    HeightMap heightMap;
    double waterLevel;
    long seed;
    double zoomX,zoomY;
    int steps = 50;
    
    MoistureMap(long s,HeightMap height, double w, double x, double y){
        seed = s;
        heightMap = height;
        zoomX = x/10;
        zoomY = y/10;
        waterLevel = w;
        moistureMap = new double[heightMap.heightMap.length][heightMap.heightMap[0].length];
    } 
    @Override
    public void run(){
        double e;
        double temp;
        OpenSimplexNoise noise = new OpenSimplexNoise(seed+1);
        for(int x=0;x<moistureMap.length;x++){
            for(int y=0;y<moistureMap[0].length;y++){
                e = ((((1*Math.abs(noise.eval(2*(x/zoomX/10),2*(y/zoomY/8))))+(0.5*Math.abs(noise.eval(3*(x/zoomX),3*(y/zoomY))))+(0.25*Math.abs(noise.eval(5*(x/zoomX/.5),5*(y/zoomY)))))+0.25)/2);
                if(e<0){
                    e=0;
                }
                if(heightMap.heightMap[x][y]<=waterLevel){
                    e+=0.2;
                    if(e>1){
                        e=1;
                    }
                }
                moistureMap[x][y]=Math.pow(e,2);
                
            }
        }
        simulate();
    }

    private void simulate() {
        double totalNMoist;
        double avgMoist;
        int count;
        double e;
        for(int i=0;i<steps;i++){
            for(int x=0;x<moistureMap.length;x++){
                for(int y=0;y<moistureMap[0].length;y++){
                    totalNMoist = 0;
                    count = 0;
                    if((x-1)>=0&&(y-1)>=0){//Check upper left
                        totalNMoist =totalNMoist+moistureMap[x-1][y-1];
                        count =count+1;
                    }
                    if((x-1)>=0){//Check left
                        totalNMoist =totalNMoist+moistureMap[x-1][y];
                        count =count+1;
                    }
                    if((x-1)>=0&&(y+1)<moistureMap[0].length){//Check lower left
                        totalNMoist =totalNMoist+moistureMap[x-1][y+1];
                        count =count+1;
                    }
                    if((y-1)>=0){//Check upper
                        totalNMoist =totalNMoist+moistureMap[x][y-1];
                        count =count+1;
                    }
                    if((y+1)<moistureMap[0].length){//Check down
                        totalNMoist =totalNMoist+moistureMap[x][y+1];
                        count =count+1;
                    }
                    if((x+1)<moistureMap.length&&(y-1)>=0){//Check upper right
                        totalNMoist =totalNMoist+moistureMap[x+1][y-1];
                        count =count+1;
                    }
                    if((x+1)<moistureMap.length){//Check right
                        totalNMoist =totalNMoist+moistureMap[x+1][y];
                        count =count+1;
                    }
                    if((x+1)<moistureMap.length&&(y+1)<moistureMap[0].length){//Check lower right
                        totalNMoist =totalNMoist+moistureMap[x+1][y];
                        count =count+1;
                    }

                    avgMoist = totalNMoist/count;
                    e=0;

                    if(((moistureMap[x][y]+avgMoist)/2)+e>1){
                        e=1;
                    }else if(((moistureMap[x][y]+avgMoist)/2)+e<0){
                        e=0;
                    }else{
                        e=((moistureMap[x][y]+avgMoist)/2)+e;
                    }
                    moistureMap[x][y]= e;
                }
            }
        }
    }
}
