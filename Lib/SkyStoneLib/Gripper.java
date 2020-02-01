package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class Gripper {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    enum State {
        GRIPPED, RELEASED, GRIPPING, RELEASING
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863 gripperServo;

    private double initPos = 0.1;
    private double releasePosition = 0.9;
    private double gripPosition = 0.3;
    private double homePos = 0.5;
    private State gripperState;
    private Telemetry telemetry;
    private ElapsedTime timer;
    private boolean pendingGrip;
    private boolean pendingRelease;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    public void setGripperState(State gripperState) {
        this.gripperServo = gripperServo;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Gripper(String servoName, HardwareMap hardwareMap, Telemetry telemetry) {
        gripperServo = new Servo8863(servoName, hardwareMap, telemetry, homePos, releasePosition, gripPosition, initPos, Servo.Direction.FORWARD);
        this.telemetry = telemetry;
        timer.reset();
        pendingGrip = false;
        pendingRelease = false;
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

    public void init() {
        release();
    }

    public boolean isInitComplete() {
        return true;
    }

    public void gripBlock() {
        pendingGrip = true;
    }

    public void releaseBlock() {
        pendingRelease = true;
    }

    public void shutdown() {
        release();
    }

    public void update() {
        telemetry.addData("servo states: ", gripperState);
        switch (gripperState) {

            case RELEASED:
                if (pendingGrip == true) {
                    pendingGrip = false;
                    grip();
                    gripperState = State.GRIPPING;
                    timer.reset();
                }
                break;
            case GRIPPING:
                if (timer.milliseconds() > 1000) {
                    setGripperState(State.GRIPPED);
                    timer.reset();

                }
                break;
            case RELEASING:
                if (timer.milliseconds() > 1000) {
                    setGripperState(State.RELEASED);
                    timer.reset();
                }
                break;
            case GRIPPED:
                if (pendingRelease == true) {
                    pendingRelease = false;
                    release();
                    setGripperState(State.RELEASING);
                }
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
