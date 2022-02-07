package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFExtensionArm;

/**
 * This Opmode allows the user to control the movement of the extension arm by changing constants
 * that appear in the FTC Dashboard. The positions of the extension arm and delivery box servo
 * can be changed on the fly without having to recompile and download.
 *
 * My thought is that this could be extended to control the servo position. Then the opmode can be
 * run and the proper combinations of extension arm position and servo position can be quickly found
 * to result in a sequence of extension positions and servo positions that allow the arm to extend
 * or retract.
 *
 * They might have to be tweaked once the arm is moving at full speed rather than the stop and go it
 * will be doing here. But I think it will get us closer to the proper sequence fast.
 *
 * You will have to record the extension and servo positions and manually transfer them to the actual
 * state machine code for the delivery.
 */
// THE LINE BELOW TELLS THE FTC DASHBOARD TO LOOK AT THIS OPMODE AND MAKE ANY public static variables
// appear in the dashboard so you can change them on the fly:
@Config
@TeleOp(name = "Find Extension Arm Positions", group = "Test")
//@Disabled
public class FindExtensionArmPositions extends LinearOpMode {

    // Put your variable declarations her
    FFExtensionArm delivery;
    ElapsedTime timer;

    // These public static constants can be changed in the FTC Dashboard in real time. Make any
    // constant public static and it appears on the dashboard and can be changed on the fly.
    public static double EXTENSION_ARM_POSITION = 0;
    public static double EXTENSION_ARM_POWER = 0.2;

    double lastArmPosition = EXTENSION_ARM_POSITION;

    @Override
    public void runOpMode() {

        // Put your initializations here
        delivery = new FFExtensionArm(hardwareMap, telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Init the delivery extension arm and servo
        delivery.init();
        while (opModeIsActive() && !delivery.isInitComplete()) {
            telemetry.addData("Initing ...", ".");
            telemetry.update();
            idle();
        }
        telemetry.addData("Init complete", ".");
        telemetry.update();

        // display the init complete for a bit so the user has a chance to see it
        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }

        // now loop. Check to see if the constant has been updated from the FTC Dashboard and if so
        // output the new position.
        while (opModeIsActive()) {
            // is there a change in the position?
            if (EXTENSION_ARM_POSITION != lastArmPosition) {
                // yup
                // update the last position to the new one so we can use it later
                lastArmPosition = EXTENSION_ARM_POSITION;
                delivery.extendToPosition(EXTENSION_ARM_POSITION, EXTENSION_ARM_POWER);
                telemetry.addData("Extending to position = ", EXTENSION_ARM_POSITION);
            }
            if (delivery.isExtensionMovementComplete()) {
                telemetry.addData("Extension position = ", EXTENSION_ARM_POSITION);
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
