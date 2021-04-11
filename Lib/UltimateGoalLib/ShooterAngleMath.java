package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import java.lang.Math;

public class ShooterAngleMath {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
double gravity = 9.81;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public double calculateHorizontalVelocity(double velocity, double angleInRadians){
        return Math.cos(angleInRadians)*velocity;
    }
    public double calculateVerticalVelocity(double velocity, double angleInRadians){
        return Math.sin(angleInRadians)*velocity;
    }
    public double calculateShooterHeight(double angleInRadians, double shooterLength){
        return Math.sin(angleInRadians)*shooterLength;
    }

    /**
     * Calculates the angle of the shooter needed to hit the goal.
     * @param shooterHeightAddedByAngle The height added by the angle of the shooter(m).
     * @param distanceFromGoal The distance from the shooter to the base of the goal(m).
     * @param goalHeight The height above the ground of the goal(m).
     * @param velocity The speed of the ring leaving the shooter(m/s).
     * @param shooterHeightParallelGround The height of the shooter when it is parallel to the ground(m).
     * @return The angle of the shooter in radians. Returns -1 if no valid angle
     */
    private double calculateShooterAngle2(double shooterHeightAddedByAngle, double distanceFromGoal, double goalHeight, double velocity, double shooterHeightParallelGround){
         double discriminant = (Math.pow(velocity, 4)-(Math.pow(gravity, 2)*Math.pow(distanceFromGoal, 2))
                 -2*(gravity*(goalHeight-shooterHeightParallelGround-shooterHeightAddedByAngle)*(Math.pow(velocity, 2))));
         if (discriminant<=0){
             return -1;
         } else{
             //double positiveSolution = ((Math.pow(velocity, 2)+Math.sqrt(discriminant))/(gravity*distanceFromGoal));
             double negativeSolution = ((Math.pow(velocity, 2)-Math.sqrt(discriminant))/(gravity*distanceFromGoal));
             return Math.atan(negativeSolution);
         }

    }
    /**
     * Calculates the angle of the shooter needed to hit the goal.
     * @param distanceFromGoal The distance from the shooter to the base of the goal(m).
     * @param goalHeight The height above the ground of the goal(m).
     * @param velocity The speed of the ring leaving the shooter(m/s).
     * @param shooterHeightParallelGround The height of the shooter when it is parallel to the ground(m).
     * @return The angle of the shooter in radians. Returns -1 if no valid angle
     */
    public double calculateShooterAngle(double distanceFromGoal, double goalHeight, double velocity, double shooterHeightParallelGround, double shooterLength){
        double firstCalculation = calculateShooterAngle2(0, distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
        if (firstCalculation<=0){
            return -1;
        } else {
            double secondCalculation = calculateShooterAngle2(calculateShooterHeight(firstCalculation, shooterLength), distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
            if (firstCalculation <= 0) {
                return -1;
            } else {
                double thirdCalculation = calculateShooterAngle2(calculateShooterHeight(secondCalculation, shooterLength), distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
                if (firstCalculation <= 0) {
                    return -1;
                } else {
                    return thirdCalculation;
                }
            }
        }
    }
    public double calculateMaxRange(double velocity, double calculateShooterAngle){
        return (velocity*Math.cos(calculateShooterAngle))/gravity*()
    }

}
