package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class ClimberDumpServo {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Servo8863 climberDumpServo;

   // where the climber arm normally rests
    private double dumpHomePosition = 0.0;

    // the position to dump the climber is this
    private double dumpUpPosition = .25;

    // an extra unused position so just make it the same as the home position
    private double dumpDownPosition = 0.0;

    private double dumpInitPosition = dumpHomePosition;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************

    // no getters and setters needed since the variable are all private and no one using this class
    // needs access to them. Only need getter and setter if someone needs access to the variables.

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ClimberDumpServo(HardwareMap hardwareMap, Telemetry telemetry) {
        climberDumpServo = new Servo8863(RobotConfigMapping.getClimberDumpServoName(), hardwareMap, telemetry, dumpHomePosition, dumpUpPosition, dumpDownPosition, dumpInitPosition, Servo.Direction.FORWARD);
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

    public void goDumpClimber() {
        climberDumpServo.goUp();
    }

    public void goHome() {
        climberDumpServo.goHome();
    }

    public void goInit() {
        climberDumpServo.goInitPosition();
    }
}
