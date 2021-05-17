package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

public class Goals {

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
    public GoalProperties top;
    public GoalProperties powerShotLeft;
    public GoalProperties powerShotMid;
    public GoalProperties powerShotRight;
    public GoalProperties middle;
    public GoalProperties bottom;
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
    public Goals() {
        top = new GoalProperties(35.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
        powerShotLeft = new GoalProperties(30 * 0.0254, new Pose2d(71 * 0.0254, 20 * 0.0254, 0));
        powerShotMid = new GoalProperties(30 * 0.0254, new Pose2d(71 * 0.0254, 12 * 0.0254, 0));
        powerShotRight = new GoalProperties(30 * 0.0254, new Pose2d(71 * 0.0254, 4.25 * 0.0254, 0));
        middle = new GoalProperties(26 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
        bottom = new GoalProperties(16.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
    }
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

}
