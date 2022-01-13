package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/*
 * This Opmode is a test to determine minimum motor power required to move the robot.
 */
@TeleOp(name = "Test Min Power", group = "ATest")
@Disabled
public class TestMinimumMotorPower extends LinearOpMode {

    class TestRobot implements FTCRobot {

        AdafruitIMU8863 imu;

        public TestRobot(AdafruitIMU8863 imu) {
            this.imu = imu;
        }

        @Override
        public boolean createRobot() {
            return false;
        }

        @Override
        public void init() {

        }

        @Override
        public boolean isInitComplete() {
            return false;
        }

        @Override
        public void update() {

        }

        @Override
        public void shutdown() {

        }

        @Override
        public void timedUpdate(double timerValueMsec) {

        }

        @Override
        public double getCurrentRotation(AngleUnit unit) {
            return unit.fromDegrees(imu.getHeading());
        }
    }


    // Put your variable declarations here
    private Configuration config = new Configuration();
    private boolean configLoaded = false;

    private boolean loadConfiguration() {
        configLoaded = false;
        config.clear();
        configLoaded = config.load();
        return configLoaded;
    }

    private boolean saveConfiguration() {
        return config.store();
    }

    private double getDistance(DistanceUnit units, Position p1, Position p2) {
        Position pp1 = p1.toUnit(units);
        Position pp2 = p2.toUnit(units);
        return Math.sqrt((pp2.x - pp1.x) * (pp2.x - pp1.x) + (pp2.y - pp1.y) * (pp2.y - pp1.y));
    }

    @Override
    public void runOpMode() {


        // Put your initializations here
        loadConfiguration();

        MecanumCommands mecanumCommands = new MecanumCommands();
        boolean intakeState = false;

        DcMotor8863 frontLeft = new DcMotor8863("FrontLeft", hardwareMap);
        DcMotor8863 backLeft = new DcMotor8863("BackLeft", hardwareMap);
        DcMotor8863 frontRight = new DcMotor8863("FrontRight", hardwareMap);
        DcMotor8863 backRight = new DcMotor8863("BackRight", hardwareMap);

        frontLeft.setMotorType(ANDYMARK_20_ORBITAL);
        backLeft.setMotorType(ANDYMARK_20_ORBITAL);
        frontRight.setMotorType(ANDYMARK_20_ORBITAL);
        backRight.setMotorType(ANDYMARK_20_ORBITAL);

        frontLeft.setMovementPerRev(360);
        backLeft.setMovementPerRev(360);
        frontRight.setMovementPerRev(360);
        backRight.setMovementPerRev(360);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);

        frontLeft.runAtConstantPower(0);
        backLeft.runAtConstantPower(0);
        frontRight.runAtConstantPower(0);
        backRight.runAtConstantPower(0);

        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);

        DistanceUnit units = DistanceUnit.CM;
        OdometryModule left = new OdometryModule(1440, 3.8 * Math.PI, units, "FrontLeft", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8 * Math.PI, units, "BackRight", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8 * Math.PI, units, "FrontRight", hardwareMap);
        OdometrySystem odometry = new OdometrySystem(units, left, right, back);
        Position initialPosition = new Position(DistanceUnit.CM, 0.0, 0.0, 0.0, 0);
        Position currentPosition = new Position(DistanceUnit.CM, 0.0, 0.0, 0.0, 0);

        DataLogging logger = new DataLogging("TestMinPower", telemetry);

        if (configLoaded && odometry.loadConfiguration(config)) {
            telemetry.addData("init", "Loaded Odometry configuration");
        } else {
            telemetry.addData("ERROR: ", "Error loading config");
        }


        ElapsedTime timer = new ElapsedTime();
        waitForStart();

        List<Double> lst = new ArrayList<Double>();
        double max = 0;
        for (int i = 0; i < 10; i++) {
            telemetry.addData("Cycle: ", i + 1);
            telemetry.update();
            double motorPower = 0.07;
            do {
                motorPower += .01;
                odometry.calculateMoveDistance();
                odometry.getCurrentPosition(initialPosition);
                odometry.getCurrentPosition(currentPosition);
                //telemetry.addData("motorPower:", String.format("%.2f", motorPower));
                //telemetry.update();
                logger.logData("motorPower:", String.format("%.2f", motorPower));
                frontLeft.setPower(motorPower);
                frontRight.setPower(motorPower);
                backLeft.setPower(motorPower);
                backRight.setPower(motorPower);
                timer.reset();
                do {
                    idle();
                    odometry.calculateMoveDistance();
                    odometry.getCurrentPosition(currentPosition);
                } while (timer.milliseconds() < 3000 && getDistance(DistanceUnit.CM, initialPosition, currentPosition) < 1.0);
            } while (getDistance(DistanceUnit.CM, initialPosition, currentPosition) < 1.0);
            frontLeft.shutDown();
            frontRight.shutDown();
            backLeft.shutDown();
            backRight.shutDown();
            if (max < motorPower)
                max = motorPower;
            lst.add(motorPower);
            telemetry.addData("min power: ", lst);
            telemetry.update();
        }
        telemetry.addData("min power: ", lst);
        telemetry.addData("max min power: ", String.format("%.2f", max));
        telemetry.update();
        timer.reset();
        while (timer.milliseconds() < 20000) {
            idle();
        }
        stop();
    }
}
