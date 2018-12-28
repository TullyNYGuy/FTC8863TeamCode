package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Lift Height Test", group = "Test")
//@Disabled
public class LiftHeightTest extends LinearOpMode {

    // Put your variable declarations here
    public DeliveryLiftSystem deliveryLiftSystem;

    @Override
    public void runOpMode() {


        // Put your initializations here
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);
        deliveryLiftSystem.init();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        //deliveryLiftSystem.moveToPosition(5);
        deliveryLiftSystem.dehang();
//        while (opModeIsActive()&& deliveryLiftSystem.update()!= DcMotor8863.MotorState.COMPLETE_FLOAT) {
//
//            // Put your calls that need to run in a loop here
//
//            // Display the current value
//            telemetry.addData("lift height = ",deliveryLiftSystem.getLiftPosition());
//            telemetry.addData(">", "Press Stop to end test.");
//
//            telemetry.update();
//
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
