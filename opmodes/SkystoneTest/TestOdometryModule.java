package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Units;

public class TestOdometryModule extends OdometryModule {
    public TestOdometryModule(HardwareMap hardwareMap) {
        super(0, 0, DistanceUnit.CM, "", null);
    }

    private double data = 0;

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public double getDistanceSinceLastChange(DistanceUnit units) {
        return data;
    }
}
