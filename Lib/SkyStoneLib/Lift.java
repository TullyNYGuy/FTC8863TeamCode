package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

public class Lift extends ExtensionRetractionMechanism {

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
private double raiseOffLimitSwitchPower = 0.1;

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

    public Lift(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                String extensionLimitSwitchName, String retractionLimitSwitchName,
                String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution ){
        super(hardwareMap, telemetry, mechanismName, extensionLimitSwitchName, retractionLimitSwitchName, motorName, motorType, movementPerRevolution);

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
    // sequence of states
    // These states are in the parent
    // MOVING_TO_RESET - when limit switch pressed
    //    (from parent)
    //     float the motor
    //     stop the motor
    //     stop and reset the encoder
    // PERFORM_POST_RESET_ACTIONS (override this)

    // These states are in the child
    // wait for 200 mSec to allow encoder reset to take hold - when time expires ->
    // move lift up - when limit switch no longer pressed ->
    // set motor to hold position, set target position to the current position, change motor to RUN_TO_POSITION
    // now post reset actions are complete
    //

    private void moveOffRetractionLimitSwitch() {
        // when the mechanism extends you may want to do something with whatever is attached to it.
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(raiseOffLimitSwitchPower);
    }

    /**
     * This method is the default for checking to see if the mechanism is in the retracted position.
     * You can override it if you have a different method. Note that the retraction limit position
     * is assumed to be the least value possible for all of the possible positions of the mechanism.
     *
     * @return true if EITHER extension limit switch is pressed OR if current position is equal to
     * or less than the extension position.
     */
    protected boolean isRetractionLimitReached() {
        boolean retractionLimitSwitchReached = false;
        // if a limit switch is not present, the retractedLimitSwitch object will be null.
        // Only check it if it is present.
        if (retractedLimitSwitch != null) {
            if (retractedLimitSwitch.isPressed()) {
                retractionLimitSwitchReached = true;
                log("Retraction limit switch tripped" + mechanismName);
            }
        }
        return (retractionLimitSwitchReached);
    }

    /**
     * This method will be executed after a retract movement completes. You need to write custom code
     * for your specific mechanism for the actions you want to perform. This gives you the
     * opportunity to do things that must happen after this mechanism moves to a retract position.
     * For example, if you have another mechanism attached to this one, and it has to turn a certain
     * way in order after the retract movement is complete, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePostResetActionsComplete() in all cases.
     */
    protected void performPostResetActions() {
        // put your actions that need to be performed here
        moveOffRetractionLimitSwitch();
        log("Beginning to move off lift " + mechanismName);
    }

    /**
     * This method returns true when all the post retract actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform after a movement to a retract position, just return true.
     *
     * @return true when all post retract actions are complete.
     */
    @Override
    protected boolean arePostResetActionsComplete() {
        boolean result = false;
        // put your custom code to check whether the actions are complete here
        if(!isRetractionLimitReached()){
            result = true;
            log("Post reset actions complete" + mechanismName);
        }
        return result;
    }
}
