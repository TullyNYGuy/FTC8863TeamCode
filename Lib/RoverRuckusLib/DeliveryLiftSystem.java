package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class DeliveryLiftSystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum Commands{
        GO_TO_BOTTOM,
        GO_TO_TOP,
        GO_TO_POSITION,
        RESET
    }
    private enum States{
        UNKOWN,
        BOTTOM,
        IN_BETWEEN,
        TOP
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DcMotor8863 liftMotor;

    private Servo8863 dumpServo;
    private double dumpServoHomePosition = 0.9;
    private double dumpServoDumpPosition = 0.1;
    private double dumpServoInitPosition = 0.5;
    private double dumpServoTransferPosition = 0.7;
    private Switch bottomLimitSwitch;
    private Switch topLimitSwitch;
    private Commands commands;
    private Telemetry telemetry;
    private States state;
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

        bottomLimitSwitch = new Switch(hardwareMap, "bottomLiftSwitch", Switch.SwitchType.NORMALLY_OPEN);
        topLimitSwitch = new Switch(hardwareMap,"topLiftSwitch", Switch.SwitchType.NORMALLY_OPEN);
        state =  States.UNKOWN;
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

    public void shutdown(){dumpServo.goHome();}

    public void testLiftMotorEncoder(){
       int encoderValue = liftMotor.getCurrentPosition();
       telemetry.addData("Encoder= ",encoderValue);
    }
    public void goToBottom(){
        commands = Commands.GO_TO_BOTTOM;
    }
    public void goToTop(){
        commands = Commands.GO_TO_TOP;
    }
    public void reset(){
        commands = Commands.RESET;
    }

    private void sendToBottom(){
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(-1);
    }

    private void sendToTop(){
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(1);
    }

    /**
     * Move to a position based on zero which is set when the lift is all the way down, must run
     * update rotuine in a loop after that.
     * @param heightInInches how high the lift will go up relative to all the way down
     */
    public void moveToPosition(double heightInInches){
       commands = Commands.GO_TO_POSITION;
       liftMotor.moveToPosition(1,heightInInches, DcMotor8863.FinishBehavior.FLOAT);
    }
    public void dehang(){
        moveToPosition(11.25);
    }

    public void undehang(){
        moveToPosition(.25);
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

    public void update(){
        DcMotor8863.MotorState motorState = liftMotor.update();
        switch (state){
            case UNKOWN:
                switch (commands){
                    case RESET:
                        sendToBottom();
                        break;
                    case GO_TO_BOTTOM:
                        sendToBottom();
                        break;
                    case GO_TO_TOP:
                        break;
                    case GO_TO_POSITION:
                        break;
                }
                break;
            case BOTTOM:
                switch (commands){
                    case RESET:
                        break;
                    case GO_TO_BOTTOM:
                        break;
                    case GO_TO_TOP:
                        sendToTop();
                        break;
                    case GO_TO_POSITION:
                        break;
                }
                break;
            case IN_BETWEEN:
                switch (commands){
                    case RESET:
                        break;
                    case GO_TO_BOTTOM:
                        sendToBottom();
                        break;
                    case GO_TO_TOP:
                        sendToTop();
                        break;
                    case GO_TO_POSITION:
                        break;
                }
                break;
            case TOP:
                switch (commands){
                    case RESET:
                        break;
                    case GO_TO_BOTTOM:
                        sendToBottom();
                        break;
                    case GO_TO_TOP:
                        break;
                    case GO_TO_POSITION:
                        break;
                }
                break;
        }
    }



}
