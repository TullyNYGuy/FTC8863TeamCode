package org.firstinspires.ftc.teamcode.Lib.LauncherBot;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

import java.util.concurrent.TimeUnit;

public class LauncherBotShooterServo {

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
    private Servo8863New shooterServo;
    private final String SHOOTER_SERVO = LauncherBotRobot.HardwareName.SHOOTER_SERVO.hwName;
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

    public LauncherBotShooterServo(HardwareMap hardwareMap, Telemetry telemetry) {
        shooterServo = new Servo8863New(SHOOTER_SERVO, hardwareMap, telemetry);
        shooterServo.setDirection(Servo.Direction.REVERSE);
        shooterServo.addPosition("Init", .80, 500, TimeUnit.MILLISECONDS);
        shooterServo.addPosition("Hit", 1.00, 500, TimeUnit.MILLISECONDS);
        shooterServo.addPosition("Drop", .80, 500, TimeUnit.MILLISECONDS);
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
        shooterServo.setPosition("Init");
    }

    public void kick() {
        shooterServo.setPosition("Hit");
    }

    public void retract() {
        shooterServo.setPosition("Drop");
    }

    public boolean isPositionReached() {
        return shooterServo.isPositionReached();
    }
}
