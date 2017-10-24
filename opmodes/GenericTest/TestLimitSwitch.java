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
@Disabled
public class TestSwitch extends LinearOpMode {

    // Put your variable declarations here
    Switch leftFrontLimitSwitch;
    Switch rightFrontLimitSwitch;
    Switch leftBackLimitSwitch;
    Switch rightBackLimitSwitch;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        leftFrontLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getLeftFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        rightFrontLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRightFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        leftBackLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getLeftBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        rightBackLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRightBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);

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
            leftFrontLimitSwitch.updateSwitch();
            rightFrontLimitSwitch.updateSwitch();
            leftBackLimitSwitch.updateSwitch();
            rightBackLimitSwitch.updateSwitch();

            telemetry.addData("Switch 1 Pressed = ", leftFrontLimitSwitch.isPressed());
            telemetry.addData("Switch 1 Released = ", leftFrontLimitSwitch.isReleased());
            telemetry.addData("Switch 2 Pressed = ", rightFrontLimitSwitch.isPressed());
            telemetry.addData("Switch 2 Released = ", rightFrontLimitSwitch.isReleased());
            telemetry.addData("Switch 3 Pressed = ", leftBackLimitSwitch.isPressed());
            telemetry.addData("Switch 3 Released = ", leftBackLimitSwitch.isReleased());
            telemetry.addData("Switch 4 Pressed = ", rightBackLimitSwitch.isPressed());
            telemetry.addData("Switch 4 Released = ", rightBackLimitSwitch.isReleased());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        timer.reset();

        // Now test just the bumped status. If you do nothing bumped should be false. But press and
        // release the switch and bumped should be true at the end of 5 seconds.
        while(opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here
            leftFrontLimitSwitch.updateSwitch();
            rightFrontLimitSwitch.updateSwitch();
            leftBackLimitSwitch.updateSwitch();
            rightBackLimitSwitch.updateSwitch();
            telemetry.addData(">", "Collecting bumped status for 5 seconds");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("Switch 1 Bumped = ", leftFrontLimitSwitch.isBumped());
        telemetry.addData("Switch 2 Bumped = ", rightFrontLimitSwitch.isBumped());
        telemetry.addData("Switch 3 Bumped = ", leftBackLimitSwitch.isBumped());
        telemetry.addData("Switch 4 Bumped = ", rightBackLimitSwitch.isBumped());
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(2000);

    }
}
