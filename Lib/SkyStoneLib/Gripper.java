package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class Gripper implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "Gripper";

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    enum State {
        GRIPPING, RELEASED, GRIPPED, RELEASING, INITTING, INIT_FINISHED
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863 gripperServo;


    private double releasePosition = 0.20;
    private double initPos = releasePosition;
    private double gripPosition = 0.74;
    private double homePos = releasePosition;

    private State gripperState;
    private Telemetry telemetry;
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
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Gripper(HardwareMap hardwareMap, String servoName, Telemetry telemetry) {
        gripperServo = new Servo8863(servoName, hardwareMap, telemetry, homePos, releasePosition, gripPosition, initPos, Servo.Direction.FORWARD);
        this.telemetry = telemetry;
        timer = new ElapsedTime();
        timer.reset();
        gripperState = State.RELEASED;
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
    public void release() {
        gripperServo.goUp();
    }

    public void grip() {
        gripperServo.goDown();
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (gripperState == State.INIT_FINISHED) {
            return true;
        } else return false;
    }

    @Override
    public boolean init(Configuration config) {
        gripperServo.goInitPosition();
        timer.reset();
        gripperState = State.INITTING;
        return true;
    }

    public void gripBlock() {
        grip();
        timer.reset();
        gripperState = State.GRIPPING;

    }

    public void releaseBlock() {
        release();
        timer.reset();
        gripperState = State.RELEASING;
    }

    @Override
    public void shutdown() {
        release();
    }

    @Override
    public void update() {
        telemetry.addData("servo states: ", gripperState);
        switch (gripperState) {
            case INITTING:
                if (timer.milliseconds() > 500) {
                    gripperState = State.INIT_FINISHED;
                }
                break;

            case INIT_FINISHED:
                break;
            case RELEASED:
                break;
            case GRIPPING:
                if (timer.milliseconds() > 1000) {
                    gripperState = State.GRIPPED;
                    timer.reset();
                }
                break;
            case RELEASING:
                if (timer.milliseconds() > 1000) {
                    gripperState = State.RELEASED;
                    timer.reset();
                }
                break;
            case GRIPPED:
                break;
        }
        telemetry.update();
    }

    public boolean IsGripComplete() {
        if (gripperState == State.GRIPPED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isReleaseComplete() {
        if (gripperState == State.RELEASED) {
            return true;
        } else {
            return false;
        }
    }
}
