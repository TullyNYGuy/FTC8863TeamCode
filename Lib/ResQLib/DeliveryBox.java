package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class DeliveryBox {

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

    CRServo slideServo;
    Servo8863 rampServo;

    private double dumpHomePosition = 1;
    private double dumpUpPosition = 0;
    private double dumpDownPosition = 0.0;
    private double dumpInitPosition = dumpHomePosition;

    /**
     * Amount of time to wait between "wiggle" movements when dump ramp is wiggling
     */
    private double dumpWiggleDelay = .5;

    /**
     * Amount of time to wiggle the dump ramp trying to get debris out of the box
     */
    private double dumpWiggleTime = 5;

    /**
     * Amount of movement for each wiggle of the dump ramp
     */
    private double dumpWiggleDelta = -.2;

    private double slideServoZeroThrottle = .52;
    private double slideServoZeroZone = .1;

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

    public DeliveryBox(HardwareMap hardwareMap, Telemetry telemetry) {
        slideServo = new CRServo(RobotConfigMapping.getLinearSlideServoName(),hardwareMap, slideServoZeroThrottle, slideServoZeroZone);
        rampServo = new Servo8863(RobotConfigMapping.getRampServoName(), hardwareMap, telemetry, dumpHomePosition, dumpUpPosition, dumpDownPosition, dumpInitPosition, Servo.Direction.REVERSE);

        //setup the dump ramp wiggle for later use
        rampServo.setupWiggle(dumpUpPosition, dumpWiggleDelay, dumpWiggleDelta, dumpWiggleTime);

        //for reference using a touch sensor will need statements like these
        // private TouchSensor v_sensor_touch;
        //v_sensor_touch = hardwareMap.touchSensor.get ("sensor_touch");
        //v_sensor_touch.isPressed ();
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

    public void initDeliveryBox() {
        // set slide servo so box is not moving
        slideServo.updatePosition(slideServoZeroThrottle);
    }

    public void updateDeliveryBox(double throttle){
        // update the linear slider throttle
        // if the limit switch has been pressed, the box is at the end of its movement
        // force the box to stop moving despite what the operator says. Here are the details:
        // the left limit switch should only stop movement to the left, it should allow no movement
        //    or movement to the right
        // the right limit swtich should only stop movement to the right, it should allow no movement
        //    or movement to the left

        // for now, there are no limit switches installed so just go ahead and move the box.
        // I hope the operator is good!
        slideServo.updatePosition(throttle);

        //update the wiggle for the dump ramp (if any)
        rampServo.updateWiggle();
    }

    public void raiseDumpRamp(){
        rampServo.goUp();
    }

    public void lowerDumpRamp() {
        rampServo.goHome();
    }

    public void startWiggleDumpRamp() {
        rampServo.startWiggle();
    }

    public void stopWiggleDumpRamp() {
        rampServo.stopWiggle();
    }

    public void initDumpRamp() {
        rampServo.goInitPosition();
    }


}
