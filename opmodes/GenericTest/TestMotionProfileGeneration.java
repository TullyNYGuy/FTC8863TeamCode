package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY;

import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Motion Profile Generation", group = "Test")
//@Disabled
public class TestMotionProfileGeneration extends LinearOpMode {

    // Put your variable declarations her
    MotionProfile motionProfile;
    CSVDataFile motionProfileCSV;
    NanoClock clock;
    double timeStart;
    double elapsedTime;

    @Override
    public void runOpMode() {


        // Put your initializations here
        motionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                new MotionState(0, 0, 0, 0),
                new MotionState(35, 0, 0, 0),
                50,
                200
        );

        motionProfileCSV = new CSVDataFile("motionProfile");
        clock = NanoClock.system();

        // Wait for the start button
        telemetry.addData("motion profile start = ", motionProfile.start().getX());
        telemetry.addData("motion profile end = ", motionProfile.end().getX());
        telemetry.addData("motion profile duration = ", motionProfile.duration());
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timeStart = clock.seconds();
        elapsedTime = 0;
        motionProfileCSV.headerStrings("time", "position", "velocity", "acceleration");
        while (opModeIsActive() && elapsedTime < motionProfile.duration()) {
            elapsedTime = clock.seconds()-timeStart;
            MotionState targetState = motionProfile.get(elapsedTime);
            motionProfileCSV.writeData(elapsedTime, targetState.getX(), targetState.getV(), targetState.getA());

            telemetry.addData("recording profile","...");
            telemetry.update();
            idle();
        }

        motionProfileCSV.closeDataLog();

        telemetry.addData("wrote CSV file with profile", ".");
        telemetry.update();
        while(opModeIsActive()){
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
