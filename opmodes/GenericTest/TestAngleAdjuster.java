package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AngleAdjuster;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Angle Adjuster", group = "Test")
@Disabled
public class TestAngleAdjuster extends LinearOpMode {

    // Put your variable declarations here
    AngleAdjuster angleAdjuster;
    DataLogging logfile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        angleAdjuster = new AngleAdjuster();
        logfile = new DataLogging("AngleAdjuster", telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loopTemplateLinearOpModeTemplateLinearOpModeTemplateLinearOpMode
        angleAdjuster.setTarget(135, 90);
        logfile.logData("threshold=90 " + "input angle=45 " + "adjusted angle= " + angleAdjuster.adjustAngle(45));
        logfile.logData("threshold=90 " + "input angle=135 " + "adjusted angle= " + angleAdjuster.adjustAngle(135));
        logfile.logData("threshold=90 " + "input angle=-45 " + "adjusted angle= " + angleAdjuster.adjustAngle(-45));
        logfile.logData("threshold=90 " + "input angle=-135 " + "adjusted angle= " + angleAdjuster.adjustAngle(-135));

        angleAdjuster.setTarget(-135, -90);
        logfile.logData("threshold=90 " + "input angle=45 " + "adjusted angle= " + angleAdjuster.adjustAngle(45));
        logfile.logData("threshold=90 " + "input angle=135 " + "adjusted angle= " + angleAdjuster.adjustAngle(135));
        logfile.logData("threshold=90 " + "input angle=-45 " + "adjusted angle= " + angleAdjuster.adjustAngle(-45));
        logfile.logData("threshold=90 " + "input angle=-135 " + "adjusted angle= " + angleAdjuster.adjustAngle(-135));

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
