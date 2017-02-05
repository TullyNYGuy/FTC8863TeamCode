package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusherControl;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Front Beacon Pusher Control", group = "Test")
//@Disabled
public class TestFrontBeaconPusherControl extends LinearOpMode {

    // Put your variable declarations here

    private FrontBeaconPusherControl frontBeaconPusherControl;

    private MuxPlusColorSensors muxPlusColorSensors;

    private FrontBeaconPusherControl.AllianceColor allianceColor = FrontBeaconPusherControl.AllianceColor.RED;

    private FrontBeaconPusherControl.FrontBeaconControlState frontBeaconControlState;

    private boolean gamepad1LeftBumperIsReleased = true;

    private DriveTrain driveTrain;

    @Override
    public void runOpMode() {


        // Put your initializations here

        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.1); // cm
        frontBeaconPusherControl =  new FrontBeaconPusherControl(hardwareMap, telemetry, muxPlusColorSensors, allianceColor, driveTrain);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        frontBeaconPusherControl.initialize();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            if (gamepad1.left_bumper) {
                if (gamepad1LeftBumperIsReleased) {
                    // toggle the drive train mode: differential <-> tank drive
                    frontBeaconPusherControl.startBeaconControl();
                    gamepad1LeftBumperIsReleased = false;
                }
            } else {
                gamepad1LeftBumperIsReleased = true;
            }

            // Put your calls that need to run in a loop here
            frontBeaconControlState = frontBeaconPusherControl.update();

            // Display the current value
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("State = ", frontBeaconControlState.toString());

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
