package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class ShoulderMotor {

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
    private DcMotor8863 shoulderMotor;
    private final String SHOULDER_MOTOR_NAME = FreightFrenzyRobotRoadRunner.HardwareName.SHOULDER_MOTOR.hwName;
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
    public ShoulderMotor(HardwareMap hardwareMap, Telemetry telemetry) {
        shoulderMotor = new DcMotor8863(SHOULDER_MOTOR_NAME, hardwareMap);
        shoulderMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_117); // this sets the type of motor we are using
        shoulderMotor.setMovementPerRev(360); // 360 degrees per revolution, our position will be in degrees
        shoulderMotor.setDirection(DcMotorSimple.Direction.FORWARD); // says which direction (clockwise or counter clockwise) is considered a positive rotation
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

    public void up() {
        // power ranges from -1.0 (backwards rotation) to +1.0 (forwards rotation). Example: 0.5 is forwards 50% power
        // position is in degrees. 0 is the starting position (on the stop)
        // HOLD tells the motor to hold its position when it reaches the target position
        shoulderMotor.moveToPosition(1.0,135.0, DcMotor8863.FinishBehavior.HOLD);
    }

    public void down() {
        shoulderMotor.moveToPosition( 1.0,225.0, DcMotor8863. FinishBehavior.HOLD);
    }

    public void storage() {
        shoulderMotor.moveToPosition(  1.0,0, DcMotor8863. FinishBehavior.HOLD);
    }

    public boolean isPositionReached() {
        return shoulderMotor.isMovementComplete();
    }

    public boolean isInitComplete() {
        return shoulderMotor.isMovementComplete();
    }

    public void update() {
        shoulderMotor.update();
    }
}
