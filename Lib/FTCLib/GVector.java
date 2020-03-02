package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public class GVector {
    double[] values;

    GVector(boolean init) {
        values = new double[3];
        if (init)
            setZero();
    }

    public GVector() {
        values = new double[3];
        setZero();
    }

    public GVector(GVector m) {
        values = new double[3];
        for (int i = 0; i < 3; i++)
            values[i] = m.values[i];
    }

    public GVector(double a1, double a2) {
        values = new double[3];
        values[0] = a1;
        values[1] = a2;
        values[2] = 1.0;
    }

    public GVector(double a1, double a2, double a3) {
        values = new double[3];
        values[0] = a1;
        values[1] = a2;
        values[2] = a3;
    }

    public GVector(Vector2D src) {
        values = new double[3];
        values[0] = src.x;
        values[1] = src.y;
        values[2] = 1.0;
    }

    public Vector2D getVector2D() {
        return new Vector2D(values[0], values[1]);
    }

    public void set(int i, double value) {
        if (i > 0 && i <= 3)
            values[i - 1] = value;
    }

    public double get(int i) {
        if (i > 0 && i <= 3)
            return values[i - 1];
        return 0.0;
    }

    public void setZero() {
        values[0] = 0.0;
        values[1] = 0.0;
        values[2] = 1.0;
    }

    public GVector add(GVector m) {
        GVector res = new GVector(false);
        for (int i = 0; i < 3; i++)
            res.values[i] = values[i] + m.values[i];
        return res;
    }

    @Override
    public String toString() {
        return String.format("[%+.2f, %+.2f, %+.2f]", values[0], values[1], values[2]);
    }
}
