/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 *
 * @author Ryan
 */
public class Population {
    int swimDistance;
    int flyDistance;
    double preferredHeat;
    double  preferredMoisture;
    ArrayList<String> food;//Which type of food it eats. Vegetation/Meat/Nutrients
    String landWaterPlant;//If it lives on land, water or is vegetation
    long minPopForSpread;
    double mutationChance;//Of the population
    double foodConsumption;//Per individual
    double popIncreaseFactor;//Between 0 and 1
    long popSize;
    double tolerance;//Between 0 and 1
    long id;
    Color color;
    Random rand = new Random();
    long nutrientsToReturn;
    
    Population(int sD,int fD, double pH, double pM, ArrayList<String> f, String lW, long mPFS,double m,double consumption,double pI,long pSize,double t, long i){
        swimDistance = sD;
        flyDistance = fD;
        preferredHeat = pH;
        preferredMoisture = pM;
        food = f;
        landWaterPlant = lW;
        minPopForSpread = mPFS;
        mutationChance = m;
        foodConsumption = consumption;
        popIncreaseFactor = pI;
        popSize = pSize;
        tolerance = t;
        id = i;
        assignColor(id);
        nutrientsToReturn =(long)(popSize*foodConsumption); 
    }
    
    Population(int sD,int fD, double pH, double pM, ArrayList<String> f, String lW, long mPFS,double m,double consumption,double pI,long pSize,double t, long i, Color c){
        iterateColor(id);
        swimDistance = sD;
        flyDistance = fD;
        preferredHeat = pH;
        preferredMoisture = pM;
        food = f;
        landWaterPlant = lW;
        minPopForSpread = mPFS;
        mutationChance = m;
        foodConsumption = consumption;
        popIncreaseFactor = pI;
        popSize = pSize;
        tolerance = t;
        id = i;
    }
    public void mutate(long newId){
        id = newId;
        if (landWaterPlant=="PLANT"){
            int traitToChange= rand.nextInt(8);
            switch (traitToChange){
                case 0:
                    mutatePreferredHeat();
                    break;
                case 1:
                    mutatePreferredMoisture();
                    break;
                case 2:
                    mutateMinPopForSread();
                    break;    
                case 3:
                    mutateMutationChance();
                    break;
                case 4:
                    mutateConsumption();
                    break;
                case 5:
                    popIncreaseFactor();
                    break;
                case 6:
                    mutateSwimDistance();
                    break;
                case 7:
                    mutateTolerance();
                    break;
                default:
                    break;
            }
        }else{
            int traitToChange= rand.nextInt(11);
            switch (traitToChange){
                case 0:
                    mutateSwimDistance();
                    break;
                case 1:
                    mutateFlyDistance();
                    break;
                case 2:
                    mutatePreferredHeat();
                    break;
                case 3:
                    mutatePreferredMoisture();
                    break;
                case 4:
                    mutateFood();
                    break;
                case 5:
                    mutateLandWater();
                    break;
                case 6:
                    mutateMinPopForSread();
                    break;
                case 7:
                    mutateMutationChance();
                    break;
                case 8:
                    mutateConsumption();
                    break;
                case 9:
                    popIncreaseFactor();
                    break;
                case 10:
                    mutateTolerance();
                    break;
                default:
                    break;
            } 
        }

    }

    private void mutateSwimDistance() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                swimDistance-=1;
                break;
            case 1:
                swimDistance+=1;
                break;
            default:
                break;
        }
        if(swimDistance <=0){
            swimDistance = 0;
        }
    }

    private void mutateFlyDistance() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                flyDistance-=1;
                break;
            case 1:
                flyDistance+=1;
                break;
            default:
                break;
        }
        if(flyDistance<=0){
            flyDistance = 0;
        }
    }

    private void mutatePreferredHeat() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                preferredHeat-=rand.nextDouble()/1;
                break;
            case 1:
                preferredHeat+=rand.nextDouble()/1;
                break;
            default:
                break;
        }
        if(preferredHeat<=0){
            preferredHeat = 0;
        }
    }

    private void mutatePreferredMoisture() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                preferredMoisture-=rand.nextDouble()/1;
                break;
            case 1:
                preferredMoisture+=rand.nextDouble()/1;
                break;
            default:
                break;
        }
        if(preferredMoisture<=0){
            preferredMoisture =0;
        }
    }

    private void mutateFood() {
        //Mutats type of food eaten. No omnivours for now

    }

    private void mutateLandWater() {
        //Mutates if the species lives on land or water
    }

    private void mutateMutationChance() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                mutationChance-=rand.nextDouble()/10000000;
                break;
            case 1:
                mutationChance+=rand.nextDouble()/10000000;
                break;
            default:
                break;
        }
        if(mutationChance<=0){
            mutationChance =0;
        }
    }

    private void mutateMinPopForSread() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                minPopForSpread-=rand.nextInt()*1000;
                break;
            case 1:
                minPopForSpread+=rand.nextInt()*1000;
                break;
            default:
                break;
        }
        if(minPopForSpread<=0){
            minPopForSpread =0;
        }
    }

    private void popIncreaseFactor() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                popIncreaseFactor-=rand.nextDouble()/1000;
                break;
            case 1:
                popIncreaseFactor+=rand.nextDouble()/1000;
                break;
            default:
                break;
        }
        if(popIncreaseFactor<=0){
            popIncreaseFactor =0;
        }else if(popIncreaseFactor>=0){
            popIncreaseFactor =1;
        }
    }

    private void mutateConsumption() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                foodConsumption-=rand.nextDouble()*2;
                break;
            case 1:
                foodConsumption+=rand.nextDouble()*2;
                break;
            default:
                break;
        }
        if(foodConsumption<=0.1){
            foodConsumption=0.1;
        }
        if(foodConsumption >= 1000){
            foodConsumption = 1000;
        }
    }
    
    private void mutateTolerance() {
        int choice = rand.nextInt(2);
        switch(choice){
            case 0:
                tolerance-=rand.nextDouble()/5;
                break;
            case 1:
                tolerance+=rand.nextDouble()/5;
                break;
            default:
                break;
        }
        if(tolerance<=0){
            tolerance=0;
        }else if(tolerance >=1){
            tolerance = 1;
        }
    }
    
    private long grow(long unusedFood){
        long popInc = (long)(popSize*popIncreaseFactor);
        long foodConsumed = (long)(popInc*foodConsumption);
        
        if (foodConsumed>unusedFood){
            popInc = (long)(unusedFood/foodConsumption);
            popSize += popInc;
            return unusedFood -foodConsumed;
        }else{
            popSize+=popInc;
            return (unusedFood -foodConsumed);
        }
    }
    
    public long feed(long food){
        
        long unusedFood;
        long requiredFood = (long)(popSize*foodConsumption);
        unusedFood = food - requiredFood;
        if(food ==0){
            popSize = 0;
            return food;
        }else if (unusedFood<0){
            popSize += (unusedFood/foodConsumption);//Kill off a portion of the population when over populated 
            if(popSize<0){
                popSize = 0;
            }
            return 0;
        }else{
            return grow(unusedFood);
        }
    }
    
    public Population clone(){
        Population newPop = new Population(swimDistance,flyDistance,preferredHeat,preferredMoisture,food,landWaterPlant,minPopForSpread,mutationChance,foodConsumption,popIncreaseFactor,popSize,tolerance,id);
        return newPop;
    }

    private void assignColor(long id) {
        switch((int)id%3){
            case 0:
                color = Color.rgb(((int)id%255),100,100);
                break;
            case 1:
                color = Color.rgb(100,((int)id%255),100);
                break;
            case 2:
                color = Color.rgb(100,100,((int)id%255));
                break;
            default:
                color = Color.rgb(100,100,100);
                break;
        }
    }

    private void iterateColor(long id) {
        int red;
        int blue;
        int green;
        switch((int)id%3){
            case 0:
                red = (int)color.getRed()+1;
                green = (int)color.getGreen();
                blue = (int)color.getBlue();
                if(red>255){
                    red = 0;
                }
                color= Color.rgb(red,green,blue);
                break;
            case 1:
                red = (int)color.getRed();
                green = (int)color.getGreen()+1;
                blue = (int)color.getBlue();
                if(green>255){
                    green = 0;
                }
                color= Color.rgb(red,green,blue);
                break;
            case 2:
                red = (int)color.getRed();
                green = (int)color.getGreen();
                blue = (int)color.getBlue()+1;
                if(blue>255){
                    blue = 0;
                }
                color= Color.rgb(red,green,blue);
                break;
            default:
                color = Color.rgb(0,0,0);
                break;
        }
    }

    
}
