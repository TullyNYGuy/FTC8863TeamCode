package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.control.PIDFController;

public class MotionProfileFollower {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private MotionProfile profile;

    public MotionProfile getProfile() {
        return profile;
    }

    public void setProfile(MotionProfile profile, String profileName) {
        this.profile = profile;
        this.profileName = profileName;
    }

    private PIDFController motionController;

    private String profileName = "";

    public String getProfileName() {
        return profileName;
    }

    private double measuredPosition = 0;

    public void setMeasuredPosition(double measuredPosition) {
        this.measuredPosition = measuredPosition;
    }

    private double correction = 0;

    public double getCorrection() {
        return correction;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MotionProfileFollower(com.acmerobotics.roadrunner.control.PIDFController motionController) {
        this.motionController = motionController;
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

    public void start() {
        // set the clock
    }

    public void update() {
        // todo code all this up
        // check to see if the position has been reached. If so, set the complete flag. If not:
        // get the time from the clock
        // get the target motion state from the profile, given the time
        // set the new target position in the PIDF controller
        // get the correction from the PIDF, given the current position
        // save the correction

    }

    public boolean isProfileComplete() {
        return true;
    }
}
