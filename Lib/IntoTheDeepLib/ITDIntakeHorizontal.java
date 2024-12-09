package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import android.net.IpSecManager;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ITDIntakeHorizontal {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum State {
        INTAKING,
        CHECK_WHAT_WE_GOT,
        OUTTAKING,
        OFF,
        HAVE_GOOD_SAMPLE

    }
    private State state = State.OFF;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public ITDIntakeSweeperServo intakeSweeperServo;
    public ITDIntakeColorSensor intakeColorSensor;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public ITDIntakeHorizontal(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeColorSensor = new ITDIntakeColorSensor(hardwareMap, telemetry,"intakeColorSensorV3Left");
        intakeSweeperServo = new ITDIntakeSweeperServo(hardwareMap, telemetry);
        state = State.OFF;
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          Commands
    //
    //*********************************************************************************************
    public void stop() {
        intakeSweeperServo.stop();
    }

    public void intake() {
        intakeSweeperServo.intake();
    }

    public void intakeThenStop(double delayTimeInMillisec) {
        intakeSweeperServo.intakeThenStop(delayTimeInMillisec);
    }

    public void outtake() {
        intakeSweeperServo.outtake();
    }

    public void outtakeThenStop(double delayTimeInMillisec) {
        intakeSweeperServo.outtakeThenStop(delayTimeInMillisec);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void update() {
        switch(state) {
            case OFF: {

            }
            break;
            case INTAKING: {

            }
            break;
            case OUTTAKING: {

            }
            break;
            case HAVE_GOOD_SAMPLE: {

            }
            break;
        }
    }
}
