package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

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

    private enum LiftResetExtraStates{
        WAITING_FOR_TIMER,
        MOVING_OFF_LIMIT_SWITCH,
        TENSION_COMPLETE
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private double raiseOffLimitSwitchPower = 0.1;

    private LiftResetExtraStates liftResetExtraState = LiftResetExtraStates.WAITING_FOR_TIMER;
    private LiftResetExtraStates previousLiftResetExtraState = liftResetExtraState;
    private LiftResetExtraStates currentLiftResetExtraState = liftResetExtraState;

    private ElapsedTime resetTimer;

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
                String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        super(hardwareMap, telemetry, mechanismName, extensionLimitSwitchName, retractionLimitSwitchName, motorName, motorType, movementPerRevolution);
        resetTimer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Write the state and command into the log file ONLY if the state has changed or the command has changed.
     * @param liftResetExtraState
     */
    protected void logResetExtraState(LiftResetExtraStates liftResetExtraState) {
        if (logFile != null && loggingOn) {
            if (liftResetExtraState != previousLiftResetExtraState) {
                logFile.logData(mechanismName, liftResetExtraState.toString());
                previousLiftResetExtraState = liftResetExtraState;
            }
        }
    }

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

    // These states are in the child. State machine update is done in arePostResetActionsComplete()
    // wait for 200 mSec to allow encoder reset to take hold - when time expires ->
    // move lift up - when limit switch no longer pressed ->
    // set motor to hold position, set target position to the current position, change motor to RUN_TO_POSITION
    // now post reset actions are complete
    //

    /**
     * This method is called before the start of a reset sequence to check if the mechanism is
     * ok to reset. Put your code for determining that here. The code here is a suggestion. It
     * assumes that the reset fully retracts the mechanism.
     *
     * @return
     */
    protected boolean isOKToReset() {
        // for the lift it is always ok to reset because the sting has to be tensioned so the lift
        // has to go through the entire reset/tension process
        return true;
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
        log("Beginning to tension string " + mechanismName);
        resetTimer.reset();
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
        // run the state machine for the extra reset states
        if (updateResetExtraStates() == LiftResetExtraStates.TENSION_COMPLETE) {
            // string tension is completed, let the rest of the reset state machine complete
            result = true;
            log("Lift string tensioned " + mechanismName);
        }
        return result;
    }

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
            }
        }
        return (retractionLimitSwitchReached);
    }

    public LiftResetExtraStates updateResetExtraStates() {
        logResetExtraState(liftResetExtraState);

        switch (liftResetExtraState) {
            case WAITING_FOR_TIMER:
                if (resetTimer.milliseconds() > 200) {
                    // The lift has been sitting on the limit switch for long enough. Move the lift
                    // up off the limit switch
                    log("reset timer expired, moving lift up off limit switch " + mechanismName);
                    moveOffRetractionLimitSwitch();
                    liftResetExtraState = LiftResetExtraStates.MOVING_OFF_LIMIT_SWITCH;
                }
                break;
            case MOVING_OFF_LIMIT_SWITCH:
                if (!isRetractionLimitReached()) {
                    // the lift is no longer pressing the retraction limit switch. Stop the lift and
                    // make it hold its position
                    // make the target position the current position
                    extensionRetractionMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
                    extensionRetractionMotor.setTargetPosition((extensionRetractionMotor.getCurrentPosition()));
                    extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    // power should already be set to raiseOffLimitSwitchPower
                    liftResetExtraState = LiftResetExtraStates.TENSION_COMPLETE;
                    log("Holding position off limit switch, string tensioned " + mechanismName);
                }
                break;
            case TENSION_COMPLETE:
                // do nothing but sit in this state
                break;
        }
        currentLiftResetExtraState = liftResetExtraState;
        return liftResetExtraState;
    }
}
