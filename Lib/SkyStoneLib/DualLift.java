package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

public class DualLift implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "DualLift";

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum LiftSide {
        LEFT(0),
        RIGHT(1);

        public final int side;

        private LiftSide(int side) {
            this.side = side;
        }
    }

    private enum PositionReachedStates {
        LEFT_REACHED,
        RIGHT_REACHED,
        BOTH_REACHED,
        NONE_REACHED
    }

    private PositionReachedStates positionReachedState;


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    public Lift liftRight;
    public Lift liftLeft;

    private DcMotor8863.MotorType motorType = org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;
    private double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    private double movementPerRevolution = spoolDiameter * Math.PI * 5;

    private DataLogging logFileBoth;

    private ExtensionRetractionMechanism.ExtensionRetractionStates[] dualLiftStates;

    private Lift.LiftResetExtraStates[] liftResetExtraStates;

    private int[] encoderValues;

    private int baseEncoderValue = 0;

    private boolean collectData = false;

    private boolean dataLogging = false;

    public DataLogging timeEncoderValueFile = null;

    private ElapsedTime positionReachedTimer;

    private int liftLeftTensionCompleteEncoderValue;

    public int getLiftLeftTensionCompleteEncoderValue() {
        return liftLeftTensionCompleteEncoderValue;
    }

    private int liftRightTensionCompleteEncoderValue;

    public int getLiftRightTensionCompleteEncoderValue() {
        return liftRightTensionCompleteEncoderValue;
    }

    public double encoderToInchMath = 1120 / 20.47;

    public int leftBaseEncoderValue = 0;

    public int rightBaseEncoderValue = 0;

    public int rightLiftEncoderValue;

    public int leftLiftEncoderValue;

    private double correction;

    private int correctionMultiplier;

    private double desiredPower;

    private PIDControl pidControl;
    private boolean enablePID = false;

    private Telemetry telemetry;

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

    public DualLift(HardwareMap hardwareMap,
                    String liftRightName,
                    String liftRightMotorName,
                    String liftRightExtensionLimitSwitchName,
                    String liftRightRetractionLimitSwitch,
                    String liftRightZeroLimitSwitchName,
                    String liftLeftName,
                    String liftLeftMotorName,
                    String liftLeftExtensionLimitSwitchName,
                    String liftLeftRetractionLimitSwitch,
                    String liftLeftZeroLimitSwitchName,
                    Telemetry telemetry) {
        liftRight = new Lift(hardwareMap, telemetry, liftRightName,
                liftRightExtensionLimitSwitchName, liftRightRetractionLimitSwitch, liftRightZeroLimitSwitchName, liftRightMotorName,
                motorType, movementPerRevolution);
        liftLeft = new Lift(hardwareMap, telemetry, liftLeftName,
                liftLeftExtensionLimitSwitchName, liftLeftRetractionLimitSwitch, liftLeftZeroLimitSwitchName, liftLeftMotorName,
                motorType, movementPerRevolution);

        dualLiftStates = new ExtensionRetractionMechanism.ExtensionRetractionStates[2];
        getState();

        liftResetExtraStates = new Lift.LiftResetExtraStates[2];
        getResetState();

        positionReachedTimer = new ElapsedTime();

        encoderValues = new int[2];
        getEncoderValues();

        disableCollectData();
        disableDataLogging();

        configureForSkystone();

        pidControl = new PIDControl(.002, 0, 1);

        this.telemetry = telemetry;
    }

    private void configureForSkystone() {
        liftRight.reverseMotor();
        liftRight.setExtensionPositionInEncoderCounts(DualLiftConstants.maximumExtensionInEncoderCounts);
        liftRight.setMovementPerRevolution(DualLiftConstants.movementPerRevolution);
        liftRight.setResetPower(DualLiftConstants.resetPower);

        liftLeft.setExtensionPositionInEncoderCounts(DualLiftConstants.maximumExtensionInEncoderCounts);
        liftLeft.setMovementPerRevolution(DualLiftConstants.movementPerRevolution);
        liftLeft.setResetPower(DualLiftConstants.resetPower);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public int getCurrentEncoderValueLeft() {
        return liftLeft.getCurrentEncoderValue();
    }

    public int getCurrentEncoderValueRight() {
        return liftRight.getCurrentEncoderValue();
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return (liftRight.isResetComplete() && liftLeft.isResetComplete());
    }

    @Override
    public boolean init(Configuration config) {
        liftRight.reset();
        liftLeft.reset();
        return true;
    }

    public void reset() {
        liftRight.reset();
        liftLeft.reset();
        enablePID = false;
    }

    public boolean isResetComplete() {
        if (liftRight.isResetComplete() && liftLeft.isResetComplete()) {
            liftLeftTensionCompleteEncoderValue = liftLeft.getTensionCompleteEncoderValue();
            liftRightTensionCompleteEncoderValue = liftRight.getTensionCompleteEncoderValue();
            ////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////
            //Take this out
            // need to capture the base encoder value before the adjustment, it should be the same as the tension complete value
            // need to captuer the base encoder value after the adjusetment, it should be the same as tension complete + tweak value
            leftBaseEncoderValue = liftLeft.getBaseEncoderValue();
            rightBaseEncoderValue = liftRight.getBaseEncoderValue();
            //liftRight.setBaseEncoderValue(liftRight.getBaseEncoderValue() + 104);
            //liftLeft.setBaseEncoderValue(liftLeft.getBaseEncoderValue() + 164);
            leftLiftEncoderValue = liftLeft.getMotorEncoderValue();
            rightLiftEncoderValue = liftRight.getMotorEncoderValue();

            // need to capture the altered and unaltered encoder values
            ////////////////////////////////////////////////////////////////////////////////////////////
            if (collectData) {
                timeEncoderValueFile.startTimer();
            }
            return true;
        } else {
            return false;
        }
    }

    public void goToFullExtend() {
        liftRight.goToFullExtend();
        liftLeft.goToFullExtend();
    }

    public boolean isExtensionComplete() {
        return liftRight.isExtensionComplete() && liftLeft.isExtensionComplete();
    }

    public void goToFullRetract() {
        liftRight.goToFullRetract();
        liftLeft.goToFullRetract();
    }

    public boolean isRetractionComplete() {
        return liftRight.isRetractionComplete() && liftLeft.isRetractionComplete();
    }

    public void goToPosition(double positionInInches, double desiredPower) {
        this.desiredPower = desiredPower;
        liftRight.goToPosition(positionInInches, desiredPower);
        // this is a hardwired cob to see the effect of offsetting the command
        liftLeft.goToPosition(positionInInches, desiredPower);
        positionReachedState = PositionReachedStates.NONE_REACHED;
        enablePID = true;
        logFileBoth.logData("Dual lift PID control enabled");
        if (liftLeft.convertMechanismUnitsToEncoderCounts(positionInInches) < getCurrentEncoderValueLeft()) {
            logFileBoth.logData("Dual lift going down, Kp and multiplier set");
            correctionMultiplier = -1;
            pidControl.setKp(0.0002);
        } else {
            logFileBoth.logData("Dual lift going up, Kp and multiplier set");
            correctionMultiplier = 1;
            //todo show kellen the pid control bug
            // here is the bug in the correction! kp in the pidControl was never set
            //KP = 0.002;
            pidControl.setKp(.002);

        }
    }

    public void setExtensionPositionInMechanismUnits(double heightTimesSlides) {
        liftLeft.setExtensionPositionInMechanismUnits(heightTimesSlides);
        liftRight.setExtensionPositionInMechanismUnits(heightTimesSlides);
    }

    public boolean isPositionReached() {
        boolean result = false;
        switch (positionReachedState) {
            case NONE_REACHED:
                if (liftLeft.isPositionReached()) {
                    positionReachedTimer.reset();
                    positionReachedState = PositionReachedStates.LEFT_REACHED;
                }
                if (liftRight.isPositionReached()) {
                    positionReachedTimer.reset();
                    positionReachedState = PositionReachedStates.RIGHT_REACHED;
                }
                break;
            case LEFT_REACHED:
                if (liftRight.isPositionReached()) {
                    result = true;
                    positionReachedState = PositionReachedStates.BOTH_REACHED;
                }
                if (positionReachedTimer.milliseconds() > 1000) {
                    result = true;
                    positionReachedState = PositionReachedStates.BOTH_REACHED;
                }
                break;
            case RIGHT_REACHED:
                if (liftLeft.isPositionReached()) {
                    result = true;
                    positionReachedState = PositionReachedStates.BOTH_REACHED;
                }
                if (positionReachedTimer.milliseconds() > 1000) {
                    result = true;
                    positionReachedState = PositionReachedStates.BOTH_REACHED;
                }
                break;
            case BOTH_REACHED:
                break;
        }
        return result;
    }

    public void setPowerUsingJoystick(double power) {
        liftRight.setPowerUsingJoystick(power);
        liftLeft.setPowerUsingJoystick(power);
    }

    public void goToBottom() {
        goToPosition(0.5, 0.5);
    }

    public void lowerBlockOntoTower() {
        //go to the block height directed - 1
    }

    public void setRetractionPower(double power) {
        liftRight.setRetractionPower(power);
        liftLeft.setRetractionPower(power);
    }

    public void setExtensionPower(double power) {
        liftLeft.setExtensionPower(power);
        liftRight.setExtensionPower(power);
    }

    public void setDataLog(DataLogging logFileBoth) {
        this.logFileBoth = logFileBoth;
        liftLeft.setDataLog(logFileBoth);
        liftRight.setDataLog(logFileBoth);
    }

    public void enableDataLogging() {
        liftLeft.enableDataLogging();
        liftRight.enableDataLogging();
        dataLogging = true;
    }

    public void disableDataLogging() {
        liftLeft.disableDataLogging();
        liftRight.disableDataLogging();
        dataLogging = false;
    }

    public void enableCollectData(String filename) {
        timeEncoderValueFile = new DataLogging(filename);
        timeEncoderValueFile.headerStrings("left encoder", "right encoder");
        collectData = true;
    }

    public void disableCollectData() {
        collectData = false;
    }

    public void setResetPower(double resetPower) {
        liftRight.setResetPower(resetPower);
        liftLeft.setResetPower(resetPower);
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates[] getState() {
        dualLiftStates[LiftSide.RIGHT.side] = liftRight.getExtensionRetractionState();
        dualLiftStates[LiftSide.LEFT.side] = liftLeft.getExtensionRetractionState();
        return dualLiftStates;
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates getLeftState() {
        return liftLeft.getExtensionRetractionState();
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates getRightState() {
        return liftRight.getExtensionRetractionState();
    }

    public String stateToString() {
        // update the state property
        getState();
        return "state (L, R) = " + dualLiftStates[LiftSide.LEFT.side].toString() + " " + dualLiftStates[LiftSide.RIGHT.side].toString();
    }

    public Lift.LiftResetExtraStates[] getResetState() {
        liftResetExtraStates[LiftSide.RIGHT.side] = liftRight.getLiftResetExtraState();
        liftResetExtraStates[LiftSide.LEFT.side] = liftLeft.getLiftResetExtraState();
        return liftResetExtraStates;
    }

    public Lift.LiftResetExtraStates getLeftResetState() {
        return liftLeft.getLiftResetExtraState();
    }

    public Lift.LiftResetExtraStates getRightResetState() {
        return liftRight.getLiftResetExtraState();
    }

    public String resetStateToString() {
        // update the state property
        getState();
        return "reset state (L, R) = " + liftResetExtraStates[LiftSide.LEFT.side].toString() + " " + liftResetExtraStates[LiftSide.RIGHT.side].toString();
    }


    public int[] getEncoderValues() {
        encoderValues[LiftSide.RIGHT.side] = liftRight.getCurrentEncoderValue();
        encoderValues[LiftSide.LEFT.side] = liftLeft.getCurrentEncoderValue();
        return encoderValues;
    }

    public int getLeftEncoderValue() {
        return liftLeft.getCurrentEncoderValue();
    }

    public int getRightEncoderValue() {
        return liftRight.getCurrentEncoderValue();
    }

    public String encoderValuesToString() {
        getEncoderValues();
        return "encoder values (L, R) = " + encoderValues[LiftSide.LEFT.side] + " " + encoderValues[LiftSide.RIGHT.side];
    }

    @Override
    public void update() {
        double currentPowerLeft = 0;
        double currentPowerRight = 0;
        liftRight.update();
        liftLeft.update();
        if (collectData && !enablePID) {
            timeEncoderValueFile.logData(liftLeft.getCurrentEncoderValue(), liftRight.getCurrentEncoderValue());
        }
        //////Pid//////////
        if (enablePID) {
            correction = pidControl.getCorrection(liftRight.getCurrentEncoderValue() - liftLeft.getCurrentEncoderValue());
            currentPowerLeft = desiredPower - correction * correctionMultiplier;
            currentPowerRight = desiredPower + correction * correctionMultiplier;
            telemetry.addData("correction = ", correction);
            telemetry.addData("left power = ", currentPowerLeft);
            telemetry.addData("right power = ", currentPowerRight);
            liftLeft.setCurrentPower(currentPowerLeft);
            liftRight.setCurrentPower(currentPowerRight);
            if (collectData) {
                timeEncoderValueFile.logData(liftLeft.getCurrentEncoderValue(), liftRight.getCurrentEncoderValue(), correction, currentPowerLeft, currentPowerRight);
            }
        }

    }

    @Override
    public void shutdown() {
        liftRight.shutdown();
        liftLeft.shutdown();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    /**
     * Rotate the motor attached to the mechanism a certain number of degrees. When it stops, you
     * should measure the distance moved and come up with the distance moved / revolution.
     *
     * @param degrees
     * @param power
     * @param opMode
     */
    public void calibrate(double degrees, double power, LinearOpMode opMode) {
        int originalEncoderCountLeft = liftLeft.getCurrentEncoderValue();
        int originalEncoderCountRight = liftRight.getCurrentEncoderValue();
        liftLeft.rotateNumberOfRevolutions(.1, 1, DcMotor8863.FinishBehavior.HOLD);
        liftRight.rotateNumberOfRevolutions(.1, 1, DcMotor8863.FinishBehavior.HOLD);

        while (opMode.opModeIsActive() && !liftLeft.isMotorStateComplete() && !liftRight.isMotorStateComplete()) {
            liftLeft.update();
            liftRight.update();
            //opMode.telemetry.addData("motor state = ", extensionRetractionMotor.getCurrentMotorState().toString());
            opMode.telemetry.addData("encoder count (L,R) = ", liftLeft.getCurrentEncoderValue() + " " + liftRight.getCurrentEncoderValue());
            opMode.telemetry.update();
            opMode.idle();
        }

        double numberOfRevolutionsLeft = (liftLeft.getCurrentEncoderValue() - originalEncoderCountLeft) / (double) liftLeft.getCountsPerRev();
        double numberOfRevolutionsRight = (liftRight.getCurrentEncoderValue() - originalEncoderCountRight) / (double) liftRight.getCountsPerRev();
        opMode.telemetry.addData("encoder count (L, R) = ", liftLeft.getCurrentEncoderValue() + " " + liftRight.getCurrentEncoderValue());
        opMode.telemetry.addData("actual number of revolutions (L,R) = ", numberOfRevolutionsLeft + " " + numberOfRevolutionsRight);
        opMode.telemetry.addData("Average # revolutions = ", (numberOfRevolutionsLeft + numberOfRevolutionsRight) / 2);
        opMode.telemetry.addData("Measure the distance moved. Calculate distance / revolution", "!");
        opMode.telemetry.update();
    }

    public void testLimitSwitches(LinearOpMode opMode) {
        // needed to update the encoder values
        liftLeft.update();
        liftRight.update();

        opMode.telemetry.addData("LEFT", "=");
        if (liftLeft.isRetractionLimitSwitchPressed()) {
            opMode.telemetry.addLine("retracted limit switch pressed");
        } else {
            opMode.telemetry.addLine("retracted limit switch NOT pressed");
        }

        if (liftLeft.isExtensionLimitSwitchPressed()) {
            opMode.telemetry.addLine("extension limit switch pressed");
        } else {
            opMode.telemetry.addLine("extension limit switch NOT pressed");
        }
        if (liftLeft.isZeroLimitReached()) {
            opMode.telemetry.addLine("zero limit switch pressed");
        } else {
            opMode.telemetry.addLine("zero limit switch NOT pressed");
        }
        opMode.telemetry.addData("encoder = ", liftLeft.getCurrentEncoderValue());

        opMode.telemetry.addData("RIGHT", "=");
        if (liftRight.isRetractionLimitSwitchPressed()) {
            opMode.telemetry.addLine("retracted limit switch pressed");
        } else {
            opMode.telemetry.addLine("retracted limit switch NOT pressed");
        }

        if (liftRight.isExtensionLimitSwitchPressed()) {
            opMode.telemetry.addLine("extension limit switch pressed");
        } else {
            opMode.telemetry.addLine("extension limit switch NOT pressed");
        }

        if (liftRight.isZeroLimitReached()) {
            opMode.telemetry.addLine("zero limit switch pressed");
        } else {
            opMode.telemetry.addLine("zero limit switch NOT pressed");
        }
        opMode.telemetry.addData("encoder = ", liftRight.getCurrentEncoderValue());
    }
}
