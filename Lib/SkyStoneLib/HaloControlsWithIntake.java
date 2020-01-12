package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;

public class HaloControlsWithIntake extends HaloControls {
    private boolean powerSwitchPressed = false;
    private int modifier = 1;
    private Telemetry telemetry;

    public HaloControlsWithIntake(Gamepad gamepad, AdafruitIMU8863 imu, Telemetry telemetry) {
        super(gamepad, imu);
        this.telemetry = telemetry;
    }

    public boolean isIntakeInPressed() {
        return gamepad.b;
    }

    public boolean isIntakeOutPressed() {
        return gamepad.a;
    }

    public boolean isIntakeStopPressed() {
        return gamepad.y;
    }

    public double getPowerModifier() {
        if (gamepad.x && !powerSwitchPressed) {
            modifier = 1 - modifier;
        }
        telemetry.addData("Speed:", modifier == 0 ? "Half" : "Full");
        powerSwitchPressed = gamepad.x;
        return (modifier + 1) * 2;
    }

    public void calculateMecanumCommands(MecanumCommands commands) {
        super.calculateMecanumCommands(commands);
        if (commands != null) {
            double powerModifier = getPowerModifier();
            commands.setSpeed(commands.getSpeed() / powerModifier);
            commands.setSpeedOfRotation(commands.getSpeedOfRotation() / powerModifier);
        }
    }
}