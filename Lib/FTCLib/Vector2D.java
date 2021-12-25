package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import java.util.Locale;

import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D source) {
        if (source != null) {
            this.x = source.x;
            this.y = source.y;
        } else {
            this.x = 0;
            this.y = 0;
        }
    }

    public Vector2D(Position source) {
        if (source != null) {
            this.x = source.x;
            this.y = source.y;
        } else {
            this.x = 0;
            this.y = 0;
        }
    }

    public void toPosition(Position copyTo) {
        if (copyTo != null) {
            copyTo.x = x;
            copyTo.y = y;
            copyTo.z = 0;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "(%+.2f, %+.2f)", x, y);
    }

    public Vector2D minus(Vector2D p) {
        if (p != null)
            return new Vector2D(x - p.x, y - p.y);
        else
            return new Vector2D(this);
    }

    public Vector2D plus(Vector2D p) {
        if (p != null)
            return new Vector2D(x + p.x, y + p.y);
        else
            return new Vector2D(this);
    }

    public Vector2D scale(double t) {
        return new Vector2D(x * t, y * t);
    }

    public double innerProduct(Vector2D p) {
        if (p != null)
            return x * p.x + y * p.y;
        else
            return 0;
    }

    public double vectorProductScalar(Vector2D p) {
        if (p != null)
            return this.x * p.y - this.y * p.x;
        else
            return 0;
    }

    public double size() {
        return Math.hypot(x, y);
    }

    public double direction() {
        return Math.atan2(y, x);
    }

}