package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test 180 Degree Turn", group = "Test")
//@Disabled
public class Test180DegreeTurn_Arthur extends LinearOpMode {

    // Put your variable declarations here
    public DriveTrain driveTrain;
    public DataLogging turnLog;
    public double desiredTurnAngle = 45;
    public double desiredTurnPower = .3;
    public boolean timerResetFlag = false;

    @Override
    public void runOpMode() {


        // Put your initializations here

        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        turnLog = new DataLogging("turn", telemetry);
        driveTrain.setDataLog(turnLog);
        driveTrain.enableLogTurns();

        // you must press one of these buttons at the same time you press init

        if (gamepad1.right_bumper) desiredTurnPower = .5;
        if (gamepad1.left_bumper) desiredTurnPower = .7;
        if (gamepad1.y) desiredTurnPower = 1;
        if (gamepad1.a) desiredTurnPower = .3;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // you must press one of these buttons at the same time you press play

        // left turn
        if (gamepad1.dpad_up) desiredTurnAngle = 175;
        // right turn
        if (gamepad1.dpad_down) desiredTurnAngle = -175;
        // left turn
        if (gamepad1.dpad_left) desiredTurnAngle = 90;
        // right turn
        if (gamepad1.dpad_right) desiredTurnAngle = -90;
        // left turn
        if (gamepad1.x) desiredTurnAngle = 270;
        // right turn
        if (gamepad1.b) desiredTurnAngle = -270;

        telemetry.addData("Turn angle = ", desiredTurnAngle);
        telemetry.addLine("- is CCW, + is CW");
        telemetry.addData("Turn power = ", desiredTurnPower);
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        driveTrain.setupTurn(desiredTurnAngle, desiredTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            if (!timerResetFlag) {
                driveTrain.resetTurnTimer();
                timerResetFlag = true;
            }
            // Put your calls that need to run in a loop here
            driveTrain.updateTurn();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
