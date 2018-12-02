package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.Collector;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

public class RoverRuckusRobot {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum RobotMode {
        AUTONOMOUS,
        TELEOP
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // Here are all of the objects that make up the entire robot
    // note that the IMU is an object in the drive train
    public RobotMode robotMode;
    public DriveTrain driveTrain;
    public CollectorGB collector;
    public Telemetry telemetry;
    public DeliveryLiftSystem deliveryLiftSystem;
   public CollectorArm collectorArm;

    
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    private RoverRuckusRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        this.telemetry = telemetry;

        collector = new CollectorGB(hardwareMap, telemetry);
        collectorArm = new CollectorArm(hardwareMap, telemetry);
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);

        if (robotMode == RobotMode.AUTONOMOUS) {
            // create the robot for autonomous
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
            //allianceColorSwitch = new AllianceColorSwitch(hardwareMap, telemetry);
            //allianceColor = allianceColorSwitch.getAllianceColor();
        } else {
            // create the robot for teleop
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
        }
        init(telemetry);
    }

    public static RoverRuckusRobot createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        RoverRuckusRobot robot = new RoverRuckusRobot(hardwareMap, RobotMode.AUTONOMOUS, telemetry, teamColor, dataLog);
        return robot;
    }

    public static RoverRuckusRobot createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        RoverRuckusRobot robot = new RoverRuckusRobot(hardwareMap, RobotMode.TELEOP, telemetry, teamColor, dataLog);
        return robot;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void init(Telemetry telemetry) {
        collector.initialize();
        deliveryLiftSystem.init();
    }

    public void setupForRun() {
    }

    public void update() {
        collector.update();
        deliveryLiftSystem.update();
        collectorArm.update();
    }

    public void dehang (){
//        collectorArm.goToDehang();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //delay(2000);
        //collectorArm.update();
        deliveryLiftSystem.dehang();
        while (deliveryLiftSystem.update() != DcMotor8863.MotorState.COMPLETE_FLOAT){
        }
        //delay(5000);
        //deliveryLiftSystem.update();
//        collectorArm.goToClearStar();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //collectorArm.update();
        //delay (1000);
        deliveryLiftSystem.deliveryBoxToTransfer();
//        delay(500);
//        collectorArm.goToHome();
//        while (collectorArm.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {
//        }
        //collectorArm.update();
    }

    public void undehang() {
        deliveryLiftSystem.undehang();
        delay(2000);
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        driveTrain.shutdown();
       // collector.shutdown();
    }

}
