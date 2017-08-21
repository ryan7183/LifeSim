/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import java.util.ArrayList;
import java.util.ListIterator;
import javafx.scene.paint.Color;

/**
 *
 * @author Ryan
 */
public class MapPoint {
    int x;
    int y;
    double height;
    double moisture;
    double heat;
    double pressure;
    long nutrients;
    ArrayList<Population> populations;
    Color color;
    
    MapPoint(int xPos,int yPos){
        x=xPos;
        y=yPos;
        height=0;
        nutrients = 100000L;//10000000
        populations = new ArrayList<Population>();
    }
    
    public void addPop(Population p){
        Boolean found = false;
        for(Population c :populations){
            if(c.id==p.id){
                c.popSize+=p.popSize;
                found = true;
            }
        }
        if(!found){
            populations.add(p);
        }
    }
    public void removePop(int i){
        populations.remove(i);
    }
    public int vegetationAmount(){
        int amount=0;
        if(populations.size()>0){
            for(Population p :populations){
                if(!p.landWaterPlant.isEmpty()&&p.landWaterPlant.equals("Plant")){
                    amount +=p.popSize;
                }
            }
        }
        return amount;
    }

    void growPop() {
        
        long unusedFood;
        long numOfPops = getNumOfPops("Nutrients");
        if(numOfPops>0){
            long foodForVegetation = (long)(nutrients/numOfPops);
            unusedFood = growVegetation(foodForVegetation);
            nutrients = nutrients-(nutrients-unusedFood);
        }
        if(getNumOfPops("Vegetation")>0){
            long foodForHerbivour = vegetationAmount()/getNumOfPops("Vegetation"); 
            unusedFood = growHerbivour(foodForHerbivour);
            killVegetation(getNumOfPops("Nutrients")-unusedFood);

        }
        if(getNumOfPops("Meat")>0){
            long foodForCarnivour = getNumOfPops("Vegetation")/getNumOfPops("Meat");
            unusedFood = growCarnivour(foodForCarnivour);
            killHerbivour(getNumOfPops("Vegetation")-unusedFood);
        }
        
        
    }

    private long getNumOfPops(String food) {
        long count =0;
        for(Population p:populations){
            if(p.food.contains(food)){
                count++;
            }
        }
        return count;
    }

    private long growVegetation(long foodForVegetation) {
        long unusedFood=0;
        long oldPop;
        long newPop;
        Population p;
        int i =0;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(p.food.contains("Nutrients")){
                oldPop = p.popSize;
                nutrients += p.nutrientsToReturn;
                unusedFood += p.feed(foodForVegetation);
                p.nutrientsToReturn = (foodForVegetation-unusedFood);
                newPop = p.popSize;
                if(oldPop>newPop){
                    nutrients += (oldPop-newPop)*p.foodConsumption;
                }
            }
            i++;
        }
        i=0;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(p.popSize <= 0){
                populations.remove(i);
                i=0;
            }else{  
                i++;
            }
        }
        return unusedFood;
    }

    private long growHerbivour(long foodForHerbivour) {
        long unusedFood=0;
        long oldPop;
        long newPop;
        Population p;
        int i =0;
        while(i<populations.size()&&populations.size()!=0){
            
            p = populations.get(i);
            if(p.food.contains("Vegetation")){
                oldPop = p.popSize;
                nutrients += p.nutrientsToReturn;
                unusedFood += p.feed(foodForHerbivour);
                p.nutrientsToReturn = (foodForHerbivour-unusedFood);
                newPop = p.popSize;
                if(oldPop>newPop){
                    nutrients += (oldPop-newPop)*p.foodConsumption;
                }
            }
            i++;
        }
        i=0;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(p.popSize <= 0){
                populations.remove(i);
                i=0;
            }else{  
                i++;
            }
        }
        return unusedFood;
    }

    private void killVegetation(long consumed) {  
        if(!populations.isEmpty()&&(float)vegetationAmount()>0){
            float percentConsumed = (float)consumed/(float)vegetationAmount();
            for(Population p:populations){
                if(p.food.contains("Nutrients")){
                    p.popSize = p.popSize-(long)(p.popSize*percentConsumed);
                }
            }
        }
    }

    private long growCarnivour(long foodForCarnivour) {
        long unusedFood=0;
        for(Population p:populations){
            if(p.food.contains("Meat")){
                unusedFood += p.feed(foodForCarnivour);
                
            }
            
        }
        return unusedFood;
    }

    private void killHerbivour(long consumed) {
        double percentConsumed = consumed/getNumOfPops("Nutrients");
        for(Population p:populations){
            if(p.food.contains("Vegetation")){
                p.popSize = p.popSize-(long)(p.popSize*percentConsumed);
            }
        }
    }
    
    public Boolean hasPop(long id){
        Boolean result=false;
        for(Population p:populations){
            if(p.id ==id){
                result = true;
            }
        }
        return result;
    }

    public long getPopSize(long id) {
        for(Population p:populations){
            if(p.id ==id){
                return p.popSize;
            }
        }
        return -1;
    }
    public void setPopSize(long id, long newAmount) {
        for(Population p:populations){
            if(p.id ==id){
                p.popSize = newAmount;
            }
        }
    }

    void checkConditions() {
        int i =0;
        Population p;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(!surviveConditions(p)){
                    nutrients += p.popSize*p.foodConsumption;
                    populations.remove(i);
                    i=0;
                }else{
                    i++;
                }
        }
    }
    
    public boolean surviveConditions(Population p){
        boolean survived = true;
        
        //PreferredHeat
        if(p.preferredHeat<(heat-(p.tolerance/10))||p.preferredHeat>(heat+(p.tolerance/10))){
            survived = false;
        }
        //PrefferedMositure 
        if(p.preferredMoisture<(moisture-(p.tolerance/10))||p.preferredMoisture>(moisture+(p.tolerance/10))){
            survived = false;
        }
        //Has food
        if(p.food.contains("Land")&&vegetationAmount()<=0){
            survived = false;
        }
        
        if(p.landWaterPlant.equals("Plant")&&nutrients<=0){
            survived = false;
        }
        
        if(p.popSize <= 0){
            survived = false;
        }
        return survived;
    }
    
    public void killLowPop(){
        int i = 0;
        Population p;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(p.popSize<10){
                    nutrients += p.popSize;
                    populations.remove(i);
                    i=0;
                }else{
                    i++;
                }
        }
    }

    public int landAmount() {
        int amount=0;
        if(populations.size()>0){
            for(Population p :populations){
                if(!p.landWaterPlant.isEmpty()&&p.landWaterPlant.equals("Land")){
                    amount +=p.popSize;
                }
            }
        }
        return amount;
    }

    void killZeroPop() {
        int i = 0;
        Population p;
        while(i<populations.size()&&populations.size()!=0){
            p = populations.get(i);
            if(p.popSize<=0){
                    nutrients += p.popSize;
                    populations.remove(i);
                    i=0;
                }else{
                    i++;
                }
        }
    }
}
