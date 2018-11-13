package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.frogbots.ftcopmodetunercommon.opmode.TunableLinearOpMode;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test OpMode Tuner", group = "Test")
//@Disabled
public class TestOpModeTuner extends TunableLinearOpMode {

    // trying to implement a toggle button
    boolean buttonPress = false;
    // a test field for a double - this works great
    double exampleDouble;

    @Override
    public void runOpMode() {

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        while(opModeIsActive()) {

            exampleDouble = getDouble("servoPosition");
            //onButtonPressEvent("toggle");

            // Display the current values
            telemetry.addData("Double = ", exampleDouble);
            telemetry.addData("button toggle = ", buttonPress);
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
    // not sure how this will get called
    @Override
    public void onButtonPressEvent(String tag){
        switch (tag) {
            case "toggle":
                if (buttonPress) {
                    buttonPress = false;
                } else {
                    buttonPress = true;
                }
        }

    }
}
