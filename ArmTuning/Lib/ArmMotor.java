package org.firstinspires.ftc.teamcode.ArmTuning.Lib;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MechanismUnits;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorType;

public class ArmMotor {

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
    public ArmMotor(HardwareMap hardwareMap, Telemetry telemetry) {
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
    public double getPosition(AngleUnit units) {
        encoderCounts = armMotor.getCurrentPosition();
        position = mechanismUnits.toMechanism(encoderCounts, units);
        return position;
    }

    public int getCounts() {
        return armMotor.getCurrentPosition();
    }

    public void resetEncoder() {
        armMotor.resetEncoder();
    }

    public void holdAtVertical() {
        armMotor.holdAtPosition(ArmConstants.VERTICAL_POSITION);
    }

    public void setMode(DcMotor.RunMode mode) {
        armMotor.setMode(mode);
    }

    public void setPower(double power) {
        armMotor.setPower(power);
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
