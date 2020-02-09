package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class GripperRotator implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "GripperRotator";
    public RotatorStates rotatorState = RotatorStates.IN;
    public Telemetry telemetry;
    private ElapsedTime timer;

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum RotatorStates {
        INITTING, INIT_FINISHED, ROTATING_INWARD, IN, ROTATING_OUTWARD, OUT
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863 servoRotator;
    private double initPos = 0;
    private double outwardPos = 0.95;
    private double inwardPos = 0;
    private double homePos = 0.00;
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
    public GripperRotator(HardwareMap hardwareMap, String servoName, Telemetry telemetry) {
        servoRotator = new Servo8863(servoName, hardwareMap, telemetry, homePos, outwardPos, inwardPos, initPos, Servo.Direction.FORWARD);
        timer = new ElapsedTime();
        this.telemetry = telemetry;
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
    public void rotateOutward() {
        servoRotator.goUp();
        timer.reset();
        rotatorState = RotatorStates.ROTATING_OUTWARD;

    }

    public void rotateInward() {
        servoRotator.goDown();
        timer.reset();
        rotatorState = RotatorStates.ROTATING_INWARD;

    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (rotatorState == RotatorStates.INIT_FINISHED) {
            return true;
        } else
            return false;
    }

    @Override
    public boolean init(Configuration config) {
        rotateInward();
        timer.reset();
        rotatorState = RotatorStates.INITTING;
        return true;
    }

    @Override
    public void update() {
        telemetry.addData("servo states: ", rotatorState);
        switch (rotatorState) {
            case INITTING:
                if (timer.milliseconds() > 1000) {
                    rotatorState = RotatorStates.INIT_FINISHED;
                }
                break;

            case INIT_FINISHED:
                break;
            case IN:
                break;
            case ROTATING_INWARD:
                if (timer.milliseconds() > 1000) {
                    rotatorState = RotatorStates.IN;
                    timer.reset();
                }
                break;
            case ROTATING_OUTWARD:
                if (timer.milliseconds() > 1000) {
                    rotatorState = RotatorStates.OUT;
                    timer.reset();
                }
                break;
            case OUT:
                break;
        }
        telemetry.update();
    }

    public boolean isRotateOutwardComplete() {
        if (rotatorState == rotatorState.OUT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRotateInwardComplete() {
        if (rotatorState == rotatorState.IN) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void shutdown() {
        rotateInward();
    }

}
