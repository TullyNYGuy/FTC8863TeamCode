package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;

/**
 * General usage:
 *     Some other object creates a motion profile.
 *     It also creates this follower, passing in a PIDF controller.
 *     The object sets the motion profile using setProfile().
 *     The object starts the follower using start().
 *     The object starts looping:
 *        The object checks to see if the profile is complete using isProfileComplete().
 *        The object constantly updates the with the position feedback using update(feedback).
 *        The object gets the correction (new motor power) using getCorrection() and applies it to the
 *           motor.
 * Notes:
 *     The motor must be in
 */
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

    private NanoClock clock;
    private double startTime;
    private double elapsedTime;

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
        clock = NanoClock.system();
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
        startTime = clock.seconds();
    }

    public void update(double measuredPosition) {
        // todo how does it end?
        this.measuredPosition = measuredPosition;
        // check to see if the position has been reached. If so, set the complete flag. If not:
        // get the time from the clock
        elapsedTime = clock.seconds() - startTime;
        // get the target motion state from the profile, given the time
        MotionState targetState = profile.get(elapsedTime);
        // set the new target position in the PIDF controller
        motionController.setTargetPosition(targetState.getX());
        motionController.setTargetVelocity(targetState.getV());
        motionController.setTargetAcceleration(targetState.getA());
        // get the correction from the PIDF, given the current position
        correction = motionController.update(measuredPosition);
    }

    public boolean isProfileComplete() {

        // is profile timed out?
        // OR is the actual position close enough to the target position
        return true;
    }
}
