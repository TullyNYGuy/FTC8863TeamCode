package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class GripperRotator implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum GripperRotatorStates {
        INITTING,
        INIT_FINISHED,
        ROTATING_INWARD,
        IN,
        ROTATING_OUTWARD,
        OUT
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863 servoGripperRotator;

    private GripperRotatorStates gripperRotatorState;

    public GripperRotatorStates getGripperRotatorState() {
        return gripperRotatorState;
    }

    private double initPos = 0;
    private double outwardPos = 0.95;
    private double inwardPos = 0;
    private double homePos = inwardPos;

    private final static String SUBSYSTEM_NAME = "GripperRotator";

    public Telemetry telemetry;
    private ElapsedTime timer;
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
        servoGripperRotator = new Servo8863(servoName, hardwareMap, telemetry, homePos, outwardPos, inwardPos, initPos, Servo.Direction.FORWARD);
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
        servoGripperRotator.goUp();
        timer.reset();
        gripperRotatorState = GripperRotatorStates.ROTATING_OUTWARD;
    }

    public void rotateInward() {
        servoGripperRotator.goDown();
        timer.reset();
        gripperRotatorState = GripperRotatorStates.ROTATING_INWARD;
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (gripperRotatorState == GripperRotatorStates.INIT_FINISHED) {
            return true;
        } else
            return false;
    }

    @Override
    public boolean init(Configuration config) {
        rotateInward();
        timer.reset();
        gripperRotatorState = GripperRotatorStates.INITTING;
        return true;
    }

    @Override
    public void update() {
        switch (gripperRotatorState) {
            case INITTING:
                if (timer.milliseconds() > 1000) {
                    gripperRotatorState = GripperRotatorStates.INIT_FINISHED;
                }
                break;
            case INIT_FINISHED:
                break;

            case ROTATING_INWARD:
                if (timer.milliseconds() > 1000) {
                    gripperRotatorState = GripperRotatorStates.IN;
                    timer.reset();
                }
                break;
            case IN:
                break;

            case ROTATING_OUTWARD:
                if (timer.milliseconds() > 1000) {
                    gripperRotatorState = GripperRotatorStates.OUT;
                    timer.reset();
                }
                break;
            case OUT:
                break;
        }
        telemetry.update();
    }

    public boolean isRotateOutwardComplete() {
        if (gripperRotatorState == gripperRotatorState.OUT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRotateInwardComplete() {
        if (gripperRotatorState == gripperRotatorState.IN) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void shutdown() {
        rotateInward();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

}
