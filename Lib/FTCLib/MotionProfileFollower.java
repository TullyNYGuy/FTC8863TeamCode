package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;

/**
 * General usage:
 *     Some other object creates a motion profile.
 *     It also creates this follower, passing in a PIDF controller.
 *     The object sets the motion profile in this follower using setProfile().
 *     The object starts this follower using start().
 *     The object starts looping:
 *        The object checks to see if the profile is complete using isProfileComplete().
 *        The object constantly updates this follower with the position feedback using update(feedback).
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

    private double completionTimeout = 0.25;

    public double getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(double completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    private double targetTolerance = 0.1; // units are same as whatever the profile units are

    public void setTargetTolerance(double targetTolerance) {
        this.targetTolerance = targetTolerance;
    }

    private boolean profileComplete = false;

    public boolean isProfileComplete() {
        return profileComplete;
    }

    private double targetPosition = 0;

    public double getTargetPosition() {
        return targetPosition;
    }

    private double targetVelocity = 0;

    public double getTargetVelocity() {
        return targetVelocity;
    }

    private double targetAcceleration = 0;

    public double getTargetAcceleration() {
        return targetAcceleration;
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

    private boolean isCloseEnoughToTarget(double measuredPosition, double targetPosition) {
        boolean result = false;
        if (Math.abs(targetPosition - measuredPosition) < targetTolerance) {
            result = true;
        }
        return result;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void start() {
        startTime = clock.seconds();
        profileComplete = false;
    }

    public void update(double measuredPosition) {
        this.measuredPosition = measuredPosition;
        // check to see if the position has been reached. If so, set the complete flag. If not:
        // get the time from the clock
        elapsedTime = clock.seconds() - startTime;
        // skip the complete check if the profile is already complete
        // Note that the profile will still run, holding position at the desired position. At
        // least until the thing using the profile stops it.
        if (!profileComplete) {
            // if the elapsed time is greater than the profile duration by at least the completionTimeout call the profile complete
            // OR if the measured position is close enough to the desired position call the profile complete
            if (elapsedTime > (profile.duration() + completionTimeout) || isCloseEnoughToTarget(measuredPosition, profile.end().getX())) {
                profileComplete = true;
            }
        }
        // get the target motion state from the profile, given the time
        MotionState targetState = profile.get(elapsedTime);

        // update the variables to an external class can call the getters for them
        targetPosition = targetState.getX();
        targetVelocity = targetState.getV();
        targetAcceleration = targetState.getA();

        // set the new target position in the PIDF controller
        motionController.setTargetPosition(targetPosition);
        motionController.setTargetVelocity(targetVelocity);
        motionController.setTargetAcceleration(targetAcceleration);

        // get the correction from the PIDF, given the current position
        correction = motionController.update(measuredPosition);
    }
}

