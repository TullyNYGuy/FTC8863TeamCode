package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.GlyphDumper;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.TestDrivingDistanceUsingIMURunToPosition;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Autonomous Movements", group = "Test")
//@Disabled
public class TestAutonomousMovements extends LinearOpMode {

    // Put your variable declarations here

    public GlyphDumper glyphDumper;
    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;
    public double actualTurnAngle;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.9); // cm

        glyphDumper = new GlyphDumper(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        redNonMatColumnTest2Movements();

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("actual turn angle was ", "%3.2f", actualTurnAngle);
        telemetry.addData(">", "Done");
        telemetry.addData("final Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }

    /**
     * Movements from blue side toward cryptobox that is farthest away from relic zone mats
     */
    public void blueNonMatColumn1Movements() {
        //turn on block
        spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        //drive straight
        driveStraight(-75, 0.1);
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-4, 0.1);
        sleep(1500);
        driveStraight(5, 0.1);
    }

    public void blueNonMatColumn2Movements() {
        //turn on block
        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        spinTurn(-18.5, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        //drive straight
        driveStraight(-75, 0.1);
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-5, 0.1);
        sleep(1500);
        driveStraight(6, 0.1);
    }

    public void blueNonMatColumn3Movements() {
        //turn on block
        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        spinTurn(-26.5, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        //drive straight
        driveStraight(-85, 0.1);
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-5, 0.1);
        sleep(1500);
        driveStraight(6, 0.1);
    }

    public void redNonMatColumn1Movements() {
        //turn on block
        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        spinTurn(4.3, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = driveTrain.imu.getHeading();
        //drive straight
        driveStraight(-65, 0.1);
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-10, 0.1);
        sleep(1500);
        driveStraight(15, 0.1);
    }

    public void redNonMatColumnTestMovements() {
        //turn on block
        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveStraight(-67, 0.1);
        spinTurn(10, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = driveTrain.imu.getHeading();
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-15,0.1);
        driveStraight(10, 0.1);
        //drive straight
        //driveStraight(-65, 0.1);
        //glyphDumper.dump();
        //sleep(1000);
        //glyphDumper.goHome();
        //driveStraight(-10, 0.1);
        //sleep(1500);
        //driveStraight(15, 0.1);
    }

    public void redNonMatColumnTest2Movements() {
        //turn on block
        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveStraight(-55, 0.1);
        spinTurn(27, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = driveTrain.imu.getHeading();
        glyphDumper.dump();
        sleep(1000);
        glyphDumper.goHome();
        driveStraight(-30,0.1);
    }

    public void driveStraight(double distance, double power) {
        driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive()) {
            statusDrive = driveTrain.updateDriveDistance();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Status = ", statusDrive.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.addData("Status = ", statusDrive.toString());
        telemetry.update();
    }

    public void spinTurn(double angle, double power, AdafruitIMU8863.AngleMode angleMode) {
        driveTrain.setupTurn(angle, power, angleMode);

        while (opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        driveTrain.stopTurn();
        telemetry.addData("Turn Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
    }

    public void driveDistanceUsingIMU(double heading, double power, double distance) {
        driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 2000);

        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
        sleep(1000);

        while (opModeIsActive()) {
            boolean isDestinationReached = driveTrain.updateDriveDistanceUsingIMU();
            //boolean isDestinationReached = true;
            if (isDestinationReached) {
                //driveTrain.stopDriveDistanceUsingIMU();
                break;
            }

//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.addData("Heading = ", driveTrain.imu.getHeading());
//            telemetry.update();
            idle();
        }
    }

}
