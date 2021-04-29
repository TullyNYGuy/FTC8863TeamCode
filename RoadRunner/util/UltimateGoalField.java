package org.firstinspires.ftc.teamcode.RoadRunner.util;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

/**
 * Class to implement a joystick with scaling and deadband compensation
 */
public class UltimateGoalField {

    // Constants - should probably be made into private fields to make it more generic
    final static Vector2d TOWER_GOAL = new Vector2d(72.25, -12.39);
    final static double towerGoalHighHeight = 35.26;
    final static double getTowerGoalMidHeight = 26.5;
    final static double getTowerGoalLowHeight = 16.5;

    //*********************************************************************************************
    // private data fields that can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //*********************************************************************************************


    //*********************************************************************************************
    //           CONSTRUCTORS
    //*********************************************************************************************
public UltimateGoalField(){

}

    //*********************************************************************************************
    //          UTILITY METHODS
    //*********************************************************************************************
    public double angleTo(Vector2d vector1,Vector2d vector2){
        Vector2d vectorDif =  vector1.minus(vector2);
        return Math.atan(vectorDif.getY()/ vectorDif.getX());
    }

    //*********************************************************************************************
    //          MAIN METHOD TO CALL
    //*********************************************************************************************
public double distanceToGoal(Pose2d robotPosition){
    Vector2d robotVector = new Vector2d(robotPosition.getX(),robotPosition.getY());
     return TOWER_GOAL.distTo(robotVector);
}
public double angleToGoal(Pose2d robotPosition){
    Vector2d robotVector = new Vector2d(robotPosition.getX(),robotPosition.getY());
    return  angleTo(TOWER_GOAL,robotVector);
}

    //*********************************************************************************************
    //          SCALING METHODS
    //*********************************************************************************************

}