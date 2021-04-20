package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorCurrentVoltageMonitor;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
//import org.openftc.revextensions2.ExpansionHubEx;

/**
 * This Opmode runs each drive train motor. It starts with the front left and proceeds in a clockwise
 * order (as viewed from the top of the robot). This allows you to verify the wiring and the forward
 * or reverse settings are correct. All motor should spin toward the front of the robot, as if it
 * were traveling forward.
 */
@TeleOp(name = "Test Drive Motor Directions", group = "Diagnostics")
//@Disabled
public class TestDriveMotorDirections extends LinearOpMode {
    private Configuration config = new Configuration();
    private boolean configLoaded = false;


    private boolean loadConfiguration() {
        configLoaded = false;
        config.clear();
        configLoaded = config.load();
        return configLoaded;
    }
    DcMotor8863 frontLeft;
    DcMotor8863 frontRight;
    DcMotor8863 backLeft;
    DcMotor8863 backRight;

    //ExpansionHubEx expansionHubPrimary;
    //ExpansionHubEx expansionHubSecondary;

    MotorCurrentVoltageMonitor motorCurrentVoltageMonitor;

    @Override
    public void runOpMode() {

        // these method calls require the installation of the RevExtensions2 package
        // https://github.com/OpenFTC/RevExtensions2

        //motorCurrentVoltageMonitor = new MotorCurrentVoltageMonitor(hardwareMap, telemetry, "Expansion Hub 2", MotorCurrentVoltageMonitor.OutputTo.WRITE_CSV_FILE_AND_DISPLAY);

        ElapsedTime timer = new ElapsedTime();

        // create the motor objects
        DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, "FLMotor", hardwareMap);
        DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, "BLMotor", hardwareMap);
        DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, "FRMotor", hardwareMap);
        DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, "BRMotor", hardwareMap);

        frontLeft.runAtConstantPower(0);

        frontRight.runAtConstantPower(0);

        backLeft.runAtConstantPower(0);

        backRight.runAtConstantPower(0);


        // setDirection() is a software control that controls which direction the motor moves when
        // you give it a positive power. We may have to change this once we see which direction the
        // motor actually moves.


        //motorCurrentVoltageMonitor.addMotor(frontLeft);
        //motorCurrentVoltageMonitor.addMotor(frontRight);
        //motorCurrentVoltageMonitor.addMotor(backRight);
        //motorCurrentVoltageMonitor.addMotor(backLeft);

        //motorCurrentVoltageMonitor.setupCSVDataFile("DriveMotorCurrents");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // I have not figured out how to get the expansion hub name for a given motor name yet. So I
        // have hardwired the expansion hub when I get the current draw and expansion hub voltage.

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            frontLeft.setPower(1.0);
            //motorCurrentVoltageMonitor.update();
            telemetry.addData("Front Left running ", "forward");
            telemetry.update();
            idle();
        }

        frontLeft.setPower(0);
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            frontRight.setPower(1.0);
            //motorCurrentVoltageMonitor.update();
            telemetry.addData("Front Right running", "forward");
            telemetry.update();
            idle();
        }

        frontRight.setPower(0);
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            backRight.setPower(1.0);
            //motorCurrentVoltageMonitor.update();
            telemetry.addData("Back Right running", "forward");
            telemetry.update();
            idle();
        }

        backRight.setPower(0);
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            backLeft.setPower(1.0);
            //motorCurrentVoltageMonitor.update();
            telemetry.addData("Back Left running", "forward");
            telemetry.update();
            idle();
        }

        backLeft.setPower(0);
        //motorCurrentVoltageMonitor.closeCSVData();

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}