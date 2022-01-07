package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class ClawServo {

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
    Servo8863New clawServo;

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

    public ClawServo(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new Servo8863New("clawServo", hardwareMap, telemetry);
        clawServo.addPosition("open", .0, 1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("close", .58, 3000,1000, TimeUnit.MILLISECONDS);

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

    public void open() {
        clawServo.setPosition("open");
    }

    public void close() {
        clawServo.setPosition("close");
    }
    public boolean isPositionReached() {
        return clawServo.isPositionReached();
    }
}

