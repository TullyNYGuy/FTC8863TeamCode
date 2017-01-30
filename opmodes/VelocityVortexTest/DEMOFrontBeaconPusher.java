package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.VelocityVortex.FrontBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortex.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Front Beacon Pusher Movement", group = "Test")
//@Disabled
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

        // Put your calls here - they will not run in a loop

        // Put your calls that need to run in a loop here
        timer.reset();
        frontBeaconPusher.moveBothPushersBack();
        //NEED TO check to see if using frontBeaconPusherState will cause bugs when it is not known
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_BACK) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
        telemetry.update();
        sleep(3000);

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
        sleep(3000);

        timer.reset();
        frontBeaconPusher.moveLeftPusherBackRightPusherForward();
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.LEFT_BACK_RIGHT_FORWARD) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
        telemetry.update();
        sleep(3000);

        timer.reset();
        frontBeaconPusher.moveBothMidway();
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_MIDDLE) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.update();
        sleep(3000);

        timer.reset();
        frontBeaconPusher.moveLeftPusherForwardRightPusherBack();
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.LEFT_FORWARD_RIGHT_BACK) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.update();
        sleep(3000);

        timer.reset();
        frontBeaconPusher.moveBothPushersBack();
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_BACK) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
        telemetry.update();
        sleep(3000);

        timer.reset();
        frontBeaconPusher.moveBothPushersForward();
        while (opModeIsActive() && frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.BOTH_FORWARD) {
            frontBeaconPusherState = frontBeaconPusher.updateState();
            telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("Beacon pusher state = ", frontBeaconPusherState.toString());
        telemetry.addData("Time to move (Sec) = ", "%2.1f", timer.seconds());
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusher.findBeaconPusherState().toString());
        telemetry.update();
        sleep(3000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
