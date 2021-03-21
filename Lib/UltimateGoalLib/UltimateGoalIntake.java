package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import android.sax.StartElementListener;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class UltimateGoalIntake {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
            OFF,
            TURNING_ON,
            STAGE1ON,
            STAGE2ON,
            STAGE3ON,
            ON
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

    public UltimateGoalIntake(HardwareMap hardwareMap, Telemetry telemetry) {
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

    public void updateIntake() {
        switch (currentState) {
            case OFF:
                break;
            case TURNING_ON:
                turnStage2On();
                turnStage3On();
                if(turnOnTimer.milliseconds() > 1000) {
                    currentState = State.STAGE2ON;
                }
                break;
            case STAGE2ON:
            case STAGE3ON:
                turnStage1On();
                currentState = State.ON;
                break;
            case STAGE1ON:
                break;
            case ON:
                break;
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

    public void turnStage1On(){
        stage1Motor.runAtConstantPower(1);
    }

    public void turnStage1Off() {
        stage1Motor.stop();
    }

    public void turnStage2On() {
        stage2CRServo.setPower(1);
    }

    public void turnStage2Off() {
        stage2CRServo.setPower(0);
    }

    public void turnStage3On() {
        stage3CRServo.setPower(1);
    }

    public void turnStage3Off() {
        stage3CRServo.setPower(0);
    }

    public void turnIntakeOff() {
        turnStage1Off();
        turnStage2Off();
        turnStage3Off();
        currentState = State.OFF;
    }

    public void turnIntakeOn() {
        turnStage2On();
        turnStage1On();
        turnStage3On();
    }

    public void turnIntake123On() {
        turnOnTimer.reset();
        currentState = State.TURNING_ON;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}
