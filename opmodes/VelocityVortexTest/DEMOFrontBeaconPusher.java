package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "DEMO Front Beacon Pusher", group = "Test")
@Disabled
public class DEMOFrontBeaconPusher extends LinearOpMode {

    // Put your variable declarations here
    FrontBeaconPusher frontBeaconPusher;
    MuxPlusColorSensors muxPlusColorSensors;

    FrontBeaconPusher.BeaconPusherState frontBeaconPusherState;

    ElapsedTime timer;

    public void runOpMode() {


        // Put your initializations here
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        timer = new ElapsedTime();

        frontBeaconPusherState = frontBeaconPusher.findBeaconPusherState();
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusherState.toString());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        while (opModeIsActive()) {
            if (frontBeaconPusher.leftCRServo.frontSwitch.isPressed()) {
                timer.reset();
                frontBeaconPusher.moveBothMidway();
                while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_MIDDLE) {
                    frontBeaconPusherState = frontBeaconPusher.updateState();
                    telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
                    telemetry.update();
                    idle();
                }
                telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
                telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
                telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
                telemetry.update();

                timer.reset();
                frontBeaconPusher.moveLeftPusherBackRightPusherForward();
                while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.LEFT_BACK_RIGHT_FORWARD) {
                    frontBeaconPusherState = frontBeaconPusher.updateState();
                    telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
                    telemetry.update();
                    idle();
                }
                sleep(3000);
                frontBeaconPusher.moveBothPushersBack();
                //NEED TO check to see if using frontBeaconPusherState will cause bugs when it is not known
                while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_BACK) {
                    frontBeaconPusherState = frontBeaconPusher.updateState();
                    telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
                    telemetry.update();
                    idle();
                }
            }
            frontBeaconPusher.leftCRServo.frontSwitch.updateSwitch();
        }
    }
}