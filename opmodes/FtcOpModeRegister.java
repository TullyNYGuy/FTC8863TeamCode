package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import org.firstinspires.ftc.teamcode.opmodes.GenericTest.TestDCMotor8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.ToggleButtonTest;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.TestServo8863;

import org.firstinspires.ftc.teamcode.opmodes.ResQ.TeleOP;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.SweeperTest;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestBarGrabber;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestLeftZipLineServo;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestLinearSlide;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestRightZipLineServo;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestTrapDoor;

import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestRampServo;
import org.firstinspires.ftc.teamcode.opmodes.ResQTest.TestAimingServo;

/**
 * Register Op Modes
 */
public class FtcOpModeRegister implements OpModeRegister {
  public void register(OpModeManager manager) {
    // test routines
    manager.register("SweeperTest", SweeperTest.class);
    manager.register("TestLinearSlide", TestLinearSlide.class);
    manager.register("TestTrapDoor", TestTrapDoor.class);
    manager.register("TestServo8863", TestServo8863.class);
    manager.register("TestBarGrabber", TestBarGrabber.class);
    manager.register("TestLeftZipLineServo", TestLeftZipLineServo.class);
    manager.register("TestRightZipLineServo", TestRightZipLineServo.class);
    manager.register("TestAimingServo", TestAimingServo.class);
    manager.register("TestDCMotor8863", TestDCMotor8863.class);
    manager.register("TestRampServo", TestRampServo.class);

    /** ed young's */
    manager.register("TeleOP", TeleOP.class);
    manager.register("testing", ToggleButtonTest.class);
  }
}
