/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package populationsim;

import javafx.scene.paint.Color;

/**
 *
 * @author Ryan
 */
public class PointPopInfo {
    Coordinate coord;
    double height;
    double heat;
    double moisture;
    Long nutrients;
    long id;
    Color color;
    long popSize;
    int swimDistance;
    int flyDistance;
    double preferredHeat;
    double preferredMositure;
    String food;
    double popincreaseFactor;
    double tolerance;
    long idCount;
    
    PointPopInfo(){
        coord = new Coordinate();
    }
}
