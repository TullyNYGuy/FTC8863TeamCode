package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;

public class SkystoneRobot {
    /* TODO: Needs initialization */
    private IntakeWheels intake;
    private Mecanum mecanum;
    private DualLift lift;
    private ExtensionArm extensionArm;
    private GripperRotator gripper;
    private OdometrySystem odometry;

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


    public SkystoneRobot() {

    }

    void getCurrentPosition(Position position) {
        odometry.getCurrentPosition(position);
    }
}
