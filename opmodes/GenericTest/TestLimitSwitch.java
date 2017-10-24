package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Limit Switch", group = "Test")
//@Disabled
public class TestLimitSwitch extends LinearOpMode {

    // Put your variable declarations here
    Switch limitSwitch;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        limitSwitch = new Switch(hardwareMap, "limitSwitch", Switch.SwitchType.NORMALLY_OPEN);

        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        // for the first 10 seconds test the pressed and released methods.
        // Since these clear the bumped status, I can't test that here
        while(opModeIsActive() && timer.milliseconds() < 10000) {

            // Put your calls that need to run in a loop here
            limitSwitch.updateSwitch();
            
            telemetry.addData("Switch 1 Pressed = ", limitSwitch.isPressed());
            telemetry.addData("Switch 1 Released = ", limitSwitch.isReleased());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }
    }
}
