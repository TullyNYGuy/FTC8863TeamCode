package org.firstinspires.ftc.teamcode.opmodes.LauncherBotTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotAngleChanger;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotDualMotorGearBox;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotGamepad;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotRobot;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotShooterServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Launcher Bot angle changer test", group = "Test")
//@Disabled
public class TestLauncherBotAngleChanger extends LinearOpMode {

    // Put your variable declarations here
    public LauncherBotAngleChanger angleChanger;
    public LauncherBotGamepad gamepad;
    public LauncherBotRobot robot;
    public Configuration config = null;
    DataLogging dataLog = null;

    @Override
    public void runOpMode() {


        // Put your initializations here
        angleChanger = new LauncherBotAngleChanger(hardwareMap, telemetry);
        robot = new LauncherBotRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        // create the robot and run the init for it
        robot.createRobot();
        gamepad = new LauncherBotGamepad(gamepad1, gamepad2, robot);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            gamepad.update();
            // Put your calls that need to run in a loop here
            angleChanger.setPower(gamepad.gamepad2RightJoyStickYValue * -1);

            // Display the current value
            gamepad.displayGamepad2JoystickValues(telemetry);
            telemetry.addData("Power requested = ", gamepad.gamepad2RightJoyStickYValue * -1);
            telemetry.addData("Upper limit switch pressed = ", angleChanger.isUpperSwitchPressed());
            telemetry.addData("Lower limit switch pressed = ", angleChanger.isLowerSwitchPressed());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
    }
}
