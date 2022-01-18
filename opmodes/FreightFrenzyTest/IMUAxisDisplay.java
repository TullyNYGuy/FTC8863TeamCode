package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;

@TeleOp(name = "IMU axis display", group = "Test")
//@Disabled
public class IMUAxisDisplay extends LinearOpMode {

    // Put your variable declarations her
    public Configuration config = null;
    
    DataLogging dataLog = null;

    @Override
    public void runOpMode() {


        // Put your initializations here
        dataLog = new DataLogging("imu heading display", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        FreightFrenzyRobotRoadRunner robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM,this );
        // Wait for the start button
        robot.createRobot();
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            robot.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData("heading number - ", robot.mecanum.getExternalHeading());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
