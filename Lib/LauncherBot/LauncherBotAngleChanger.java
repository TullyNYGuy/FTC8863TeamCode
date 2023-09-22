package org.firstinspires.ftc.teamcode.Lib.LauncherBot;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

public class LauncherBotAngleChanger implements FTCRobotSubsystem {

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

    private DcMotor8863 motor;
    private Switch lowerLimitSwitch;
    private Switch upperLimitSwitch;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //********************************************************************************************

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public LauncherBotAngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(LauncherBotRobot.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lowerLimitSwitch = new Switch(hardwareMap, LauncherBotRobot.HardwareName.ANGLE_CHANGER_LOWER_LIMIT_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
        upperLimitSwitch = new Switch(hardwareMap, LauncherBotRobot.HardwareName.ANGLE_CHANGER_UPPER_LIMIT_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
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
     * The intention is ta take in a joystick value that ranges from -1 to +1. -1 means lower the
     * angle at full speed. +1 means increase the angle at full speed. 0 means the motor should not
     * run. Anything between 0 and 1 increases the angle at a slower speed. Anything between -1 and
     * 0 decreases the angle at a slower speed.
     * @param power
     */
    public void setPower(double power) {
        double powerToApply = 0;
        if (!lowerLimitSwitch.isPressed() && !upperLimitSwitch.isPressed()) {
            powerToApply = power;
        }
        // We don't want the angle changer to go down if it is already at the farthest position down.
        if (lowerLimitSwitch.isPressed() && power < 0) {
            powerToApply = 0;
        } else {
            powerToApply = power;
        }
        // We don't want the angle changer to go up if it is already at the farthest position up.
        if (upperLimitSwitch.isPressed() && power > 0) {
            powerToApply = 0;
        } else {
            powerToApply = power;
        }
        motor.setPower(powerToApply);
    }

    public boolean isUpperSwitchPressed() {
        return upperLimitSwitch.isPressed();
    }

    public boolean isLowerSwitchPressed() {
        return lowerLimitSwitch.isPressed();
    }

    @Override
    public String getName() {
        return "Angle Changer";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
        setPower(0);
    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {

    }

    @Override
    public void disableDataLogging() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
