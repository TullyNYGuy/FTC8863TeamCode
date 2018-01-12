package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.GlyphDumper;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.ReadPictograph;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.RelicRecoveryRobotStJohnFisher;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.TestDrivingDistanceUsingIMURunToPosition;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Test Autonomous Movements", group = "Test")
//@Disabled
public class AutonomousMethods extends LinearOpMode {

    // Put your variable declarations here

    public enum StartPosition {
        AWAY_FROM_MAT,
        NEAR_MAT
    }

    public RelicRecoveryRobotStJohnFisher robot;

    StartPosition startPosition;
    AllianceColor.TeamColor teamColor;

    double correction;
    DriveTrain.Status statusDrive;
    public double actualTurnAngle;
    ReadPictograph readPictograph;
    RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;
    ElapsedTime timeToRead;


    @Override
    public void runOpMode() {
        // Put your initializations here
        createRobot();
        readPictograph = new ReadPictograph(hardwareMap, telemetry);
        timeToRead = new ElapsedTime();

        // Wait for the start button
        telemetry.addData("Alliance = ", teamColor.toString());
        telemetry.addData("Position = ", startPosition.toString());
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        readPictograph.runAtStart();

        timeToRead.reset();
        while (opModeIsActive() && vuMark == RelicRecoveryVuMark.UNKNOWN) {
            vuMark = readPictograph.getvuMark();
        }
        telemetry.addData("stopwatch =", "%5.2f", timeToRead.milliseconds());
        telemetry.addData("vumark =", vuMark.toString());
        telemetry.update();
        sleep(4000);


        doAutonomousMovements(startPosition, teamColor, vuMark);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("actual turn angle was ", "%3.2f", actualTurnAngle);
        telemetry.addData(">", "Done");
        telemetry.addData("final Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        robot.shutdown();
        sleep(3000);
    }

    public void createRobot() {
        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        robot = robot.createRobotForAutonomous(hardwareMap, telemetry);
    }

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - BLUE
    //**********************************************************************************************

    /**
     * Movements from blue side toward cryptobox that is farthest away from relic zone mats
     */
    public void blueNonMatColumn1Movements() {
        driveStraight(-67, 0.1);
        spinTurn(-22.5, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-13, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 1 ", "BLUE");
    }

    public void blueNonMatColumn2Movements() {
        driveStraight(-67, 0.1);
        spinTurn(-40, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-20, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 2 ", "BLUE");
    }

    public void blueNonMatColumn3Movements() {
        driveStraight(-67, 0.1);
        spinTurn(-56, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-32, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 3 ", "BLUE");
    }

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - RED
    //**********************************************************************************************

    public void redNonMatColumn1Movements() {
        telemetry.addData("Aiming for column 1 ", "RED");
        telemetry.update();
        //sleep(2000);
        driveStraight(67, 0.1);
        spinTurn(-162.7, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        //driveStraight(-15,0.1);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-10, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
    }

    public void redNonMatColumn2Movements() {
        telemetry.addData("Aiming for column 2 ", "RED");
        telemetry.update();
        //sleep(2000);
        driveStraight(67, 0.1);
        spinTurn(-140, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-10, 0.1);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-8, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 2 ", "RED");
    }

    public void redNonMatColumn3Movements() {
        telemetry.addData("Aiming for column 3 ", "RED");
        telemetry.update();
        //sleep(2000);
        driveStraight(60, 0.1);
        spinTurn(-130.6, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-30, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-5, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 3 ", "RED");
    }

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - GENERIC METHODS
    //**********************************************************************************************

    public void driveStraight(double distance, double power) {
        robot.driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive()) {
            statusDrive = robot.driveTrain.updateDriveDistance();
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
        robot.driveTrain.setupTurn(angle, power, angleMode);

        while (opModeIsActive() && !robot.driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        robot.driveTrain.stopTurn();
        telemetry.addData("Turn Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
    }

    public void driveDistanceUsingIMU(double heading, double power, double distance) {
        robot.driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 2000);

        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
        sleep(1000);

        while (opModeIsActive()) {
            boolean isDestinationReached = robot.driveTrain.updateDriveDistanceUsingIMU();
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

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - CONTROL AND SWITCHING
    //**********************************************************************************************

    public void doAutonomousMovements(StartPosition startPosition, AllianceColor.TeamColor teamColor, RelicRecoveryVuMark vuMark) {
        telemetry.addData("Starting Autonomous Movements", "!");
        telemetry.update();
        switch (teamColor){
            case RED:
                switch (startPosition) {
                    case NEAR_MAT:
                        switch (vuMark) {
                            case LEFT:
                                break;
                            case CENTER: case UNKNOWN:
                                break;
                            case RIGHT:
                                break;
                        }
                        break;
                    case AWAY_FROM_MAT:
                        switch (vuMark) {
                            case LEFT:
                                redNonMatColumn1Movements();
                                break;
                            case CENTER: case UNKNOWN:
                                redNonMatColumn2Movements();
                                break;
                            case RIGHT:
                                redNonMatColumn3Movements();
                                break;
                        }
                        break;
                }
                break;
            case BLUE:
                switch (startPosition) {
                    case NEAR_MAT:
                        switch (vuMark) {
                            case LEFT:
                                break;
                            case CENTER: case UNKNOWN:
                                break;
                            case RIGHT:
                                break;
                        }
                        break;
                    case AWAY_FROM_MAT:
                        switch (vuMark) {
                            case LEFT:
                                blueNonMatColumn3Movements();
                                break;
                            case CENTER: case UNKNOWN:
                                blueNonMatColumn2Movements();
                                break;
                            case RIGHT:
                                blueNonMatColumn1Movements();
                                break;
                        }
                        break;
                }
                break;
        }

//        switch (vuMark) {
//            case LEFT:
//                switch (teamColor) {
//                    case RED:
//                        switch (startPosition) {
//                            case AWAY_FROM_MAT:
//                                redNonMatColum3Movements();
//                                break;
//                            case NEAR_MAT:
//
//                                break;
//                        }
//                        break;
//                    case BLUE:
//                        switch (startPosition) {
//                            case AWAY_FROM_MAT:
//                                break;
//                            case NEAR_MAT:
//                                break;
//                        }
//                        break;
//                }
//                switch (startPosition) {
//                    case RED_MAT:
//                        //do nothing
//                        break;
//                    case BLUE_MAT:
//                        //do nothing
//                        break;
//                    case RED_NO_MAT:
//                        redNonMatColumnTest3Movements();
//                        break;
//                    case BLUE_NO_MAT:
//                        blueNonMatColumn1Movements();
//                        break;
//                }
//                break;
//            case RIGHT:
//                switch (startPosition) {
//                    case RED_MAT:
//                        //do nothing
//                        break;
//                    case BLUE_MAT:
//                        //do nothing
//                        break;
//                    case RED_NO_MAT:
//                        redNonMatColumn1TestMovements();
//                        break;
//                    case BLUE_NO_MAT:
//                        blueNonMatColumn3Movements();
//                        break;
//                }
//                break;
//            case CENTER:
//                switch (startPosition) {
//                    case RED_MAT:
//                        //do nothing
//                        break;
//                    case BLUE_MAT:
//                        //do nothing
//                        break;
//                    case RED_NO_MAT:
//                        redNonMatColumnTest2Movements();
//                        break;
//                    case BLUE_NO_MAT:
//                        blueNonMatColumn2Movements();
//                        break;
//                }
//                break;
//            case UNKNOWN:
//                switch (startPosition) {
//                    case RED_MAT:
//                        //do nothing
//                        break;
//                    case BLUE_MAT:
//                        //do nothing
//                        break;
//                    case RED_NO_MAT:
//                        redNonMatColumnTest2Movements();
//                        break;
//                    case BLUE_NO_MAT:
//                        blueNonMatColumn2Movements();
//                        break;
//                }
//                break;
//        }
//        telemetry.addData("Ending Switch statements", "!");
//        telemetry.update();
//        sleep(2000);
    }

    /**
     * Pass in the start position and teamcolor from an autonomous picker opmode
     *
     * @param startPosition
     * @param teamColor
     */
    public void setPositionsAndColor(StartPosition startPosition, AllianceColor.TeamColor teamColor) {
        this.startPosition = startPosition;
        this.teamColor = teamColor;
    }
}