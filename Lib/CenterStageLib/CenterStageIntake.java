package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;

public class CenterStageIntake {

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
    private DcMotor8863 intakeMotor;
    private final String SHOULDER_MOTOR_NAME = "";
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
    public CenterStageIntake(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeMotor = new DcMotor8863(SHOULDER_MOTOR_NAME, hardwareMap);
        intakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40); // this sets the type of motor we are using
        intakeMotor.setMovementPerRev(360); // 360 degrees per revolution, our position will be in degrees
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE); // says which direction (clockwise or counter clockwise) is considered a positive rotation
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
    public void on() {
        intakeMotor.runAtConstantPower(1.0);
    }

    public void off() {
        intakeMotor.runAtConstantPower(0);
    }


    public boolean isInitComplete() {
        return intakeMotor.isMovementComplete();
    }

    public void update() {
        intakeMotor.update();
    }


}