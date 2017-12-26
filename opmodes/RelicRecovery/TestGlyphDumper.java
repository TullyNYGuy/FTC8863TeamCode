package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.GlyphDumper;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Glyph Dumper", group = "Test")
//@Disabled
public class TestGlyphDumper extends LinearOpMode {

    // Put your variable declarations here

    public GlyphDumper glyphDumper;

    @Override
    public void runOpMode() {


        // Put your initializations here

        glyphDumper = new GlyphDumper(hardwareMap, telemetry);
        glyphDumper.init();
        sleep(5000);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        glyphDumper.dump();
        sleep(5000);
        glyphDumper.goHome();
        sleep(5000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(3000);
    }
}
