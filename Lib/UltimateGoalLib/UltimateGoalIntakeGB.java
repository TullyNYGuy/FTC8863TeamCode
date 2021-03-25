package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class UltimateGoalIntakeGB {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
            OFF,
            DELAY,
            ONE_ON,
            ONE_TWO_ON,
            ONE_TWO_THREE_ON,
            TWO_THREE_ON,
            THREE_ON
    }
    private enum Commands {
            TURN_ON_123,
            TURN_ON_12,
            TURN_ON_1,
            TURN_ON_23,
            TURN_ON_3,
            OFF
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private State currentState = State.OFF;

    private ElapsedTime turnOnTimer;

    private Switch stage1Switch;
    private Switch stage2Switch;
    private Switch stage3Switch;

    private DcMotor8863 stage1Motor;

    private CRServo stage2CRServo;
    private CRServo stage3CRServo;

    private int numberOfRingsAtStage3 = 0;

    private boolean commandComplete=true;

    private Commands currentCommand= Commands.OFF;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getNumberOfRingsAtStage3() {
        return numberOfRingsAtStage3;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public UltimateGoalIntakeGB(HardwareMap hardwareMap, Telemetry telemetry) {
        stage1Switch = new Switch(hardwareMap, "stage1Switch", Switch.SwitchType.NORMALLY_OPEN);
        stage2Switch = new Switch(hardwareMap, "stage2Switch", Switch.SwitchType.NORMALLY_OPEN);
        stage3Switch = new Switch(hardwareMap, "stage2Switch", Switch.SwitchType.NORMALLY_OPEN);

        stage1Motor = new DcMotor8863("stage1Motor", hardwareMap, telemetry);
        stage1Motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        stage1Motor.setMovementPerRev(360);
        stage1Motor.setDirection(DcMotorSimple.Direction.REVERSE);
        stage1Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        stage1Motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stage2CRServo = hardwareMap.get(CRServo.class, "stage2CRServo");
        stage2CRServo.setDirection(DcMotorSimple.Direction.REVERSE);
        stage3CRServo = hardwareMap.get(CRServo.class, "stage3CRServo");

        turnOnTimer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void turnStage1On(){
        stage1Motor.runAtConstantPower(1);
    }

    private void turnStage1Off() {
        stage1Motor.stop();
    }

    private void turnStage2On() {
        stage2CRServo.setPower(1);
    }

    private void turnStage2Off() {
        stage2CRServo.setPower(0);
    }

    private void turnStage3On() {
        stage3CRServo.setPower(1);
    }

    private void turnStage3Off() {
        stage3CRServo.setPower(0);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void updateIntake() {
        switch (currentState) {
            case OFF:
                switch (currentCommand){
                    case TURN_ON_123:
                        turnOnTimer.reset();
                        turnStage2On();
                        turnStage3On();
                        commandComplete = false;
                        currentState = State.DELAY;
                        break;
                    case TURN_ON_1:
                        turnStage1On();
                        commandComplete = true;
                        currentState = State.ONE_ON;
                        break;
                    case TURN_ON_12:
                        turnOnTimer.reset();
                        turnStage2On();
                        commandComplete = false;
                        currentState = State.DELAY;
                        break;
                    case TURN_ON_23:
                        turnStage2On();
                        turnStage3On();
                        commandComplete = true;
                        break;
                    case TURN_ON_3:
                        turnStage3On();
                        commandComplete = true;
                        break;
                    case OFF:
                        // already off
                        break;
                }
                break;
            case DELAY:
                switch (currentCommand){
                    case TURN_ON_123:
                        if (turnOnTimer.milliseconds() > 1000) {
                            turnStage1On();
                            commandComplete = true;
                            currentState = State.ONE_TWO_THREE_ON;
                        }
                        break;
                    case TURN_ON_1:
                        // there is no delay to turn 1 on
                        break;
                    case TURN_ON_12:
                        if (turnOnTimer.milliseconds() > 1000) {
                            turnStage1On();
                            commandComplete = true;
                            currentState = State.ONE_TWO_ON;
                        }
                        break;
                    case TURN_ON_23:
                        // there is no delay to turn 2 3 on
                        break;
                    case TURN_ON_3:
                        // there is no delay to turn 3 on
                        break;
                    case OFF:
                        // there is not delay to turn off
                        break;
                }
                break;
            case ONE_ON:
                switch (currentCommand){
                    case TURN_ON_123:
                        turnStage2On();
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        // already on
                        break;
                    case TURN_ON_12:
                        turnStage2On();
                        currentState = State.ONE_TWO_ON;
                        break;
                    case TURN_ON_23:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_3:
                        // bogus command, we would never do this
                        break;
                    case OFF:
                        turnStage1Off();
                        break;
                }
                break;
            case TWO_THREE_ON:
                switch (currentCommand){
                    case TURN_ON_123:
                        turnStage1On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_12:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_23:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_3:
                        // bogus command, we would never do this
                        break;
                    case OFF:
                        turnStage2Off();
                        turnStage3Off();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
                break;
            case THREE_ON:
                switch (currentCommand){
                    case TURN_ON_123:
                        turnOnTimer.reset();
                        turnStage2On();
                        commandComplete = false;
                        currentState = State.DELAY;
                        break;
                    case TURN_ON_1:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_12:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_23:
                        turnStage2On();
                        commandComplete = true;
                        currentState = State.TWO_THREE_ON;
                        break;
                    case TURN_ON_3:
                        // already on
                        break;
                    case OFF:
                        turnStage3Off();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
            case ONE_TWO_THREE_ON:
                switch (currentCommand){
                    case TURN_ON_123:
                        // already on
                        break;
                    case TURN_ON_1:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_12:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_23:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_3:
                        // bogus command, we would never do this
                        break;
                    case OFF:
                        commandComplete = true;
                        turnIntakeOff();
                        currentState = State.OFF;
                        break;
                }
                break;
            case ONE_TWO_ON:
                switch (currentCommand){
                    case TURN_ON_123:
                        turnStage3On();
                        commandComplete = true;
                        currentState = State.ONE_TWO_THREE_ON;
                        break;
                    case TURN_ON_1:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_12:
                        // already on
                        break;
                    case TURN_ON_23:
                        // bogus command, we would never do this
                        break;
                    case TURN_ON_3:
                        // bogus command, we would never do this
                        break;
                    case OFF:
                        turnStage1Off();
                        turnStage2Off();
                        commandComplete = true;
                        currentState = State.OFF;
                        break;
                }
        }
        if (stage3Switch.isBumped()) {
            numberOfRingsAtStage3 = numberOfRingsAtStage3 ++;
        }
    }

    public void intakeClearedOfRings() {
        numberOfRingsAtStage3 = 0;
    }

    public boolean ringAtStage1() {
        return stage1Switch.isPressed();
    }

    public boolean ringAtStage2() {
        return stage2Switch.isPressed();
    }

    public boolean ringAtStage3() {
        return stage3Switch.isPressed();
    }


    public void turnIntakeOff() {
        turnStage1Off();
        turnStage2Off();
        turnStage3Off();
        currentState = State.OFF;
    }

    public void turnIntake123On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_123;
            commandComplete = false;
        }
    }

    public void turnIntake1On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_1;
            commandComplete = false;
        }
    }

    public void turnIntake12On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_12;
            commandComplete = false;
        }
    }

    public void turnIntake23On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_23;
            commandComplete = false;
        }
    }

    public void turnIntake3On() {
        if (commandComplete) {
            currentCommand = Commands.TURN_ON_3;
            commandComplete = false;
        }
    }
}
