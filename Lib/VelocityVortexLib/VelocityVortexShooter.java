package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTracker;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

import java.util.HashMap;
import java.util.Map;

public class VelocityVortexShooter {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum BallGatePosition {
        OPEN,
        CLOSE
    }

    public enum Position {
        LOAD(50),
        ONE_FOOT(12000),
        TWO_FEET(9000),
        THREE_FEET(7000),
        FOUR_FEET(6000),
        FIVE_FEET(5000),
        SIX_FEET(4000),
        SEVEN_FEET(3000),
        EIGHT_FEET(2000),
        LIMIT_SWITCH(-20000); // a large negative number to get it to hit the switch

        public final int intVal;

        Position(int i) {
            this.intVal = i;
        }
    }

    public enum State {
        RESET_ENCODER,
        MOVING_TO_LIMIT_SWITCH,
        AT_SWITCH,
        MOVING_TO_LOAD,
        AT_LOAD,
        MOVING_TO_1_FOOT,
        AT_1_FOOT,
        MOVING_TO_2_FEET,
        AT_2_FEET,
        MOVING_TO_3_FEET,
        AT_3_FEET,
        MOVING_TO_4_FEET,
        AT_4_FEET,
        MOVING_TO_5_FEET,
        AT_5_FEET,
        MOVING_TO_6_FEET,
        AT_6_FEET,
        MOVING_TO_7_FEET,
        AT_7_FEET,
        MOVING_TO_8_FEET,
        AT_8_FEET,
        SHOOTING,
        SHOT_TAKEN,
        MANUAL_AIM_NO_INTERRUPT,
        MANUAL_AIM_INTERRUPTABLE;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public DcMotor8863 shooterMotor;
    public DcMotor8863 aimingMotor;
    public Servo8863 ballGateServo;
    public Switch limitSwitch;
    private ElapsedTime timer;

    private Telemetry telemetry;

    private DcMotor8863.MotorState shooterMotorState = DcMotor8863.MotorState.IDLE;
    private DcMotor8863.MotorState aimingMotorState = DcMotor8863.MotorState.IDLE;
    private double aimingMotorPower = .5;
    private double automaticAimingMotorPower = 1;
    private double manualAimingPower = .5;
    private double shooterMotorPower = .5;
    private int encoderOffset = 0;
    private int encoderActual = 0;
    private int adjustedEncoderCmd = 0;
    private boolean encoderOffsetAlreadySet = false;

    private int shotCount = 0;

    private Switch.SwitchPosition limitSwitchPosition = Switch.SwitchPosition.PRESSED;

    private double openPosition = 0.50;
    private double closedPosition = 1.00;
    private double initPosition = closedPosition;

    private BallGatePosition ballGatePosition = BallGatePosition.CLOSE;

    private State shooterState = State.AT_LOAD;

    // debug
    boolean debug = false;

    int resetEncoderCount = 0;
    int atSwitchCount = 0;
    int setEncoderOffsetCount = 0;
    int movingTo1FootCounter = 0;
    int at1FootCounter = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getshooterMotorPower() {
        return shooterMotorPower;
    }

    public int getShotCount() {
        return shotCount;
    }

    public int getEncoderOffset() {
        return encoderOffset;
    }

    public int getResetEncoderCount() {
        return resetEncoderCount;
    }

    public int getAtSwitchCount() {
        return atSwitchCount;
    }

    public int getSetEncoderOffsetCount() {
        return setEncoderOffsetCount;
    }

    public int getMovingTo1FootCounter() {
        return movingTo1FootCounter;
    }

    public int getAt1FootCounter() {
        return at1FootCounter;
    }

    public double getAutomaticAimingMotorPower() {
        return automaticAimingMotorPower;
    }

    public int getAdjustedEncoderCmd() {
        return adjustedEncoderCmd;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public VelocityVortexShooter(HardwareMap hardwareMap, Telemetry telemetry) {
        // setup the motor
        shooterMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getShooterMotorName(), hardwareMap);
        shooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        shooterMotor.setMovementPerRev(360);
        shooterMotor.setTargetEncoderTolerance(10);
        shooterMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        shooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        shooterMotor.setMinMotorPower(-1);
        shooterMotor.setMaxMotorPower(1);

        // setup the motor
        aimingMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getShooterLeadscrewMotorName(), hardwareMap);
        aimingMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        aimingMotor.setMovementPerRev(360);
        aimingMotor.setTargetEncoderTolerance(10);
        aimingMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        aimingMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        aimingMotor.setMinMotorPower(-1);
        aimingMotor.setMaxMotorPower(1);

        // setup the servo
        this.telemetry = telemetry;
        ballGateServo = new Servo8863(RobotConfigMappingForGenericTest.getBallGateServoName(), hardwareMap, telemetry);
        ballGateServo.setHomePosition(closedPosition);
        ballGateServo.setPositionOne(openPosition);
        ballGateServo.setInitPosition(closedPosition);
        ballGateServo.goInitPosition();

        timer = new ElapsedTime();
        timer.reset();

        limitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getShooterLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);

        // locate the shooter aiming position

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
    public void init() {
        // set its direction
        shooterMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        shooterMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //delay(100);

        aimingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // set its direction
        // Positive power means move away from the limit switch
        aimingMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        aimingMotor.runAtConstantSpeed(0);
        shooterState = State.MANUAL_AIM_INTERRUPTABLE;

        closeBallGate();

        timer.reset();
    }

    public void update() {
        shooterMotor.update();
        updateAiming();
    }

    public void shutdown() {
        shooterMotor.setPower(0);
        shooterMotor.shutDown();
        // return the shooter to its loading position then shutdown the motor
        aimingMotor.setPower(0);
        aimingMotor.shutDown();

        closeBallGate();
    }

    //--------------------------------------------
    //  Encoder methods
    //--------------------------------------------

    /**
     * Translate a real encoder value to a virtual encoder value.
     *
     * @return
     */
    public int getVirtualEncoderValue() {
        return aimingMotor.getCurrentPosition() - encoderOffset;
    }

    /**
     * Translate the virtual encoder value to a real encoder value
     *
     * @param virtualEncoderValue
     * @return
     */
    private int getActualEncoderValue(int virtualEncoderValue) {
        return virtualEncoderValue + encoderOffset;
    }

    /**
     * This method is meant to be run when the shooter is at the limit switch. It stores the
     * enocoder value at that point. This value can then be subtracted from any later encoder value
     * to find a virtual encoder value that is always the same at the same shooter position.
     */
    private void setEncoderOffset() {
        setEncoderOffsetCount++;
        encoderOffset = aimingMotor.getCurrentPosition();
    }

    //--------------------------------------------
    //  shooter methods
    //--------------------------------------------

    /**
     * For joystick control over shooter motor
     *
     * @param power
     */
    public void setPower(double power) {
        shooterMotor.setPower(power);
    }


    public void shoot() {
        shotCount++;
        shooterMotor.moveToPosition(shooterMotorPower, 360 * shotCount, DcMotor8863.FinishBehavior.HOLD);
        // reset the shooter timer so we can wait a bit after the motor has completed its movement
        timer.reset();
    }

    public void shootAndLoad() {
        shoot();
        shooterState = State.SHOOTING;
        update();
    }


    //--------------------------------------------
    //  aiming methods
    //--------------------------------------------


    // NEED TO add limit switch shut down of motor into this method.

    /**
     * Use the joystick to input a power to the leadscrew to manually aim the shooter.
     *
     * @param aimingPower
     */
    public void aimShooter(double aimingPower) {
        this.manualAimingPower = aimingPower;
//        limitSwitch.updateSwitch();
//        limitSwitchPosition = limitSwitch.getSwitchPosition();
//        if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
//            shooterState = State.AT_SWITCH;
//            // The limit switch is hit, disable any movement backwards
//            if (aimingPower < 0) {
//                aimingPower = 0;
//            }
//        } else {
//            shooterState = State.MANUAL_AIM;
//        }
//        updateManualAiming();
    }

    public void moveToLoadPosition() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.LOAD.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_LOAD;
            updateAiming();
        }
    }

    public void moveToLimitSwitch() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.LIMIT_SWITCH.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_LIMIT_SWITCH;
            updateAiming();
        }
    }

    public void moveTo1Foot() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            adjustedEncoderCmd = getActualEncoderValue(Position.ONE_FOOT.intVal);
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, adjustedEncoderCmd, DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_1_FOOT;
            updateAiming();
        }
    }

    public void moveTo2Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.TWO_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_2_FEET;
            updateAiming();
        }
    }

    public void moveTo3Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.THREE_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_3_FEET;
            updateAiming();
        }
    }

    public void moveTo4Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.FOUR_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_4_FEET;
            updateAiming();
        }
    }

    public void moveTo5Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.FIVE_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_5_FEET;
            updateAiming();
        }
    }

    public void moveTo6Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.SIX_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_6_FEET;
            updateAiming();
        }
    }

    public void moveTo7Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.SEVEN_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_7_FEET;
            updateAiming();
        }
    }

    public void moveTo8Feet() {
        if (isAutoAimingOK()) {
            aimingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimingMotor.stop();
            aimingMotor.rotateToEncoderCount(automaticAimingMotorPower, getActualEncoderValue(Position.EIGHT_FEET.intVal), DcMotor8863.FinishBehavior.HOLD);
            shooterState = State.MOVING_TO_8_FEET;
            updateAiming();
        }
    }

    private boolean isAutoAimingOK() {
        boolean result = true;
        // Nothing can interrupt manual aiming with a non 0 power applied
        if (shooterState == State.MANUAL_AIM_NO_INTERRUPT || shooterState == State.AT_SWITCH) {
            result = false;
        }
        return result;
    }

    //--------------------------------------------
    //  ball gate methods
    //--------------------------------------------

    public void openBallGate() {
        ballGateServo.goPositionOne();
        ballGatePosition = BallGatePosition.OPEN;
    }

    public void closeBallGate() {
        ballGateServo.goHome();
        ballGatePosition = BallGatePosition.CLOSE;
    }

    public void toggleBallGate() {
        if (ballGatePosition == BallGatePosition.CLOSE) {
            openBallGate();
        } else {
            closeBallGate();
        }
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //--------------------------------------------
    //  Switch methods
    //--------------------------------------------

    public boolean isLimitSwitchPressed() {
        return limitSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE);
    }

    //--------------------------------------------
    //  State machine
    //--------------------------------------------

    public State updateAiming() {

        // update the objects
        aimingMotorState = aimingMotor.update();
        limitSwitch.updateSwitch();
        limitSwitchPosition = limitSwitch.getSwitchPosition();

        switch (shooterState) {
            case MOVING_TO_LIMIT_SWITCH:
                // if there is a manual power requested that is not 0, then honor that
                if (manualAimingPower != 0) {
                    // switch the mode of the motor
                    aimingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    // since the user is requesting a manual move don't allow it to be interrupted
                    shooterState = State.MANUAL_AIM_NO_INTERRUPT;
                    aimingMotor.setPower(manualAimingPower);
                } else {
                    // check to see if the motor has arrived at the position
                    if (aimingMotor.isMotorStateComplete()) {
                        shooterState = State.AT_SWITCH;
                    }
                }
                // check to see is something went wrong and the limit switch has been hit
                if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
                    shooterState = State.RESET_ENCODER;
                    aimingMotor.setPower(0);
                }
                break;
            case RESET_ENCODER:
                resetEncoderCount++;
                aimingMotor.setPower(0);
                // When the limit switch is hit, this is the virtual encoder point = 0. In order
                // to make all the other vitual encoder values work we need to get the real encoder
                // value at this point and save it.
                if (!encoderOffsetAlreadySet) {
                    setEncoderOffset();
                    encoderOffsetAlreadySet = true;
                }
                // switch the motor state so that a manual power will be honored
                // This works even if we got here from an automatic mode because any succeeding
                // automatic command will set the mode back to RUN_TO_POSITION
                aimingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                shooterState = State.AT_SWITCH;
                break;
            case AT_SWITCH:
                atSwitchCount++;
                // There are 2 ways out of this state:
                //    manual power is applied in a positive direction so the shooter moves off the
                //    limit switch after some period of time
                //    an automatic aiming is requested
                // I only need to deal with the manual power here since the automatic request
                // changes state as part of the request

                // grab the encoder offset if it has not already been grabbed
                if (!encoderOffsetAlreadySet) {
                    setEncoderOffset();
                    encoderOffsetAlreadySet = true;
                }
                // since the shooter is on the switch, any negative power will only force the shooter
                // down farther and break things. If the power is negative, force it to 0
                if (manualAimingPower < 0) {
                    manualAimingPower = 0;
                }
                // if the shooter is not in contact with the switch anymore change back to manual
                // aim
                if (limitSwitchPosition != Switch.SwitchPosition.PRESSED) {
                    shooterState = State.MANUAL_AIM_NO_INTERRUPT;
                }
                aimingMotor.setPower(manualAimingPower);
                break;

            case MOVING_TO_LOAD:
                movingToAutomaticPosition(State.AT_LOAD);
                break;
            case AT_LOAD:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_1_FOOT:
                movingToAutomaticPosition(State.AT_1_FOOT);
                movingTo1FootCounter++;
                break;
            case AT_1_FOOT:
                at1FootCounter++;
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_2_FEET:
                movingToAutomaticPosition(State.AT_2_FEET);
                break;
            case AT_2_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_3_FEET:
                movingToAutomaticPosition(State.AT_3_FEET);
                break;
            case AT_3_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_4_FEET:
                movingToAutomaticPosition(State.AT_4_FEET);
                break;
            case AT_4_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_5_FEET:
                movingToAutomaticPosition(State.AT_5_FEET);
                break;
            case AT_5_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_6_FEET:
                movingToAutomaticPosition(State.AT_6_FEET);
                break;
            case AT_6_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_7_FEET:
                movingToAutomaticPosition(State.AT_7_FEET);
                break;
            case AT_7_FEET:
                checkAndAllowManualAiming();
                break;

            case MOVING_TO_8_FEET:
                movingToAutomaticPosition(State.AT_8_FEET);
                break;
            case AT_8_FEET:
                checkAndAllowManualAiming();
                break;

            case SHOOTING:
                if (shooterMotor.isMotorStateComplete()) {
                    // shot taken, wait a little
                    if (timer.milliseconds() > 100) {
                        shooterState = State.SHOT_TAKEN;
                        timer.reset();
                    }
                }
                break;
            case SHOT_TAKEN:
                // After the shot has been taken move the shooter to load position
                moveToLoadPosition();
                break;

            case MANUAL_AIM_NO_INTERRUPT:
                // if there is a manual aiming power that is 0 then this is interruptable
                if (manualAimingPower == 0) {
                    shooterState = State.MANUAL_AIM_INTERRUPTABLE;
                }
                // check to see is something went wrong and the limit switch has been hit
                // If it has then move to reset the encoder and set the motor power to 0
                if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
                    shooterState = State.RESET_ENCODER;
                    manualAimingPower = 0;
                }
                // If the manualAiming power != 0 and limit switch has not been hit there will
                // be no adjustment to the manualAimingPower
                aimingMotor.setPower(manualAimingPower);
                break;
            case MANUAL_AIM_INTERRUPTABLE:
                // if there is a manual aiming power that is not 0 then this takes priority over
                // anything else so change state to not allow interrupts. Power will remain the same
                if (manualAimingPower != 0) {
                    shooterState = State.MANUAL_AIM_NO_INTERRUPT;
                }
                // check to see is something went wrong and the limit switch has been hit.
                // If it has then move to reset the encoder and set the motor power to 0
                if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
                    shooterState = State.RESET_ENCODER;
                    manualAimingPower = 0;
                }
                // if the manualAimingPower = 0 and the limit switch has not been hit there will be
                // no adjustment to the manualAimingPower
                aimingMotor.setPower(manualAimingPower);
                break;
        }

        return shooterState;
    }

    private void movingToAutomaticPosition(State desiredState) {
        // if there is a manual power requested that is not 0, then honor that
        if (manualAimingPower != 0) {
            // switch the mode of the motor
            aimingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // since the user is requesting a manual move don't allow it to be interrupted
            shooterState = State.MANUAL_AIM_NO_INTERRUPT;
            aimingMotor.setPower(manualAimingPower);
        } else {
            // check to see if the motor has arrived at the position
            if (aimingMotor.isMotorStateComplete()) {
                shooterState = desiredState;
            }
        }
        // removed this code because the shooter will not come off the limit switch when an
        // automatic mode is selected - SAFETY????
//        // check to see is something went wrong and the limit switch has been hit
//        if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
//            shooterState = State.RESET_ENCODER;
//            aimingMotor.setPower(0);
//        }
    }


    private void checkAndAllowManualAiming() {
        if (manualAimingPower != 0) {
            // switch the mode of the motor
            aimingMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // since the user is requesting a manual move don't allow it to be interrupted
            shooterState = State.MANUAL_AIM_NO_INTERRUPT;
            aimingMotor.setPower(manualAimingPower);
        }
    }

//    public State updateManualAiming() {
//
//        // update the objects
//        aimingMotorState = aimingMotor.update();
//        limitSwitch.updateSwitch();
//        limitSwitchPosition = limitSwitch.getSwitchPosition();
//
//        switch (shooterState) {
//
//            case AT_SWITCH:
//                atSwitchCount++;
//                if (!encoderOffsetAlreadySet) {
//                    setEncoderOffset();
//                    encoderOffsetAlreadySet = true;
//                }
//                // sit at the limit switch until therre is a power in the right direction to take
//                // the shooter off the limit switch. There will be a period of time that the limit
//                // switch is still pressed while the shooter moves away from it. For that we have
//                // to continue to apply power. Then once the limit switch has been cleared move
//                // back to the manual aim state.
//                if (aimingMotorPower >= 0) {
//                    if (limitSwitchPosition != Switch.SwitchPosition.PRESSED) {
//                        shooterState = State.MANUAL_AIM;
//                    }
//                } else {
//                    // if the aiming power is in the wrong direction force motor to stop
//                    aimingMotorPower = 0;
//                }
//                aimingMotor.setPower(aimingMotorPower);
//                break;
//
//            case MANUAL_AIM:
//                // check to see is something went wrong and the limit switch has been hit
//                if (limitSwitchPosition == Switch.SwitchPosition.PRESSED) {
//                    shooterState = State.AT_SWITCH;
//                    aimingMotor.setPower(0);
//                } else {
//                    aimingMotor.setPower(aimingMotorPower);
//                }
//        }
//
//        return shooterState;
//    }

    public State getShooterState() {
        return shooterState;
    }
}