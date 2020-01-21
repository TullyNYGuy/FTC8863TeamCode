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

    //protected DcMotor8863 extensionRetractionMotor;

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


    /**
     * This method overrides the parent method for creating the motor since the extension arm
     * does not use a real motor. It uses a continuous rotation servo with encoder feedback instead.
     *
     * @param hardwareMap
     * @param telemetry
     * @param motorName
     */
    @Override
    protected void createExtensionRetractionMotor(HardwareMap hardwareMap, Telemetry telemetry, String motorName) {
        // the encoder is plugged into the drive train FrontLeft motor port
        extensionRetractionMotor = new DcServoMotor("FrontLeft", "extensionArmServoMotor", 0.5, 0.5, .01, hardwareMap, telemetry);
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
}
