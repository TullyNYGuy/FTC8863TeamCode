package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;

import java.lang.Math;

public class FiringSolution {

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
    private final double GRAVITY = 9.81;
    private final double MAX_VELOCITY = 6000;
    private final double MIN_VELOCITY = 1000;
    private double velocity = MAX_VELOCITY;
    private boolean solutionFail = false;
    private double angle = 45;

    public class FiringSolutionValues {
        private double shooterAngle = 0;
        private double velocity = 0;

        public double getShooterAngle() {
            return shooterAngle;
        }

        public double getVelocity() {
            return velocity;
        }

        public FiringSolutionValues(double velocity, double shooterAngle) {
            this.shooterAngle = shooterAngle;
            this.velocity = velocity;
        }
    }

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
public FiringSolution(){

}
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public double getDistanceToGoal() {
        return 0;
    }

    public double getHeightOfGoal() {
        return 0;
    }

    public double calculateHorizontalVelocity(double velocity, double angleInRadians) {
        return Math.cos(angleInRadians) * velocity;
    }

    public double calculateVerticalVelocity(double velocity, double angleInRadians) {
        return Math.sin(angleInRadians) * velocity;
    }

    public double calculateShooterHeight(double angleInRadians, double shooterLength) {
        return Math.sin(angleInRadians) * shooterLength;
    }

    /**
     * Calculates the angle of the shooter needed to hit the goal.
     *
     * @param shooterHeightAddedByAngle   The height added by the angle of the shooter(m).
     * @param distanceFromGoal            The distance from the shooter to the base of the goal(m).
     * @param goalHeight                  The height above the ground of the goal(m).
     * @param velocity                    The speed of the ring leaving the shooter(m/s).
     * @param shooterHeightParallelGround The height of the shooter when it is parallel to the ground(m).
     * @return The angle of the shooter in radians. Returns -1 if no valid angle
     */
    private double calculateShooterAngle2(double shooterHeightAddedByAngle, double distanceFromGoal, double goalHeight, double velocity, double shooterHeightParallelGround) {
        double discriminant = (Math.pow(velocity, 4) - (Math.pow(GRAVITY, 2) * Math.pow(distanceFromGoal, 2))
                - 2 * (GRAVITY * (goalHeight - shooterHeightParallelGround - shooterHeightAddedByAngle) * (Math.pow(velocity, 2))));
        if (discriminant <= 0) {
            return -1;
        } else {
            //double positiveSolution = ((Math.pow(velocity, 2)+Math.sqrt(discriminant))/(GRAVITY*distanceFromGoal));
            double negativeSolution = ((Math.pow(velocity, 2) - Math.sqrt(discriminant)) / (GRAVITY * distanceFromGoal));
            return Math.atan(negativeSolution);
        }

    }

    /**
     * Calculates the angle of the shooter needed to hit the goal.
     *
     * @param distanceFromGoal            The distance from the shooter to the base of the goal(m).
     * @param goalHeight                  The height above the ground of the goal(m).
     * @param velocity                    The speed of the ring leaving the shooter(m/s).
     * @param shooterHeightParallelGround The height of the shooter when it is parallel to the ground(m).
     * @return The angle of the shooter in radians. Returns -1 if no valid angle
     */
    public double calculateShooterAngle(double distanceFromGoal, double goalHeight, double velocity, double shooterHeightParallelGround, double shooterLength) {
        double firstCalculation = calculateShooterAngle2(0, distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
        if (firstCalculation <= 0) {
            return -1;
        } else {
            double secondCalculation = calculateShooterAngle2(calculateShooterHeight(firstCalculation, shooterLength), distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
            if (secondCalculation <= 0) {
                return -1;
            } else {
                double thirdCalculation = calculateShooterAngle2(calculateShooterHeight(secondCalculation, shooterLength), distanceFromGoal, goalHeight, velocity, shooterHeightParallelGround);
                if (thirdCalculation <= 0) {
                    return -1;
                } else {
                    return thirdCalculation;
                }
            }
        }
    }

    public double calculateMaxRange(double velocity, double shooterAngle, double shooterLength) {
        return (velocity * Math.cos(shooterAngle)) / GRAVITY * (velocity * Math.sin(shooterAngle) +
                Math.sqrt(Math.pow(velocity, 2) * Math.pow(Math.sin(shooterAngle), 2) + 2 * GRAVITY * calculateShooterHeight(shooterAngle, shooterLength)));
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
//Assume that there is a max velocity of the ring


    //Get the distance to the goal
    public FiringSolutionValues getFiringSolution(double distance, double heightOfGoal, double velocity, double shooterHeightParallelGround, double shooterLength) {

        FiringSolutionValues result = null;
        while (!solutionFail && velocity > MIN_VELOCITY) {
            //Calculate an angle
            angle = calculateShooterAngle(distance, heightOfGoal, velocity, shooterHeightParallelGround, shooterLength);
            //Check if the angle is valid, if not then tell the user to move the robot
            if (angle <= -1) {
                result = new FiringSolutionValues(0, 0);
                solutionFail = true;
            } else {
                //Check if the angle exceeds the range limit
                //If it doesn't exceed the range limit then return the solutions
                if (calculateMaxRange(velocity, angle, shooterLength) <= 16) {
                    result = new FiringSolutionValues(velocity, angle);
                } else {
                    //If it does exceed then reiterate by changing the velocity
                    velocity = velocity - 250;
                }
            }


        }

        return result;
    }
}
