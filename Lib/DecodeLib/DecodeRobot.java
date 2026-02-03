package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DecodeRobot implements FTCRobot {

    public enum HardwareName {
        IMU("imu"),

        FRONT_LEFT_DRIVE_MOTOR("leftFrontMotor"),
        FRONT_RIGHT_DRIVE_MOTOR("rightFrontMotor"),
        REAR_LEFT_DRIVE_MOTOR("leftRearMotor"),
        REAR_RIGHT_DRIVE_MOTOR("rightRearMotor"),
        MECANUM_DRIVE("mecanumDrive"),
        LIMELIGHT("limelight"),
        HOOD_SERVO("hoodServo"),
        RAMP_SERVO("rampServo"),
        BALL_SHOOTER("ballShooter"),
        SHOOTER_MOTOR("shooterMotor"),
        COLOR_SENSOR_RIGHT("colorSensorRight"),
        COLOR_SENSOR_LEFT("colorSensorLeft"),
        COLOR_SENSOR_CONTROLLER("colorSensorController"),
        INTAKE_MOTOR("intakeMotor"),
        SORTER_CONTROLLER("sorterController"),
        SORTER_MOTOR("sorterMotor"),
        TURNTABLE_MOTOR("turntableMotor"),
        INDICATOR_LIGHT("indicatorLight");

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    // to remove a subsystem, comment it out below. Then comment out the creation of the object (new statement)
    // down a little ways
    public enum Subsystem {
        IMU,
        MECANUM_DRIVE,
        TURNTABLE_MOTOR,
        BALL_SHOOTER, // BALL SHOOTER IS MADE UP OF:
        SHOOTER_MOTOR,
        HOOD_SERVO,
        // SORTER CONTROLLER IS MADE UP OF:
        SORTER_MOTOR,
        INTAKE_MOTOR,
        COLOR_SENSOR_CONTROLLER,
        RAMP_SERVO, // END OF SORTER CONTROLLER OBJECTS
        SORTER_CONTROLLER,
        LIMELIGHT3A,
        TURNTABLE_CONTROLLER,
        INDICATOR_LIGHT;
    }

    Set<Subsystem> capabilities;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    private MatchPhase matchPhase;
    Map<String, FTCRobotSubsystem> subsystemMap;
    private ElapsedTime timer;
    private LinearOpMode opMode;


    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    private DecodeIMU imu;
    public DecodePinpointDrive mecanumDrive;
    // public DecodeMecanumDrive mecanumDrive;
    public DecodeTurntableMotor turntableMotor;
    public DecodeBallShooter ballShooter;
    public DecodeShooterMotor shooterMotor;
    public DecodeHoodServo hoodServo;

    public DecodeSorterMotor sorterMotor;

    public DecodeIntakeMotor intakeMotor;
    public DecodeColorSensorController colorSensorController;
    public DecodeRampServo rampServo;
    public DecodeSorterController sorterController;
    public DecodeLimelight limelight;

    public LoopTimer loopTimer;
    public DecodeRobotModes robotModes;
    public DecodeTarget redGoal;
    public DecodeTarget blueGoal;
    public DecodeTurntableTrackingController turntableTrackingController;
    public DecodeRGBIndicator indicator;


    public DecodeRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config,
                       DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        loopTimer = new LoopTimer();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
        // sets up robot driving modes (normal/inverse) and drive train power
        robotModes = new DecodeRobotModes();
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());
        enableDataLogging();
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    /**
     * Create the robot should be called from the teleop or auto opmode.
     *
     * @return
     */

    @Override
    public boolean createRobot() {
        if (capabilities.contains(Subsystem.IMU)) {
            imu = new DecodeIMU(hardwareMap, telemetry);
            subsystemMap.put(imu.getName(), imu);
        }

        if (capabilities.contains(Subsystem.MECANUM_DRIVE)) {
            Pose2d beginPose = new Pose2d(0, 0, 0);
            mecanumDrive = new DecodePinpointDrive(hardwareMap, new Pose2d(0, 0, 0));
            //mecanumDrive = new DecodePinpointDrive(hardwareMap, beginPose);
            subsystemMap.put(mecanumDrive.getName(), mecanumDrive);
        }

        if (capabilities.contains(Subsystem.TURNTABLE_MOTOR)) {
            turntableMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
            subsystemMap.put(turntableMotor.getName(), turntableMotor);
        }


        if (capabilities.contains(Subsystem.BALL_SHOOTER)) {
            ballShooter = new DecodeBallShooter(hardwareMap, telemetry);
            subsystemMap.put(ballShooter.getName(), ballShooter);
        }


        if (capabilities.contains(Subsystem.SHOOTER_MOTOR)) {
            shooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
            subsystemMap.put(shooterMotor.getName(), shooterMotor);
        }

        if (capabilities.contains(Subsystem.HOOD_SERVO)) {
            hoodServo = new DecodeHoodServo(hardwareMap, telemetry);
            subsystemMap.put(hoodServo.getName(), hoodServo);
        }

        if (capabilities.contains(Subsystem.SORTER_MOTOR)) {
            sorterMotor = new DecodeSorterMotor(hardwareMap, telemetry);
            // The sorterController will handle logging, init and update() for its parts so we don't put it
            // into the list of subsystems.
            //subsystemMap.put(sorterMotor.getName(), sorterMotor);
        }

        if (capabilities.contains(Subsystem.INTAKE_MOTOR)) {
            intakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
            // The sorterController will handle logging, init and update() for its parts so we don't put it
            // into the list of subsystems.
            //subsystemMap.put(intakeMotor.getName(), intakeMotor);
        }

        if (capabilities.contains(Subsystem.COLOR_SENSOR_CONTROLLER)) {
            colorSensorController = new DecodeColorSensorController(hardwareMap, telemetry);
            // The sorterController will handle logging, init and update() for its parts so we don't put it
            // into the list of subsystems.
            //subsystemMap.put(colorSensorController.getName(), colorSensorController);
        }

        if (capabilities.contains(Subsystem.RAMP_SERVO)) {
            rampServo = new DecodeRampServo(hardwareMap, telemetry);
            // The sorterController will handle logging, init and update() for its parts so we don't put it
            // into the list of subsystems.
            //subsystemMap.put(rampServo.getName(), rampServo);
        }
        if (capabilities.contains(Subsystem.INDICATOR_LIGHT)) {
            indicator = new DecodeRGBIndicator(hardwareMap, telemetry);
            subsystemMap.put(indicator.getName(), indicator);
        }

        if (capabilities.contains(Subsystem.SORTER_CONTROLLER)) {
            sorterController = new DecodeSorterController(hardwareMap, telemetry,
                    colorSensorController,
                    sorterMotor,
                    rampServo,
                    intakeMotor,
                    indicator);
            subsystemMap.put(sorterController.getName(), sorterController);
        }
        if (capabilities.contains(Subsystem.LIMELIGHT3A)) {
            limelight = new DecodeLimelight(hardwareMap, telemetry, imu);
            subsystemMap.put(limelight.getName(), limelight);
        }
        if (capabilities.contains(Subsystem.TURNTABLE_CONTROLLER)) {
            turntableTrackingController = new DecodeTurntableTrackingController(hardwareMap, telemetry,
                    limelight,
                    turntableMotor,
                    imu,
                    mecanumDrive,
                    indicator);
            subsystemMap.put(turntableTrackingController.getName(), turntableTrackingController);
        }


        if (MatchPhase.getMatchPhase() == MatchPhase.AUTONOMOUS) {
            init();
        } else {
            dataLog.logData("Robot Init starting");
            timer.reset();

            for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
                subsystem.setDataLog(dataLog);
                subsystem.enableDataLogging(); /*
                if (!subsystem.init(config)) {
                    if (dataLoggingEnabled)
                        dataLog.logData(subsystem.getName() + " initialization failed");
                }
                */
            }
        }
        redGoal = new DecodeTarget(new Pose2D(DistanceUnit.INCH, -58.3, 55.6, AngleUnit.DEGREES, 0));
        blueGoal = new DecodeTarget(new Pose2D(DistanceUnit.INCH, -58.3, -55.6, AngleUnit.DEGREES, 0));

        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Robot Init starting");
        timer.reset();

        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.setDataLog(dataLog);
            subsystem.enableDataLogging();
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }

        // wait until all the updates are complete or until the timer has expired
        timer.reset();
        while (!isInitComplete()) {
            update();

            if (timer.milliseconds() > 2000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                break;
            }
            telemetry.update();
            opMode.idle();
        }
        //ledStrip.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_FOREST_PALETTE);
    }

    /*
     * Every system must tell us when its init is complete. When all of the inits are complete, the
     * robot init is complete.
     *
     * @return
     */
    @Override
    public boolean isInitComplete() {
        boolean result = true;
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            // the subsystems now take care of their own logging for init and init completion
            // This is so we don't dump huge amounts of repetitive data into the log file.
//            if (subsystem.isInitComplete()) {
//                if (dataLoggingEnabled) {
//                    dataLog.logData("Init complete for " + subsystem.getName());
//                }
//
//            } else {
//                dataLog.logData("Init is not complete for " + subsystem.getName());
//            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Robot init complete");
        }
        return result;
    }

    /**
     * Every system has an update() method that can be used to run a state machine for that system.
     * Note that some systems don't have a state machine but the update() method will be there
     * anyway just in case that changes in the future.
     */
    @Override
    public void update() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.update();
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.timedUpdate(timerValueMsec);
        }
    }

    @Override
    public void shutdown() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.shutdown();
        }
    }

    /**
     * For each subsystem that supports logging turn it on.
     */
    public void enableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.enableDataLogging();
        }
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.disableDataLogging();
        }
        dataLoggingEnabled = false;
    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        return true;
    }

    @Override
    public double getCurrentRotation(AngleUnit unit) {
        return 0;
    }

    public double getCurrentRotationIMU(AngleUnit unit) {
        return unit.fromDegrees(imu.getHeading());
    }

    public boolean getCurrentRobotPosition(RobotPosition position) {
        return true;
    }

    public void setAllianceColor(Color color) {
        if (color == Color.RED) {
            turntableTrackingController.setGoal(redGoal);
        } else {
        }
    }

}


