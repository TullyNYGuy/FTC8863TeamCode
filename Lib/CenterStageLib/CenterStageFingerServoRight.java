package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class CenterStageFingerServoRight {

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
    private CenterStageFingerServo rightFingerServo;
    private final String RIGHT_FINGER_SERVO_NAME = CenterStageRobot.HardwareName.RIGHT_FINGER_SERVO.hwName;

    public static double INIT_POSITION = 0.5;
    public static double OPEN_POSITION = 0.5;
    public static double CLOSE_POSITION = 0.21;

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

    public CenterStageFingerServoRight(HardwareMap hardwareMap, Telemetry telemetry) {
        rightFingerServo = new CenterStageFingerServo(hardwareMap, telemetry,
                RIGHT_FINGER_SERVO_NAME,
                INIT_POSITION,
                OPEN_POSITION,
                CLOSE_POSITION,
                Servo.Direction.REVERSE);
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
        rightFingerServo.init();
    }
    
    public void open() {
        rightFingerServo.open();
    }

    public void close() {
        rightFingerServo.close();
    }

    public boolean isPositionReached() {
        return rightFingerServo.isPositionReached();
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        rightFingerServo.testPositionUsingJoystick(opmode);
    }
}
