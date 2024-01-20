package org.firstinspires.ftc.teamcode.opmodes.PowerPlay;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;

@Autonomous(name = "Set Pole Leaning Offset", group = "AARun")
@Disabled

public class SetPoleLeanOffset extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    private double xOffset = 0;
    private double yOffset = 0;
    private double INCREMENT = 0.5; // inches
    private boolean finished = false;

    public GamepadButtonMultiPush gamepad1DpadUp;
    public GamepadButtonMultiPush gamepad1DpadDown;
    public GamepadButtonMultiPush gamepad1DpadLeft;
    public GamepadButtonMultiPush gamepad1DpadRight;
    public GamepadButtonMultiPush gamepad1a;

    @Override
    public void runOpMode() {
        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(1);

        telemetry.addData(">", "Set lean using game pad 1, DPAD button");
        telemetry.addData("Press play to begin", ".");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        
        while (opModeIsActive() && !finished) {
            setLean();
            setTelemetry();
            telemetry.update();
            if (gamepad1a.buttonPress(gamepad1.a)) {
                finished = true;
            }
            idle();
        }
        
        PowerPlayPersistantStorage.setJunctionPoleOffset(xOffset, yOffset);
        while (opModeIsActive()) {
            telemetry.addData("Pole lean is saved =", xOffset + "," + yOffset );
            telemetry.addData("Press stop", ".");
            telemetry.update();
        }
    }
    
    private void setLean() {
        if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
            yOffset = yOffset + INCREMENT;
        }
        if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
            yOffset = yOffset - INCREMENT;
        }
        if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
            xOffset = xOffset - INCREMENT;
        }
        if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
            xOffset = xOffset + INCREMENT;
        }
    }

    private void setTelemetry() {
        if(xOffset == 0) {
            telemetry.addData("No lean left or right", "!");
        }
        if(xOffset < 0) {
            telemetry.addData("Pole leans LEFT = ", xOffset + "inch");
        }
        if(xOffset > 0) {
            telemetry.addData("Pole leans RIGHT = ", xOffset + "inch");
        }
        if(yOffset == 0) {
            telemetry.addData("No lean towards you or away from you", "!");
        }
        if(yOffset < 0) {
            telemetry.addData("Pole leans TOWARD you = ", yOffset  + "inch");
        }
        if(yOffset > 0) {
            telemetry.addData("Pole leans AWAY you = ", yOffset  + "inch");
        }
        telemetry.addData("Press a to lock in the lean", "!");
    }
}





