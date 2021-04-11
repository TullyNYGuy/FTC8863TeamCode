package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


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
public double getDistanceToGoal(){
    return 0;
}
    public double getHeightOfGoal(){
        return 0;
    }
    public double calculateAngle(){
        return 0;
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
//Assume that there is a max velocity of the ring
    public final double MAX_VELOCITY = 6000;
    public final double MIN_VELOCITY = 1000;
    public double velocity = MAX_VELOCITY;
    public boolean solutionFail = false;
public double angle = 45;
    //Get the distance to the goal
    public double solution() {
        double distance = getDistanceToGoal();
//Get the height of the target goal
        double heightOfGoal = getHeightOfGoal();

        while (!solutionFail && velocity > MIN_VELOCITY) {
            //Calculate an angle
            angle = calculateAngle();
            //Check if the angle is valid, if not then tell the user to move the robot
            if (angle <= -1){
                return 0;
            }
//Check if the angle exceeds the range limit
//If it doesn't exceed the range limit then return the solutions
//If it does exceed then reiterate by changing the velocity
        }
        return 0;
    }

}
