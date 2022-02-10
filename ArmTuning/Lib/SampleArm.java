package org.firstinspires.ftc.teamcode.ArmTuning.Lib;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AngleUtilities;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MechanismUnits;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorType;

public class SampleArm {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public DcMotor8863 armMotor;
    private String motorName = "armMotor";
    private MechanismUnits mechanismUnits;

    private FtcDashboard dashboard;
    TelemetryPacket packet;

    private int encoderCounts;
    private double position;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public SampleArm(HardwareMap hardwareMap, Telemetry telemetry) {
        this.armMotor = new DcMotor8863(motorName, hardwareMap, telemetry);
        armMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        armMotor.setMovementPerRev(360); // 360 degrees per revolution, our position will be in degrees
        armMotor.setDirection(DcMotorSimple.Direction.FORWARD); // says which direction (clockwise or counter clockwise) is considered a positive rotation
        // this code is a hybrid of the MotorType built into the DcMotor8863 and the new MotorType enum. Just for testing purposes and just for now.
        // Long term the DcMotor8863 will get the new MotorType enum incorporated into it
        mechanismUnits = new MechanismUnits(360.0, AngleUnit.DEGREES, armMotor.getCountsPerRev());

        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        packet = new TelemetryPacket();
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

    /**
     * Get the position of the arm as an angle from 0-360 or radians from 0-2*PI
     * @param units - your desired units; degrees or radians
     * @return - angle normalized to the range and in the requested units
     */
    public double getPosition(AngleUnit units) {
        encoderCounts = armMotor.getCurrentPosition();
        switch (units) {
            case DEGREES:
                position = AngleUtilities.to0to360(mechanismUnits.toMechanism(encoderCounts, units), units);
                break;
            case RADIANS:
                position = AngleUtilities.to0to2PI(mechanismUnits.toMechanism(encoderCounts, units), units);
                break;
        }

        return position;
    }

    /**
     * Get the value of the motor encoder for the arm
     * @return counts
     */
    public int getCounts() {
        return armMotor.getCurrentPosition();
    }

    /**
     * Reset the motor encoder to 0. Effectively makes the current location of the arm 0 degrees.
     */
    public void resetEncoder() {
        armMotor.resetEncoder();
    }

    /**
     * Hold the arm at the vertical position.
     */
    public void holdAtVertical() {
        armMotor.holdAtPosition(ArmConstants.VERTICAL_POSITION);
    }

    /**
     * Hold the arm at the horizontal position that is closest to its 0 position.
     */
    public void holdAtHorizontal() {
        armMotor.holdAtPosition(ArmConstants.HORIZONTAL_POSITION);
    }

    public void setMode(DcMotor.RunMode mode) {
        armMotor.setMode(mode);
    }

    public void setPower(double power) {
        armMotor.setPower(power);
    }

    public void setSDKkF(double kF, DcMotor.RunMode runMode) {
        armMotor.setVelocityFCoefficient(kF, runMode);
    }

    public void setSDKkG() {
        setSDKkF(calculateCompensatedkG(), DcMotor.RunMode.RUN_TO_POSITION);
    }

    // todo check the signs in all quadrants for compensatedkG
    public double calculateCompensatedkG() {
        return (ArmConstants.getKg() * Math.cos(ArmConstants.getAngleToHorizontal(getPosition(AngleUnit.RADIANS),AngleUnit.RADIANS)));
    }

    public TelemetryPacket putPosition(TelemetryPacket packet, int encoderCount, double position) {
        packet.put("encoder = ", encoderCount);
        packet.put("position = ", position);
        return packet;
    }

    public TelemetryPacket putPosition(TelemetryPacket packet, AngleUnit units) {
        getPosition(units);
        return putPosition(packet, encoderCounts, position);
    }
}
