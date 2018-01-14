package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.RelicRecoveryRobotStJohnFisher;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Red Mat Column 2", group = "Run")
@Disabled
public class RedMatColumn2 extends LinearOpMode {

    // Put your variable declarations here

    public enum StartPosition {
        BLUE_MAT,
        BLUE_NO_MAT,
        RED_MAT,
        RED_NO_MAT
    }

    public RelicRecoveryRobotStJohnFisher robot;

    double correction;
    DriveTrain.Status statusDrive;
    public double actualTurnAngle;

    public AdafruitColorSensor8863.ColorFromSensor ballColor;

    DataLogging dataLog = null;

    @Override
    public void runOpMode() {
        // Put your initializations here
        createRobot();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        //ballColor = robot.jewelArm.getBallColorAndKnockOffBall(AllianceColor.TeamColor.RED);
        //telemetry.addData("Ball color = ", ballColor.toString());
        telemetry.update();
        //sleep(500);
        redMatColumn2Movements();

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
        robot = robot.createRobotForAutonomous(hardwareMap, telemetry, AllianceColor.TeamColor.RED, dataLog);
    }

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

//    public void redNonMatColumn1Movements() {
//        //turn on block
//        //spinTurn(-9.0, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
//        spinTurn(4.3, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
//        actualTurnAngle = robot.driveTrain.imu.getHeading();
//        //drive straight
//        driveStraight(-65, 0.1);
//        robot.glyphDumper.dump();
//        sleep(1000);
//        robot.glyphDumper.goHome();
//        driveStraight(-10, 0.1);
//        sleep(1500);
//        driveStraight(15, 0.1);
//    }

    public void redNonMatColumn1TestMovements() {
        telemetry.addData("Aiming for column 1 ", "RED");
        telemetry.update();
        sleep(2000);
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

    public void redNonMatColumnTest2Movements() {
        telemetry.addData("Aiming for column 2 ", "RED");
        telemetry.update();
        sleep(2000);
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

    public void redNonMatColumnTest3Movements() {
        telemetry.addData("Aiming for column 3 ", "RED");
        telemetry.update();
        sleep(2000);
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

    public void redMatColumn2Movements() {
        driveStraight(61.0, 0.1);
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

    public RelicRecoveryVuMark getPictograph() {

        final String TAG = "Vuforia VuMark Sample";
        VuforiaLocalizer vuforia;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AdFJpV3/////AAAAGfTNpwzHLEWLhTazSUptJDdvoaO1q58UH3Ix0gMjIizeGqeRTy/mHyHpZI3hX3VrQk0S4VJKsiwBIUrTZy57oWoQQGsD/6JXnrC/R2zQ2ruhxmV9JYc6zr5Lhu+aUFdce/WJezBkcUv7fD2y6kmNHAWlYyMx3ZP8YX2bSfTWu4PjiO3N/CFelgIJSz5BCRtYeFb1gKkCYhsqKUNfkWXznEFvX8ppW72yjbfq62QwqGFeuql/3cPce8asiOVo9NLiG9mIuADM+FWLairEHQ4h2euGHa+JNrk36EO0zVAFk9G2RBQJRkwgA7jUOGpCEW0Qqt0XJoM+0T0sOVORrn3Lqp9M4ecaQYsQsR8RPoZRL0Ox";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * See if any of the instances of {@link relicTemplate} are currently visible.
         * {@link RelicRecoveryVuMark} is an enum which can have the following values:
         * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
         * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
         */
        VuforiaTrackables relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        relicTrackables.activate();

//        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
//        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
//
//                /* Found an instance of the template. In the actual game, you will probably
//                 * loop until this condition occurs, then move on to act accordingly depending
//                 * on which VuMark was visible. */
//            telemetry.addData("VuMark", "%s visible", vuMark);
//
//        } else {
//            telemetry.addData("VuMark", "not visible");
//        }
//        return vuMark;
        return RelicRecoveryVuMark.UNKNOWN;
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }

    public void getAutonomousMovements(StartPosition startPosition, RelicRecoveryVuMark vuMark) {
        telemetry.addData("Starting Autonomous Movements", "!");
        telemetry.update();
        sleep(2000);
        //RelicRecoveryVuMark vuMark = getPictograph();
        switch (vuMark) {
            case LEFT:
                switch (startPosition) {
                    case RED_MAT:
                        //do nothing
                        break;
                    case BLUE_MAT:
                        //do nothing
                        break;
                    case RED_NO_MAT:
                        redNonMatColumnTest3Movements();
                        break;
                    case BLUE_NO_MAT:
                        blueNonMatColumn1Movements();
                        break;
                }
                break;
            case RIGHT:
                switch (startPosition) {
                    case RED_MAT:
                        //do nothing
                        break;
                    case BLUE_MAT:
                        //do nothing
                        break;
                    case RED_NO_MAT:
                        redNonMatColumn1TestMovements();
                        break;
                    case BLUE_NO_MAT:
                        blueNonMatColumn3Movements();
                        break;
                }
                break;
            case CENTER:
                switch (startPosition) {
                    case RED_MAT:
                        //do nothing
                        break;
                    case BLUE_MAT:
                        //do nothing
                        break;
                    case RED_NO_MAT:
                        redNonMatColumnTest2Movements();
                        break;
                    case BLUE_NO_MAT:
                        blueNonMatColumn2Movements();
                        break;
                }
                break;
            case UNKNOWN:
                switch (startPosition) {
                    case RED_MAT:
                        //do nothing
                        break;
                    case BLUE_MAT:
                        //do nothing
                        break;
                    case RED_NO_MAT:
                        redNonMatColumnTest2Movements();
                        break;
                    case BLUE_NO_MAT:
                        blueNonMatColumn2Movements();
                        break;
                }
                break;
        }
        telemetry.addData("Ending Switch statements", "!");
        telemetry.update();
        sleep(2000);
    }
}
