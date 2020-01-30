package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

import java.io.IOException;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Simple test", group = "Run")
//@Disabled
public class AutonomousTest extends LinearOpMode {

    // Put your variable declarations here

    @Override
    public void runOpMode() {
        Configuration config = new Configuration();
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SkystoneRobot robot = new SkystoneRobot(hardwareMap, telemetry, config, DistanceUnit.CM);
        if (robot.initialize() == false) {
            telemetry.addData("the initialization", " failed");
            telemetry.update();
            stop();
            return;
        }
        AutonomousController controller = new AutonomousController(robot, telemetry);


        waitForStart();
        controller.startController();
        controller.moveTo(DistanceUnit.CM, 100, 0);

        // MecanumCommands commands = new MecanumCommands();
        // commands.setSpeed(.3);
        // commands.setAngleOfTranslation(AngleUnit.RADIANS, 0);
        // commands.setSpeedOfRotation(0);
        //robot.setMovement(commands);


        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.milliseconds() < 60000) {

            idle();
        }
        controller.stopController();
        stop();
        // Put your calls here - they will not run in a loop

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}


