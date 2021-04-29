package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntake;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntakeController;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Color Sensor Test", group = "Test")
//@Disabled
public class ColorSensorTest extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalIntake intake;
    public ElapsedTime timer;
    DataLogging dataLog = null;
    NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {

        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // create a new data log file
       //dataLog = new DataLogging("IntakeController", telemetry);

        // Put your initializations here
        intake = new UltimateGoalIntake(hardwareMap, telemetry);
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
        // give the datalog to the intake
       // intake.setDataLog(dataLog);
        //enable data logging
        //intake.enableDataLogging()

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run :)");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        // reset the clock for the data log to 0
        //dataLog.startTimer();
        intake.requestTurnStage1On();

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.update();
            // Display the current value
            telemetry.addData(">", "Press Stop to terminate.");

            telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
            if (((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM)<6.5){
                intake.requestTurnIntakeOFF();
            }

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
