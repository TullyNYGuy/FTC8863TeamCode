package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcServoMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PairedList;

public class ExtensionArm extends ExtensionRetractionMechanism {

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

    protected DcMotor8863 extensionRetractionMotor;

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

    public ExtensionArm(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                        String extensionLimitSwitchName, String retractionLimitSwitchName,
                        String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        super(hardwareMap, telemetry, mechanismName, extensionLimitSwitchName, retractionLimitSwitchName, motorName, motorType, movementPerRevolution);
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
    protected void createExtensionRetractionMechanismCommonCommands(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                                                                    String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        // set all of the private variables using the parameters passed into the constructor
        motorName = motorName;
        motorType = motorType;
        extensionLimitSwitchName = extensionLimitSwitchName;
        retractionLimitSwitchName = retractionLimitSwitchName;
        movementPerRevolution = movementPerRevolution;
        this.mechanismName = mechanismName;
        telemetry = telemetry;

        // create the motor
        extensionRetractionMotor = new DcServoMotor("extensionArmMotor", "extensionArmServoMotor", 0.5, 0.5, .01, hardwareMap, telemetry);
        extensionRetractionMotor.setMotorType(motorType);
        extensionRetractionMotor.setMovementPerRev(movementPerRevolution);
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setDirection(DcMotor.Direction.REVERSE);

        // set the initial state of the state machine
        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;

        // create the time encoder data list in case it is needed
        timeEncoderValues = new PairedList();
        liftTimer = new ElapsedTime();
    }
}
