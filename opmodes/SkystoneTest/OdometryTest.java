package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Units;

/**
 * Created by ball on 10/7/2017.
 */

@TeleOp(name = "Odometry Test", group = "Run")
//@Disabled

public class OdometryTest extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations - THESE ARE JUST SETTING UP THE VARIABLES (RESERVING MEMORY FOR THEM)
    //*********************************************************************************************

    // Here are declarations for all of the hardware

    // Here is the declaration for the hardware map - it contains information about the configuration
    // of the robot and how to talk to each piece of hardware
    //public HardwareMap hardwareMap;

    // GAMEPAD 1 - declare all of the objects on game pad 1

    // declare the buttons on the gamepad as multi push button objects



    // GAMEPAD 2 - declare all of the objects on game pad 2



    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        TestOdometryModule right = new TestOdometryModule(hardwareMap);
        TestOdometryModule left= new TestOdometryModule(hardwareMap);
        TestOdometryModule back = new TestOdometryModule(hardwareMap);
        OdometrySystem trial = new OdometrySystem(DistanceUnit.CM, left, right, back);
        trial.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE, 0, 1, DcMotorSimple.Direction.FORWARD, 1, 0, DcMotorSimple.Direction.FORWARD);

        left.setData(0);
        right.setData(0);
        back.setData(1);
        trial.calculateMoveDistance();
        trial.updateCoordinates();
        MecanumCommands shower = new MecanumCommands();
                trial.getMovement(shower);
        telemetry.addData("robot moved: ", shower);
        // create the robot. Tell the driver we are creating it since this can take a few seconds
        // and we want the driver to know what is going on.
       // telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();


        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // The user pressed play so we start the robot and then check to make sure he or she has
        // not pressed stop. If they press stop, then opModeIsActive() will return false. It can
        // also return false if there is some kind of error in the robot software or hardware.

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons - look for a button press on gamepad 1 and then do the action
            // for that button
            //*************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }


            // update the drive motors with the new power




            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

}

