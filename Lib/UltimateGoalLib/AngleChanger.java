package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

import kotlin.Unit;

public class AngleChanger {


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
    //CURRENT ANGLE IS IN RADIANS
    private double currentAngle;

    private DcMotor8863 motor;

    private final double MAX_ANGLE = Math.toRadians(55);
    private final double MIN_ANGLE = Math.toRadians(0);

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCurrentAngle() {
        return Math.toDegrees(currentAngle);
    }

    public void setCurrentAngle(double currentAngle) {
        if (currentAngle > MAX_ANGLE) {
            currentAngle = MAX_ANGLE;
        }
        if (currentAngle < MIN_ANGLE) {
            currentAngle = MIN_ANGLE;
        }
        this.currentAngle = Math.toRadians(currentAngle);

        motor.moveToPosition(0.8, calculateLeadScrewPosition(currentAngle), DcMotor8863.FinishBehavior.HOLD);
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public AngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        currentAngle = AngleStorage.shooterAngle;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double calculateLeadScrewPosition(double desiredAngle) {
        //constants
        double initialLength = toMM(1.345);
        double initialAngle = Math.toRadians(9.961);
        //Side A is the bottom side side B is the shooter
        double sideA = toMM(6.593);
        double sideB = toMM(7.207);

        double leadScrewPosition = Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngle + initialAngle)) - initialLength;
        return leadScrewPosition;
    }

    private double toMM(double inches) {
        return inches * 25.4;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setAngleReference() {
        currentAngle = 0;
        AngleStorage.shooterAngle = 0;
    }

    public void update() {
        motor.update();
    }

    public boolean isAngleAdjustComplete() {
      if(motor.isRotationComplete()){
          AngleStorage.shooterAngle = currentAngle;
          return true;
      }else {
          return false;
      }

    }
}
