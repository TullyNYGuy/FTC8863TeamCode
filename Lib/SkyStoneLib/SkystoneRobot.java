package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;

public class SkystoneRobot {
    private IntakeWheels intake;

    public IntakeWheels getIntake() {
        return intake;
    }

    public void setIntake(IntakeWheels intake) {
        this.intake = intake;
    }

    public Mecanum getMecanum() {
        return mecanum;
    }

    public void setMecanum(Mecanum mecanum) {
        this.mecanum = mecanum;
    }

    public DualLift getLift() {
        return lift;
    }

    public void setLift(DualLift lift) {
        this.lift = lift;
    }

    public ExtensionArm getExtensionArm() {
        return extensionArm;
    }

    public void setExtensionArm(ExtensionArm extensionArm) {
        this.extensionArm = extensionArm;
    }

    public GripperRotator getGripper() {
        return gripper;
    }

    public void setGripper(GripperRotator gripper) {
        this.gripper = gripper;
    }

    /* TODO: Needs initialization */
    Mecanum mecanum;
    DualLift lift;
    ExtensionArm extensionArm;
    GripperRotator gripper;
    OdometrySystem odometry;

    public SkystoneRobot() {

    }

    void getCurrentPosition(Position position) {
        odometry.getCurrentPosition(position);
    }
}
