package org.firstinspires.ftc.teamcode.Lib.LauncherBot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.MecanumDriveUltimateGoal;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LauncherBotRobot implements FTCRobot {

    public enum HardwareName {

        IMU("imu"),
        CONFIG_FL_MOTOR("leftFrontMotor"),
        CONFIG_FR_MOTOR("rightFrontMotor"),
        CONFIG_BL_MOTOR("leftRearMotor"),
        CONFIG_BR_MOTOR("rightRearMotor"),
        CONFIG_LEFT_ODOMETRY_MODULE("LeftOdometryModule"),
        CONFIG_RIGHT_ODOMETRY_MODULE("RightOdometryModule"),
        CONFIG_BACK_ODOMETRY_MODULE("BackOdometryModule"),
        ODOMETRY_MODULE_LEFT("leftFrontMotor"),
        ODOMETRY_MODULE_RIGHT("rightFrontMotor"),
        ODOMETRY_MODULE_BACK("rightRearMotor"),
        LEFT_SHOOTER_MOTOR("leftShooterMotor"),
        RIGHT_SHOOTER_MOTOR("rightShooterMotor"),
        LEAD_SCREW_MOTOR("leadScrewMotor"),
        SHOOTER_SERVO("shooterServo"),
        ANGLE_CHANGER_LOWER_LIMIT_SWITCH("angleAdjusterLowerSwitch"),
        ANGLE_CHANGER_UPPER_LIMIT_SWITCH("angleAdjusterUpperSwitch");
        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM_DRIVE,
        SHOOTER,
        SHOOTER_SERVO,
        ANGLE_CHANGER,
        LOOP_TIMER
    }

    Set<Subsystem> capabilities;

    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    Map<String, FTCRobotSubsystem> subsystemMap;

    private ElapsedTime timer;
    private LinearOpMode opMode;

    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    private AdafruitIMU8863 imu;
    public LauncherBotMecanumDrive mecanum;
    public LauncherBotDualMotorGearBox shooter;
    public LauncherBotShooterServo shooterServo;
    public LauncherBotAngleChanger angleChanger;
    public LoopTimer loopTimer;

    public LauncherBotRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
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

    /**
     * Create the robot should be called from the teleop or auto opmode.
     *
     * @return
     */
    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM_DRIVE)) {
            mecanum = new LauncherBotMecanumDrive(HardwareName.CONFIG_FL_MOTOR.hwName, HardwareName.CONFIG_BL_MOTOR.hwName, HardwareName.CONFIG_FR_MOTOR.hwName, HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
            subsystemMap.put(mecanum.getName(), mecanum);
        }

        if (capabilities.contains(Subsystem.SHOOTER)) {
            shooter = new LauncherBotDualMotorGearBox(HardwareName.LEFT_SHOOTER_MOTOR.hwName, HardwareName.RIGHT_SHOOTER_MOTOR.hwName, hardwareMap, telemetry);
            subsystemMap.put(shooter.getName(), shooter);
        }

        if (capabilities.contains(Subsystem.SHOOTER_SERVO)) {
            shooterServo = new LauncherBotShooterServo(hardwareMap, telemetry);
            subsystemMap.put(shooterServo.getName(), shooterServo);
        }

        if (capabilities.contains(Subsystem.ANGLE_CHANGER)) {
            angleChanger = new LauncherBotAngleChanger(hardwareMap, telemetry);
            subsystemMap.put(angleChanger.getName(), angleChanger);
        }

        if (capabilities.contains(Subsystem.LOOP_TIMER)) {
            loopTimer = new LoopTimer();
            subsystemMap.put(loopTimer.getName(), loopTimer);
        }

        init();
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Init starting");
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

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            telemetry.update();
            opMode.idle();
        }
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
        // put the isInitComplete for each subsystem here. In other words repeat this block of code
        // for each subsystem
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            if (subsystem.isInitComplete()) {
                if (dataLoggingEnabled) {
                    dataLog.logData("Init complete for " + subsystem.getName());
                }

            } else {
                dataLog.logData("Init is not complete for " + subsystem.getName());
            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Init complete");
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
}
