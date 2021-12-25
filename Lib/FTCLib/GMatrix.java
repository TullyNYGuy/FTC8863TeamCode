package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public class GMatrix {

    double[][] values;

    private GMatrix() {
        values = new double[3][3];
    }

    public static GMatrix createFrom(GMatrix src) {
        GMatrix m = new GMatrix();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                m.values[i][j] = src.values[i][j];
        return m;
    }

    public static GMatrix createFrom(GVector src) {
        GMatrix m = new GMatrix();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (i == j)
                    m.values[i][j] = src.values[i];
                else
                    m.values[i][j] = 0;
        return m;
    }

    public static GMatrix createIdentity() {
        GMatrix m = new GMatrix();
        m.setIdentity();
        return m;
    }

    public static GMatrix createScale(double scaleX, double scaleY) {
        GMatrix m = new GMatrix();
        m.setScale(scaleX, scaleY);
        return m;
    }

    public static GMatrix createTranslation(double translationX, double translationY) {
        GMatrix m = new GMatrix();
        m.setTranslation(translationX, translationY);
        return m;
    }

    public static GMatrix createRotation(double rotationAngle) {
        GMatrix m = new GMatrix();
        m.setRotation(rotationAngle);
        return m;
    }

    public void set(int i, int j, double value) {
        if (i > 0 && i <= 3 && j > 0 && j <= 3)
            values[i - 1][j - 1] = value;
    }

    public double get(int i, int j) {
        if (i > 0 && i <= 3 && j > 0 && j <= 3)
            return values[i - 1][j - 1];
        return 0.0;
    }

    public void setIdentity() {
        values[0][0] = 1.0;
        values[0][1] = 0.0;
        values[0][2] = 0.0;
        values[1][0] = 0.0;
        values[1][1] = 1.0;
        values[1][2] = 0.0;
        values[2][0] = 0.0;
        values[2][1] = 0.0;
        values[2][2] = 1.0;
    }

    public void setScale(double scaleX, double scaleY) {
        values[0][0] = scaleX;
        values[0][1] = 0.0;
        values[0][2] = 0.0;
        values[1][0] = 0.0;
        values[1][1] = scaleY;
        values[1][2] = 0.0;
        values[2][0] = 0.0;
        values[2][1] = 0.0;
        values[2][2] = 1.0;
    }

    public void setRotation(double rotationAngle) {
        double c = Math.cos(rotationAngle);
        double s = Math.sin(rotationAngle);
        values[0][0] = c;
        values[0][1] = s;
        values[0][2] = 0;
        values[1][0] = -s;
        values[1][1] = c;
        values[1][2] = 0.0;
        values[2][0] = 0.0;
        values[2][1] = 0.0;
        values[2][2] = 1.0;
    }

    public void setTranslation(double translationX, double translationY) {
        values[0][0] = 1.0;
        values[0][1] = 0.0;
        values[0][2] = translationX;
        values[1][0] = 0.0;
        values[1][1] = 1.0;
        values[1][2] = translationY;
        values[2][0] = 0.0;
        values[2][1] = 0.0;
        values[2][2] = 1.0;
    }

    public GMatrix add(GMatrix m) {
        GMatrix res = new GMatrix();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                res.values[i][j] = values[i][j] + m.values[i][j];
        return res;
    }

    public GMatrix mult(GMatrix m) {
        GMatrix res = new GMatrix();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                res.values[i][j] = 0;
                for (int k = 0; k < 3; k++)
                    res.values[i][j] += values[i][k] * m.values[k][j];
            }
        return res;
    }

    public GVector mult(GVector v) {
        GVector res = new GVector(false);
        for (int i = 0; i < 3; i++) {
            res.values[i] = 0.0;
            for (int j = 0; j < 3; j++)
                res.values[i] += values[i][j] * v.values[j];
        }
        return res;
    }

    public GMatrix transpose() {
        GMatrix res = new GMatrix();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                res.values[j][i] = values[i][j];
        return res;
    }

    public GMatrix inverse() {
        GMatrix res = new GMatrix();
        double[][] pDet = new double[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                pDet[i][j] = partialDeterminant(i, j);
        double multiplier = 1 / (values[0][0] * pDet[0][0] - values[0][1] * pDet[0][1] + values[0][2] * pDet[0][2]);
        double runningMult = 1.0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                res.values[i][j] = multiplier * runningMult * pDet[j][i];
                runningMult = -runningMult;
            }
        return res;
    }

    public double determinant() {
        return
                values[0][0] * partialDeterminant(0, 0)
                        - values[0][1] * partialDeterminant(0, 1)
                        + values[0][2] * partialDeterminant(0, 2);
    }

    private double partialDeterminant(int i, int j) {
        switch (i) {
            case 0:
                switch (j) {
                    case 0:
                        return values[1][1] * values[2][2] - values[1][2] * values[2][1];
                    case 1:
                        return values[1][0] * values[2][2] - values[1][2] * values[2][0];
                    case 2:
                        return values[1][0] * values[2][1] - values[1][1] * values[2][0];
                }
                return 0;
            case 1:
                switch (j) {
                    case 0:
                        return values[0][1] * values[2][2] - values[0][2] * values[2][1];
                    case 1:
                        return values[0][0] * values[2][2] - values[0][2] * values[2][0];
                    case 2:
                        return values[0][0] * values[2][1] - values[0][1] * values[2][0];
                }
                return 0;
            case 2:
                switch (j) {
                    case 0:
                        return values[0][1] * values[1][2] - values[0][2] * values[1][1];
                    case 1:
                        return values[0][0] * values[1][2] - values[0][2] * values[1][0];
                    case 2:
                        return values[0][0] * values[1][1] - values[0][1] * values[1][0];
                }
                return 0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[(%+.2f, %+.2f, %+.2f)(%+.2f, %+.2f, %+.2f)(%+.2f, %+.2f, %+.2f)]", values[0][0], values[0][1],
                values[0][2], values[1][0], values[1][1], values[1][2], values[2][0], values[2][1], values[2][2]);
    }
}
