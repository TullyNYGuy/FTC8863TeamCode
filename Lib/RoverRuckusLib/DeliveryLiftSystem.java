package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


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
    private double dumpServoHomePosition = 1.0;
    private double dumpServoDumpPosition = 0.1;
    private double dumpServoInitPosition = 1.0;
    private double dumpServoTransferPosition = 0.4;

    private Telemetry telemetry;
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
        //gear ratio big gear: 80 teeth small is 48
        liftMotor.setMovementPerRev(0.45);

        this.telemetry=telemetry;
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
    public DcMotor8863.MotorState update(){
       return liftMotor.update();
    }
    public void shutdown(){dumpServo.goHome();}

    public void testLiftMotorEncoder(){
       int encoderValue = liftMotor.getCurrentPosition();
       telemetry.addData("Encoder= ",encoderValue);
    }

    /**
     * Move to a position based on zero which is set when the lift is all the way down, must run
     * update rotuine in a loop after that.
     * @param heightInInches how high the lift will go up relative to all the way down
     */
    public void moveToPosition(double heightInInches){
        liftMotor.moveToPosition(.5,heightInInches, DcMotor8863.FinishBehavior.FLOAT);
    }
    public void dehangTheRobot(){
        moveToPosition(12.125);
    }
    public double getLiftPosition(){
        return liftMotor.getPositionInTermsOfAttachment();
    }
    public void testSystem(){
        deliveryBoxToDump();
        delay(2000);
        deliveryBoxToHome();
        delay(2000);
        moveToPosition(4);
        delay(2000);
        moveToPosition(0);
    }
    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
