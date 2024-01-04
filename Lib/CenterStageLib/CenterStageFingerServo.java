package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

import java.util.concurrent.TimeUnit;

@Config
public class CenterStageFingerServo {

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
    private Servo8863New fingerServo;

    private String servoName;

    private double initPosition = 0.0;
    private double openPosition = 0.0;
    private double closePosition = 0.0;

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

    public CenterStageFingerServo(HardwareMap hardwareMap, Telemetry telemetry,
                                  String servoName,
                                  double initPosition,
                                  double openPosition,
                                  double closePosition,
                                  Servo.Direction direction) {
        fingerServo = new Servo8863New(servoName, hardwareMap, telemetry);
        this.servoName = servoName;

        this.initPosition = initPosition;
        fingerServo.addPosition("Init", initPosition, 1000, TimeUnit.MILLISECONDS);

        this.openPosition = openPosition;
        fingerServo.addPosition("Open", openPosition, 1000, TimeUnit.MILLISECONDS);

        this.closePosition = closePosition;
        fingerServo.addPosition("Close", closePosition, 1000, TimeUnit.MILLISECONDS);

        fingerServo.setDirection(direction);
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
        fingerServo.setPosition("Init");
    }
    
    public void open() {
        fingerServo.setPosition("Open");
    }

    public void close() {
        fingerServo.setPosition("Close");
    }

    public boolean isPositionReached() {
        return fingerServo.isPositionReached();
    }

    // wrappers
    public ServoPosition getServoPosition(String positionName) {
        return fingerServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        fingerServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        fingerServo.testPositionsUsingJoystick(opmode);
    }
}
