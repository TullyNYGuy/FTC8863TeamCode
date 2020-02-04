package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MeasureVelocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
import org.openftc.revextensions2.ExpansionHubEx;

import java.util.concurrent.TimeUnit;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Expansion hub voltage drop", group = "Test")
//@Disabled
public class TestRevVoltageMonitors extends LinearOpMode {

    // Put your variable declarations her
    ExpansionHubEx expansionHubPrimary;
    ExpansionHubEx expansionHubSecondary;

    CSVDataFile csvDataFile;

    DcMotor8863 frontLeft;
    DcMotor8863 frontRight;
    DcMotor8863 backLeft;
    DcMotor8863 backRight;

    MeasureVelocity measureVelocity;

    @Override
    public void runOpMode() {


        // Put your initializations here
        expansionHubPrimary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1");
        expansionHubSecondary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");

        measureVelocity = new MeasureVelocity();

        double odometryModuleRightValue = 0;
        double odometryModuleBackValue = 0;
        double odometryModuleLeftValue = 0;
        boolean distanceMeasured = false;
        boolean started = false;
        double averageDistance = 0;
        double velocity = 0;
        double travelTime;
        double travelTimeStart = 0;
        double travelTimeEnd = 0;

        OdometryModule odometryModuleRight;
        OdometryModule odometryModuleBack;
        OdometryModule odometryModuleLeft;

        csvDataFile = new CSVDataFile("RevVoltageCurrentMonitors", telemetry);
        csvDataFile.headerStrings("Primary Voltage", "Secondary Voltage", "Primary Current", "Secondary Current");

        ElapsedTime timer = new ElapsedTime();

        frontLeft = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_LEFT_MOTOR.hwName, hardwareMap);
        frontRight = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_RIGHT_MOTOR.hwName, hardwareMap);
        backLeft = new DcMotor8863(SkystoneRobot.HardwareName.BACK_LEFT_MOTOR.hwName, hardwareMap);
        backRight = new DcMotor8863(SkystoneRobot.HardwareName.BACK_RIGHT_MOTOR.hwName, hardwareMap);

        frontLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontLeft.runAtConstantPower(0);
        frontRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontRight.runAtConstantPower(0);
        backLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backLeft.runAtConstantPower(0);
        backRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backRight.runAtConstantPower(0);

        odometryModuleRight = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "BackRight", hardwareMap);
        odometryModuleBack = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "FrontRight", hardwareMap);
        odometryModuleLeft = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "FrontLeft", hardwareMap);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        odometryModuleLeft.resetEncoderValue();
        odometryModuleBack.resetEncoderValue();
        odometryModuleRight.resetEncoderValue();


        while (opModeIsActive()) {
            csvDataFile.writeData(
                    timer.seconds(),
                    expansionHubPrimary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubSecondary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubPrimary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS)),
                    expansionHubPrimary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 0),
                    expansionHubPrimary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 1),
                    expansionHubPrimary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 2),
                    expansionHubPrimary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 3),
                    expansionHubSecondary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS)),
                    expansionHubSecondary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 0),
                    expansionHubSecondary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 1),
                    expansionHubSecondary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 2),
                    expansionHubSecondary.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, 3)
            );


            if (timer.milliseconds() > 2000 && !started) {
                frontLeft.setPower(-1);
                frontRight.setPower(1);
                backLeft.setPower(-1);
                backRight.setPower(1);
                started = true;
                measureVelocity.startMeasure(0, DistanceUnit.CM);
            }

            if (timer.milliseconds() > 4000) {
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
                if (!distanceMeasured) {
                    odometryModuleRightValue = odometryModuleRight.getDistanceSinceReset(DistanceUnit.CM);
                    odometryModuleLeftValue = odometryModuleLeft.getDistanceSinceReset(DistanceUnit.CM);
                    averageDistance = ((odometryModuleLeftValue + odometryModuleRightValue) / 2);
                    measureVelocity.stopMeasure(averageDistance, DistanceUnit.CM);
                    distanceMeasured = true;
                }
                break;
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        csvDataFile.blankLine();
        csvDataFile.headerStrings("left distance", "right distance", "average distance", "travel time", "velocity");
        csvDataFile.writeData(odometryModuleLeftValue, odometryModuleRightValue, averageDistance, measureVelocity.getAcquistionTime(TimeUnit.SECONDS), measureVelocity.getGetAverageVelocity(DistanceUnit.METER));

        // Put your cleanup code here - it runs as the application shuts down
        csvDataFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
