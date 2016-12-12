package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Switch", group = "Test")
//@Disabled
public class TestSwitch extends LinearOpMode {

    // Put your variable declarations here
    Switch switch1;
    Switch switch2;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        switch1 = new Switch(hardwareMap, "switch1", Switch.SwitchType.NORMALLY_OPEN);
        switch2 = new Switch(hardwareMap, "switch2", Switch.SwitchType.NORMALLY_OPEN);

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
            switch1.updateSwitch();
            switch2.updateSwitch();

            telemetry.addData("Switch 1 Pressed = ", switch1.isPressed());
            telemetry.addData("Switch 1 Released = ", switch1.isReleased());
            telemetry.addData("Switch 2 Pressed = ", switch2.isPressed());
            telemetry.addData("Switch 2 Released = ", switch2.isReleased());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        timer.reset();

        // Now test just the bumped status. If you do nothing bumped should be false. But press and
        // release the switch and bumped should be true at the end of 5 seconds.
        while(opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here
            switch1.updateSwitch();
            switch2.updateSwitch();
            telemetry.addData(">", "Collecting bumped status for 5 seconds");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("Switch 1 Bumped = ", switch1.isBumped());
        telemetry.addData("Switch 2 Bumped = ", switch2.isBumped());
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(2000);

    }
}
