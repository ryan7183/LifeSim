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
class HeatMap extends Thread{
    int steps = 250;
    double waterLevel;
    double[][] heat;
    double[][] height;
    
    HeatMap(HeightMap hM,double w){
        height = hM.heightMap;
        waterLevel = w;
        heat = new double[hM.heightMap.length][hM.heightMap[0].length];
    }
    
    @Override
    public void run(){
      double distance;
      double temperature;
      double middleCoord = heat[0].length/2;
      double dWeight = 0.5;//Importance of height and weight for temperature
      double hWeight = 0.5;
      double h;
      for(int x=0;x<height.length;x++){
          for(int y=0;y<height[0].length;y++){
              distance =(middleCoord-Math.abs(middleCoord-y));
              h = height[x][y];
              if(h<=waterLevel){
                  h=waterLevel;
              }
              temperature= (((distance/middleCoord)*dWeight)+hWeight)-(h*hWeight);//Keeps the hieght just as important as distance from equator for temperature
              heat[x][y] = temperature;
          }
      }
      simulate();
    }

    private void simulate() {
        double totalNHeat;
        double avgHeat;
        int count;
        double e;
        for(int i=0;i<steps;i++){
            for(int x=0;x<heat.length;x++){
                for(int y=0;y<heat[0].length;y++){
                    totalNHeat = 0;
                    count = 0;
                    if((x-1)>=0&&(y-1)>=0){//Check upper left
                        totalNHeat =totalNHeat+heat[x-1][y-1];
                        count =count+1;
                    }
                    if((x-1)>=0){//Check left
                        totalNHeat =totalNHeat+heat[x-1][y];
                        count =count+1;
                    }
                    if((x-1)>=0&&(y+1)<heat[0].length){//Check lower left
                        totalNHeat =totalNHeat+heat[x-1][y+1];
                        count =count+1;
                    }
                    if((y-1)>=0){//Check upper
                        totalNHeat =totalNHeat+heat[x][y-1];
                        count =count+1;
                    }
                    if((y+1)<heat[0].length){//Check down
                        totalNHeat =totalNHeat+heat[x][y+1];
                        count =count+1;
                    }
                    if((x+1)<heat.length&&(y-1)>=0){//Check upper right
                        totalNHeat =totalNHeat+heat[x+1][y-1];
                        count =count+1;
                    }
                    if((x+1)<heat.length){//Check right
                        totalNHeat =totalNHeat+heat[x+1][y];
                        count =count+1;
                    }
                    if((x+1)<heat.length&&(y+1)<heat[0].length){//Check lower right
                        totalNHeat =totalNHeat+heat[x+1][y];
                        count =count+1;
                    }

                    avgHeat = totalNHeat/count;
                    e=0;
                    if(height[x][y]>=.6){
                        e=e-.0005;
                    }
                    if(y<heat[0].length*0.05||y>heat[0].length*0.95){
                        e=e-.0005;
                    }
                    if(((heat[x][y]+avgHeat)/2)+e>1){
                        e=1;
                    }else if(((heat[x][y]+avgHeat)/2)+e<0){
                        e=0;
                    }else{
                        e=((heat[x][y]+avgHeat)/2)+e;
                    }

                    heat[x][y]= e;
                }
            }
        }
        
    }
}
