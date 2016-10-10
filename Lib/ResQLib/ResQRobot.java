package org.firstinspires.ftc.teamcode.Lib.ResQLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;


public class ResQRobot {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Mode {
        TELEOP, AUTONOMOUS
    }

    public static DriveTrain driveTrain;

    public DeliveryBox deliveryBox;
;
    public ClimberDumpServo climberDumpServo;

    public BarGrabberServo barGrabberServoA;
    public BarGrabberServoExtends barGrabberServoX;

    public LeftZipLineServo leftZipLineServo;
    public RightZipLineServo rightZipLineServo;

    public TapeMeasureWinch tapeMeasureWinch;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    public double deliveryBoxThrottle;
    private static Telemetry telemetry;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    private ResQRobot(HardwareMap hardwareMap, Telemetry telemetry) {
        deliveryBox = new DeliveryBox(hardwareMap, telemetry);

        climberDumpServo = new ClimberDumpServo(hardwareMap, telemetry);

        barGrabberServoA = new BarGrabberServo(hardwareMap, telemetry);

        barGrabberServoX = new BarGrabberServoExtends(hardwareMap, telemetry);

        leftZipLineServo = new LeftZipLineServo(hardwareMap, telemetry);
        rightZipLineServo = new RightZipLineServo(hardwareMap, telemetry);

        tapeMeasureWinch = new TapeMeasureWinch(hardwareMap, telemetry);
    }

    /**
     * Factory class that returns a ResQRobot object setup for Teleop. Call this instead of using
     * constructor.
     * @param hardwareMap
     * @return ResQRobot object
     */
    public static ResQRobot ResQRobotTeleop(HardwareMap hardwareMap, Telemetry telemetry) {
        ResQRobot resQRobot = new ResQRobot(hardwareMap, telemetry);
        //this.telemetry = telemetry;
        driveTrain = DriveTrain.DriveTrainTeleop(hardwareMap);
        return resQRobot;
    }

    /**
     * Factory class that returns a ResQRobot object setup for Autonomous. Call this instead of using
     * constructor.
     * @param hardwareMap
     * @return ResQRobot object
     */
    public static ResQRobot ResQRobotAutonomous(HardwareMap hardwareMap, Telemetry telemetry) {
        ResQRobot resQRobot = new ResQRobot(hardwareMap,telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
        return resQRobot;
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

    /**
     * Update the robot every time through the loop() in the opmode
     */
    public void updateRobot(){
        deliveryBox.updateDeliveryBox(deliveryBoxThrottle);

        //will need to add updates for the drivetrain and other systems
    }
}
