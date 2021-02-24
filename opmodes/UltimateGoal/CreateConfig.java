package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

/*
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Create Config", group = "ATest")
//@Disabled

public class CreateConfig extends LinearOpMode {






    // Put your variable declarations here


    @Override
    public void runOpMode() {


        // Put your initializations here
        Configuration config = new Configuration();
        waitForStart();
        DcMotor8863.saveMotorConfiguration(config, "FRMotor", "FrontRight", DcMotorSimple.Direction.FORWARD, ANDYMARK_20_ORBITAL);
        DcMotor8863.saveMotorConfiguration(config, "BRMotor", "BackRight", DcMotorSimple.Direction.FORWARD, ANDYMARK_20_ORBITAL);
        DcMotor8863.saveMotorConfiguration(config, "FLMotor", "FrontLeft", DcMotorSimple.Direction.REVERSE, ANDYMARK_20_ORBITAL);
        DcMotor8863.saveMotorConfiguration(config, "BLMotor", "BackLeft", DcMotorSimple.Direction.REVERSE, ANDYMARK_20_ORBITAL);
        config.store();

    }
}
