package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import android.provider.ContactsContract;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
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
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
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

    public enum ExeJewel {
        JEWEL,
        NO_JEWEL
    }

    public RelicRecoveryRobotStJohnFisher robot;

    StartPosition startPosition;
    AllianceColor.TeamColor teamColor;
    ExeJewel exeJewel;

    DataLogging dataLog;

    double correction;
    DriveTrain.Status statusDrive;
    public double actualTurnAngle;
    ReadPictograph readPictograph;
    RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;
    ElapsedTime timeToRead;

    ModernRoboticsI2cRangeSensor rangeSensor;

    @Override
    public void runOpMode() {
        // Put your initializations here
        dataLog = new DataLogging("Autonomous", telemetry);

        createRobot(teamColor, dataLog);

        readPictograph = new ReadPictograph(hardwareMap, telemetry);
        timeToRead = new ElapsedTime();
        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "distanceSensor");

        // Wait for the start button
        telemetry.addData("Alliance = ", teamColor.toString());
        telemetry.addData("Position = ", startPosition.toString());
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        readPictograph.runAtStart();

        telemetry.addData("cm", "%.2f cm", getAverageDistance());

        timeToRead.reset();
        while (opModeIsActive() && vuMark == RelicRecoveryVuMark.UNKNOWN && timeToRead.milliseconds() < 1500) {
            vuMark = readPictograph.getvuMark();
        }
        //telemetry.addData("stopwatch =", "%5.2f", timeToRead.milliseconds());
        telemetry.addData("vumark =", vuMark.toString());
        telemetry.update();
        sleep(2000);

        if (exeJewel == ExeJewel.JEWEL) {
            telemetry.addData("running jewel", "!");
            telemetry.update();
            sleep(2000);
            while(opModeIsActive() && !robot.jewelArm.updateKnockJewelOff()) {
                idle();
            }
        }

        doAutonomousMovements(startPosition, teamColor, vuMark);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("actual turn angle was ", "%3.2f", actualTurnAngle);
        telemetry.addData(">", "Done");
        telemetry.addData("final Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        dataLog.closeDataLog();
        robot.shutdown();
        //sleep(3000);
    }

    public void createRobot(AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        robot = robot.createRobotForAutonomous(hardwareMap, telemetry, teamColor, dataLog);
    }

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - BLUE
    //**********************************************************************************************

    /**
     * Movements from blue side toward cryptobox that is farthest away from relic zone mats
     */
    public void blueNonMatColumn1Movements() {
        driveStraight(-67, 0.2);
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
        driveStraight(-67, 0.2);
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
        driveStraight(-67, 0.2);
        spinTurn(-56, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-32, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 3 ", "BLUE");
    }

    public void blueMatColumn1Movements() {
        driveStraight(-61.0, 0.2);
        spinTurn(73.75, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-8.00, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-9, 0.1);
        driveStraight(5, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
    }

    public void blueMatColumn2Movements() {
        driveStraight(-61.0, 0.2);
        spinTurn(57, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-19.5, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-8, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 2 ", "RED");
    }

    public void blueMatColumn3Movements() {
        driveStraight(-61.0, 0.2);
        spinTurn(42, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-33, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-8, 0.1);
        driveStraight(8, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
    }

    //**********************************************************************************************
    // AUTONOMOUS MOVEMENTS - RED
    //**********************************************************************************************

    public void redNonMatColumn1Movements() {
        telemetry.addData("Aiming for column 1 ", "RED");
        telemetry.update();
        //sleep(2000);
        driveStraight(67, 0.2);
        spinTurn(-162.7, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        //driveStraight(-15,0.1);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-10, 0.2);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
    }

    public void redNonMatColumn2Movements() {
        telemetry.addData("Aiming for column 2 ", "RED");
        telemetry.update();
        //sleep(2000);
        driveStraight(67, 0.2);
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
        driveStraight(60, 0.2);
        spinTurn(-130.6, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-30, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-5, 0.2);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 3 ", "RED");
    }

    public void redMatColumn1Movements() {
        driveStraight(61.0, 0.2);
        spinTurn(106.25, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-8.00, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-9, 0.1);
        driveStraight(5, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
    }

    public void redMatColumn2Movements() {
        driveStraight(61.0, 0.2);
        spinTurn(123.00, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-19.5, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-8, 0.1);
        driveStraight(10, 0.1);
        telemetry.addData("Aiming for column 2 ", "RED");
    }

    public void redMatColumn3Movements() {
        driveStraight(61.0, 0.2);
        spinTurn(138, 0.2, AdafruitIMU8863.AngleMode.ABSOLUTE);
        actualTurnAngle = robot.driveTrain.imu.getHeading();
        driveStraight(-33, 0.2);
        robot.glyphDumper.dump();
        sleep(1000);
        robot.glyphDumper.goHome();
        driveStraight(-8, 0.1);
        driveStraight(8, 0.1);
        telemetry.addData("Aiming for column 1 ", "RED");
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
                                redMatColumn3Movements();
                                break;
                            case CENTER: case UNKNOWN:
                                redMatColumn2Movements();
                                break;
                            case RIGHT:
                                redMatColumn1Movements();
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
                                blueMatColumn1Movements();
                                break;
                            case CENTER: case UNKNOWN:
                                blueMatColumn2Movements();
                                break;
                            case RIGHT:
                                blueMatColumn3Movements();
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
    public void  setPositionsAndColorAndJewel(StartPosition startPosition, AllianceColor.TeamColor teamColor, ExeJewel exeJewel) {
        this.startPosition = startPosition;
        this.teamColor = teamColor;
        this.exeJewel = exeJewel;
    }
    public double getAverageDistance(){
        double distanceOne;
        double distanceTwo;
        double distanceThree;
        double average;
        distanceOne = rangeSensor.getDistance(DistanceUnit.CM);
        sleep(100);
        distanceTwo = rangeSensor.getDistance(DistanceUnit.CM);
        sleep(100);
        distanceThree = rangeSensor.getDistance(DistanceUnit.CM);
        average = (distanceOne+distanceTwo+distanceThree)/3;
        return average;
    }
}
