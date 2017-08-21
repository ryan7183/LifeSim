/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import java.util.Random;

/**
 *
 * @author Ryan
 */
public class HeightMap extends Thread{
    
    double[][] heightMap;
    long seed;
    String landFormation;
    String generationType;
    int sizeX;//Map width
    int sizeY;//Map height
    double zoomX, xoomY;
    HeightMap(long s, int sX , int sY){
        seed = s;
        sizeX = sX;
        sizeY = sY;
    }
    
    @Override
    public void run(){ 
        double[][] values;
        int xPos;
        int yPos;
        OpenSimplexNoise noise;
        Random rand = new Random();

            noise  = new OpenSimplexNoise(seed);
            xPos=rand.nextInt(((sizeX-(sizeX/3))-(0))+1)+0;
            yPos=rand.nextInt(((sizeY-(sizeX/3))-(0))+1)+0;
            xPos =0;
            yPos=0;
            generateContinents(sizeY,sizeX,0,sizeY/(rand.nextInt(10-4+1)+4), noise);
            
    }

    private void generateContinents(int height, int width,int mOffSetX,int mOffSetY, OpenSimplexNoise noise) {
        double zoomX, zoomY;
        zoomX =width/10;
        zoomY = height/10;
        double d;
        double a=0.6;//Pushes everything up
        double b=.1;//pushes edges down
        double c=.375;//rate of drop off
        Random rand = new Random();
        int middleX=(width/2)-((rand.nextInt(3)-1)*mOffSetX);
        int middleY=(height/2)-((rand.nextInt(3)-1)*mOffSetY);
        
        heightMap = new double[width][height];
        double e;
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                e = (Math.abs(noise.eval(x/zoomX,y/zoomY))+Math.abs(.5*(noise.eval(2*x/zoomX,2*y/zoomY)))+Math.abs(.25*(noise.eval(3*x/zoomX,3*y/zoomY))))/1.75;
                
                if(e<0){
                    e=0;
                }
                e = Math.pow(e,.8);
                d=Math.sqrt(Math.pow(x-middleX,2)+Math.pow(y-middleY,2));
                e = e+a*1-b*Math.pow(d,c);
                
                if(e<0){
                    e=0;
                }else if(e>1){
                    e=1;
                }
                heightMap[x][y] = e;
            }
        }
        
    }
}
