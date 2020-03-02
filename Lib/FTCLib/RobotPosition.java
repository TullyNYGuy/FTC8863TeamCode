package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import java.util.Locale;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class RobotPosition extends Vector2D {
    public double rotation;
    public DistanceUnit distanceUnit;
    public AngleUnit angleUnit;

    public RobotPosition() {
        super();
        initZero();
    }

    public RobotPosition(DistanceUnit distanceUnit, double x, double y, AngleUnit angleUnit, double rotation) {
        super(x, y);
        this.rotation = rotation;
        this.distanceUnit = distanceUnit;
        this.angleUnit = angleUnit;
    }

    public RobotPosition(RobotPosition source) {
        super(source);
        if (source != null) {
            this.rotation = source.rotation;
            this.distanceUnit = source.distanceUnit;
            this.angleUnit = source.angleUnit;
        } else {
            initZero();
        }
    }

    public RobotPosition(Vector2D source) {
        super(source);
        initZero();
    }

    public RobotPosition(Position source) {
        super(source);
        initZero();
        if (source != null)
            this.distanceUnit = source.unit;
    }

    private void initZero() {
        this.rotation = 0;
        this.distanceUnit = DistanceUnit.CM;
        this.angleUnit = AngleUnit.RADIANS;
    }

    private void copyFrom(RobotPosition source) {
        this.x = distanceUnit.fromUnit(source.distanceUnit, source.x);
        this.y = distanceUnit.fromUnit(source.distanceUnit, source.y);
        this.rotation = angleUnit.fromUnit(source.angleUnit, source.rotation);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "(%+.2f %s, %+.2f %s, %+.2f %s)", x, distanceUnit, y, distanceUnit, rotation, angleUnit);
    }
}
