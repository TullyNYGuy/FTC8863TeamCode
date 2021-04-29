package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntake;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntakeController;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Ultimate Goal Intake Controller Test", group = "Test")
//@Disabled
public class IntakeControllerTest extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalIntakeController controller;
    public UltimateGoalIntake intake;
    public ElapsedTime timer;
    DataLogging dataLog = null;

    @Override
    public void runOpMode() {

        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // create a new data log file
        dataLog = new DataLogging("IntakeController", telemetry);

        // Put your initializations here
        intake = new UltimateGoalIntake(hardwareMap, telemetry);
        // give the datalog to the intake
        intake.setDataLog(dataLog);
        //enable data logging
        intake.enableDataLogging();

        controller = new UltimateGoalIntakeController(hardwareMap, telemetry, intake);
        // give the datalog to the controller
        controller.setDataLog(dataLog);
        // enable data logging
        controller.enableDataLogging();

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run :)");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        // reset the clock for the data log to 0
        dataLog.startTimer();
        controller.requestIntake();

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 15000) {

            // Put your calls that need to run in a loop here
            controller.update();
            // Display the current value
            telemetry.addData(">", "Press Stop to terminate.");
            intake.displaySWitches(telemetry);

            telemetry.update();

            idle();
        }

        controller.requestFire_1();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            controller.update();
            // Display the current value
            telemetry.addData(">", "Press Stop to terminate.");
            intake.displaySWitches(telemetry);

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
