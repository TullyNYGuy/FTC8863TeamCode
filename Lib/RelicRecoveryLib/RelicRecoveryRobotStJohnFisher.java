package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusherControl;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.SideBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexSweeper;

public class RelicRecoveryRobotStJohnFisher {

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
    public ExtensionArm extensionArm;
    public GlyphDumper glyphDumper;
    public AllianceColorSwitch allianceColorSwitch;
    public AllianceColorSwitch.AllianceColor allianceColor;

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

    private RelicRecoveryRobotStJohnFisher(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry) {
        if (robotMode == RobotMode.AUTONOMOUS) {
            // create the robot for autonomous
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
            telemetry.addData("Drive Train Initialized", "!");
            telemetry.update();

            allianceColorSwitch = new AllianceColorSwitch(hardwareMap, telemetry);
            telemetry.addData("Drive Train Initialized", "!");
            telemetry.addData("Color Switches Initialized", "!");
            telemetry.update();

            allianceColor = allianceColorSwitch.getAllianceColor();
            telemetry.update();
        } else {
            // create the robot for teleop
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
            telemetry.addData("Drive Train Initialized", "!");
            telemetry.update();

            allianceColor = AllianceColorSwitch.AllianceColor.BLUE;
        }

        extensionArm = new ExtensionArm(hardwareMap, telemetry);
        telemetry.addData("Drive Train Initialized", "!");
        telemetry.addData("Color Switches Initialized", "!");
        telemetry.addData("ExtensionArm Created", "!");
        telemetry.update();

        glyphDumper = new GlyphDumper(hardwareMap, telemetry);
        telemetry.addData("Drive Train Initialized", "!");
        telemetry.addData("Color Switches Initialized", "!");
        telemetry.addData("ExtensionArm created", "!");
        telemetry.addData("Glyph Dumper created", "!");
        telemetry.update();
        init(telemetry);
    }

    public static RelicRecoveryRobotStJohnFisher createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry) {
        RelicRecoveryRobotStJohnFisher robot = new RelicRecoveryRobotStJohnFisher(hardwareMap, RobotMode.AUTONOMOUS, telemetry);
        return robot;
    }

    public static RelicRecoveryRobotStJohnFisher createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry) {
        RelicRecoveryRobotStJohnFisher robot = new RelicRecoveryRobotStJohnFisher(hardwareMap, RobotMode.TELEOP, telemetry);
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
        extensionArm.init();
        telemetry.addData("Drive Train Initialized", "!");
        telemetry.addData("Color Switches Initialized", "!");
        telemetry.addData("ExtensionArm Initialized", "!");
        telemetry.addData("Glyph Dumper created", "!");
        telemetry.update();

        glyphDumper.init();
        telemetry.addData("Drive Train Initialized", "!");
        telemetry.addData("Color Switches Initialized", "!");
        telemetry.addData("ExtensionArm Initialized", "!");
        telemetry.addData("Glyph Dumper Initialized", "!");
        telemetry.update();
    }

private void createAndInitExtensionArm(Telemetry telemetry) {

}

private void createAndInitGlyphDumper(Telemetry telemetry){

}
    public void update() {
        extensionArm.update();
        glyphDumper.update();
    }

    public void shutdown() {
        extensionArm.shutdown();
        glyphDumper.shutdown();
        driveTrain.shutdown();
    }
}
