package org.firstinspires.ftc.teamcode.Lib.RoverRuckus;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class DeliveryLiftSystem {

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
    private DcMotor8863 liftMotor;

    private Servo8863 dumpServo;
    private double dumpServoHomePosition = 0.8;
    private double dumpServoDumpPosition = 0.1;
    private double dumpServoInitPosition = 0.8;
    private double dumpServoTransferPosition = 0.4;
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
    public DeliveryLiftSystem(HardwareMap hardwareMap, Telemetry telemetry) {
        dumpServo = new Servo8863("dumpServo", hardwareMap, telemetry, dumpServoHomePosition, dumpServoDumpPosition, dumpServoInitPosition, dumpServoInitPosition, Servo.Direction.FORWARD);
        dumpServo.setPositionOne(dumpServoTransferPosition);
        liftMotor = new DcMotor8863("liftMotor",hardwareMap,telemetry);
        liftMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        //gear ratio big gear: 76 teeth small is 48
        liftMotor.setMovementPerRev(8*48/76*1/25.4);
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
    public void deliveryBoxToDump(){
        dumpServo.goUp();
    }
    public void deliveryBoxToHome(){
        dumpServo.goHome();
    }
    public void deliveryBoxToTransfer(){
        dumpServo.goPositionOne();
    }
    public void init(){dumpServo.goHome();}
    public void update(){}
    public void shutdown(){dumpServo.goHome();}
}
