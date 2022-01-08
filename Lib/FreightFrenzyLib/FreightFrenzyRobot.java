package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FreightFrenzyRobot implements FTCRobot {

    public enum HardwareName {

        IMU("imu"),
        CONFIG_FL_MOTOR("FLMotor"),
        CONFIG_FR_MOTOR("FRMotor"),
        CONFIG_BL_MOTOR("BLMotor"),
        CONFIG_BR_MOTOR("BRMotor"),
        CONFIG_LEFT_ODOMETRY_MODULE("LeftOdometryModule"),
        CONFIG_RIGHT_ODOMETRY_MODULE("RightOdometryModule"),
        CONFIG_BACK_ODOMETRY_MODULE("BackOdometryModule"),
        ODOMETRY_MODULE_LEFT("FrontLeft"),
        ODOMETRY_MODULE_RIGHT("BackLeft"),
        ODOMETRY_MODULE_BACK("BackRight"),
        ;

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM,
        //INTAKE_MOTORS,
       //INTAKE_PUSHER,
        // these are now part of the IntakeWheels object
        //INTAKE_LIMIT_SW,
        ODOMETRY,
        //LIFT,
        //EXT_ARM,
        //BASE_MOVER
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
    private Mecanum mecanum;
    private OdometrySystem odometry;

    public FreightFrenzyRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        enableDataLogging();
        this.opMode = opMode;
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());

       // capabilities.remove(Subsystem.ODOMETRY);
        // capabilities.remove(Subsystem.LIFT);
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM)) {

            DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, HardwareName.CONFIG_FL_MOTOR.hwName, hardwareMap);
            DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, HardwareName.CONFIG_BL_MOTOR.hwName, hardwareMap);
            DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, HardwareName.CONFIG_FR_MOTOR.hwName, hardwareMap);
            DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);

            mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        }
        if (capabilities.contains(Subsystem.ODOMETRY)) {
            OdometryModule left = OdometryModule.createOdometryModuleFromFile(config, HardwareName.CONFIG_LEFT_ODOMETRY_MODULE.hwName, hardwareMap);
            OdometryModule right = OdometryModule.createOdometryModuleFromFile(config, HardwareName.CONFIG_RIGHT_ODOMETRY_MODULE.hwName, hardwareMap);
            OdometryModule back = OdometryModule.createOdometryModuleFromFile(config, HardwareName.CONFIG_BACK_ODOMETRY_MODULE.hwName, hardwareMap);
            odometry = new OdometrySystem(units, left, right, back);
            subsystemMap.put(odometry.getName(), odometry);
        }

        // My preference is to encapsulate as much as possible so that creation code can be reused.
        // So move this stuff into the IntakeWheels. It should know how to create itself. I should
        // not have to know that at the robot level.

//        DcMotor8863 rightIntake = new DcMotor8863("intakeMotorRight", hardwareMap);
//        DcMotor8863 leftIntake = new DcMotor8863("intakeMotorLeft", hardwareMap);
//        rightIntake.setMotorType(ANDYMARK_20_ORBITAL);
//        leftIntake.setMotorType(ANDYMARK_20_ORBITAL);





        init();
        return true;
    }
public void setPosition(double currentpositionx,double currentPositiionY,double currentPositionRot){
        if(odometry != null){

            odometry.setCoordinates(units, currentpositionx, currentPositiionY, AngleUnit.DEGREES, currentPositionRot);
        }else{
            if(imu != null){
                //it dodesnt want to work. implement inti thfr imu clas instead of  tnrd bno0ffimu class
                //imu.stopAccelerationIntegration();
                Position place = new Position(units, currentpositionx, currentPositiionY, 0, 0);
                Velocity velocity = new Velocity(units,0,0,0,0);
                imu.startAccelerationIntegration(place, velocity, 100);
            }
        }
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

        if (mecanum != null && !mecanum.init(config)) {
            if (dataLoggingEnabled)
                dataLog.logData("Mecanum initialization failed");
        }

        // inits for the command state machines

        // Start IMU-based positioning if Odometry is not enabled
        if(!capabilities.contains(Subsystem.ODOMETRY)) {
            imu.startAccelerationIntegration(null, null, 100);
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

//        if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW))
//            updateIntakeSwitches();
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
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        dataLoggingEnabled = false;

    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        if (odometry != null && odometry.isInitComplete()) {
            odometry.getCurrentPosition(position);
            return true;
        } else if(imu != null) {
            Position p = imu.getPosition();
            position.acquisitionTime = p.acquisitionTime;
            position.unit = p.unit;
            position.x = p.x;
            position.y = p.y;
            position.z = p.z;
            return true;
        } else {
            return false;
        }
    }

    /*
     * Return current robot rotation. If odometry is initialized odometry is used, otherwise IMU is used
     */
    @Override
    public double getCurrentRotation(AngleUnit unit) {
        if (odometry != null && odometry.isInitComplete())
            return odometry.getCurrentRotation(unit);
        else if (imu != null)
            return unit.fromDegrees(imu.getHeading());
        else
            return 0;
    }
    public double getCurrentRotationIMU(AngleUnit unit){
        return unit.fromDegrees(imu.getHeading());
    }
    public boolean getCurrentRobotPosition(RobotPosition position) {
        if (odometry != null && odometry.isInitComplete()) {
            odometry.getCurrentPosition(position);
            return true;
        } else if (imu != null) {
            Position p = imu.getPosition();
            position.x = position.distanceUnit.fromUnit(p.unit, p.x);
            position.y = position.distanceUnit.fromUnit(p.unit, p.y);
            position.rotation = position.angleUnit.fromDegrees(imu.getHeading());
            return true;
        } else {
            return false;
        }
    }

    public void setMovement(MecanumCommands commands) {
        if (mecanum != null)
            mecanum.setMotorPower(commands);
    }

    public void setMaxMovementPower(double maxPower) {
        if (mecanum != null)
            mecanum.setMaxMotorPower(maxPower);
    }

}
