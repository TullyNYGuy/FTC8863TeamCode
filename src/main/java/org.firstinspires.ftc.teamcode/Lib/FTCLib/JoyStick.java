package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * Class to implement a joystick with scaling and deadband compensation
 */
public class JoyStick {

    // Constants - shguld probably be made into private fields to make it more generic
    final static double JOYSTICK_MIN = -1;
    final static double JOYSTICK_MAX = 1;
    final static double OUTPUT_MIN = -1;
    final static double OUTPUT_MAX = 1;

    /**
     * An enum defining which type of scaling method to use.
     * LINEAR is a straight line
     * SQUARE is a f(x) = x^2
     */
    public enum JoyStickMode {
        LINEAR, SQUARE
    }

    /**
     * An enum defining whether the joystick input should be multiplied by -1
     * For the Y joystick up is negative. I don't think like that so this can be used to change it
     * to positive.
     */
    public enum InvertSign {
        INVERT_SIGN, NO_INVERT_SIGN
    }

    public enum JoystickSide {
        LEFT, RIGHT
    }

    public enum JoystickAxis {
        X, Y
    }

    /**
     * An enum defining power reduction states
     */
    private enum PowerReduction {
        FULL_POWER,
        HALF_POWER,
        QUARTER_POWER,
        TWENTY_PERCENT_POWER,
        TEN_PERCENT_POWER
    }
    //*********************************************************************************************
    // private data fields that can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The type of scaling to be used.
     */
    private JoyStickMode joyStickMode;
    /**
     * The deadband value to be used. The deadband is when the joystick is moved but the robot does
     * not respond. Above this value there is a response.
     */
    private double deadBand;
    /**
     * Whether to change the sign of the input.
     */
    private InvertSign invertSign;
    /**
     * The driver sometimes may want to lower the max power, like cut the power in half. This allows
     * that. Value should be between 0 and 1. 1 is no reduction.
     */
    private double reductionFactor;

    private PowerReduction toggleQuarterPower = PowerReduction.FULL_POWER;
    private PowerReduction toggleHalfPower = PowerReduction.FULL_POWER;
    private PowerReduction toggle_20_Power = PowerReduction.FULL_POWER;
    private PowerReduction toggle_10_Power = PowerReduction.FULL_POWER;

    private JoystickSide joystickSide = JoystickSide.LEFT;

    private JoystickAxis joystickAxis = JoystickAxis.Y;

    private Gamepad gamepad = null;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //*********************************************************************************************

    /**
     * @return The method for scaling the joystick.
     */
    public JoyStickMode getJoyStickMode() {
        return joyStickMode;
    }

    /**
     * @param joyStickMode The method for scaling the joystick.
     */
    public void setJoyStickMode(JoyStickMode joyStickMode) {
        this.joyStickMode = joyStickMode;
    }

    /**
     * @return The deadband value for the joystick, anything less than this value and the robot
     * does not respond.
     */
    public double getDeadBand() {
        return deadBand;
    }

    /**
     * @param deadBand The deadband value for the joystick, anything less than this value and the robot
     *                 does not respond.
     */
    public void setDeadBand(double deadBand) {
        this.deadBand = deadBand;
    }

    /**
     * @return Whether to invert the sign of the joystick value.
     */
    public InvertSign getInvertSign() {
        return invertSign;
    }

    /**
     * @param invertSign Whether to invert the sign of the joystick value.
     */
    public void setInvertSign(InvertSign invertSign) {
        this.invertSign = invertSign;
    }

    /**
     * @return Reduce output by this factor.
     */
    public double getReductionFactor() {
        return reductionFactor;
    }

    /**
     * @param reductionFactor Reduce output by this factor.
     */
    public void setReductionFactor(double reductionFactor) {
        this.reductionFactor = reductionFactor;
    }

    public JoystickSide getJoystickSide() {
        return joystickSide;
    }

    public void setJoystickSide(JoystickSide joystickSide) {
        this.joystickSide = joystickSide;
    }

    public JoystickAxis getJoystickAxis() {
        return joystickAxis;
    }

    public void setJoystickAxis(JoystickAxis joystickAxis) {
        this.joystickAxis = joystickAxis;
    }

    public Gamepad getGamepad() {
        return gamepad;
    }

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    //*********************************************************************************************
    //           CONSTRUCTORS
    //*********************************************************************************************

    /**
     * Constructor
     *
     * @param joyStickMode    The method for scaling the joystick.
     * @param deadBand        The deadband value for the joystick, anything less than this value and the robot
     *                        does not respond.
     * @param invertSign      Whether to invert the sign of the joystick value.
     * @param reductionFactor Reduce output by this factor.
     */
    public JoyStick(JoyStickMode joyStickMode, double deadBand, InvertSign invertSign, double reductionFactor) {
        this.joyStickMode = joyStickMode;
        this.deadBand = deadBand;
        this.invertSign = invertSign;
        this.reductionFactor = reductionFactor;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Constructor
     *
     * @param joyStickMode The method for scaling the joystick.
     * @param deadBand     The deadband value for the joystick, anything less than this value and the robot
     *                     does not respond.
     * @param invertSign   Whether to invert the sign of the joystick value.
     */
    public JoyStick(JoyStickMode joyStickMode, double deadBand, InvertSign invertSign) {
        this.joyStickMode = joyStickMode;
        this.deadBand = deadBand;
        this.invertSign = invertSign;
        this.reductionFactor = 1;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Constructor
     *
     * @param joyStickMode The method for scaling the joystick.
     * @param deadBand     The deadband value for the joystick, anything less than this value and the robot
     *                     does not respond.
     */
    public JoyStick(JoyStickMode joyStickMode, double deadBand) {
        this.joyStickMode = joyStickMode;
        this.deadBand = deadBand;
        this.invertSign = InvertSign.NO_INVERT_SIGN;
        this.reductionFactor = 1;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Constructor
     *
     * @param joyStickMode The method for scaling the joystick.
     */
    public JoyStick(JoyStickMode joyStickMode) {
        this.joyStickMode = joyStickMode;
        this.deadBand = 0;
        this.invertSign = InvertSign.NO_INVERT_SIGN;
        this.reductionFactor = 1;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Constructor
     * <p>
     * Defaults to Square function for scaling, no deadband compensation, no sign inversion,
     * no output reduction
     */
    public JoyStick() {
        this.joyStickMode = JoyStickMode.SQUARE;
        this.deadBand = 0.15;
        this.invertSign = InvertSign.NO_INVERT_SIGN;
        this.reductionFactor = 1;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
    }

    public JoyStick(Gamepad gamepad, JoystickSide joystickSide, JoystickAxis joystickAxis) {
        this.joyStickMode = JoyStickMode.SQUARE;
        this.deadBand = 0.15;
        if (joystickAxis == JoystickAxis.X) {
            this.invertSign = InvertSign.NO_INVERT_SIGN;
        } else {
            this.invertSign = InvertSign.INVERT_SIGN;
        }
        this.reductionFactor = 1;
        this.toggleHalfPower = PowerReduction.FULL_POWER;
        this.toggleQuarterPower = PowerReduction.FULL_POWER;
        this.toggle_20_Power = PowerReduction.FULL_POWER;
        this.toggle_10_Power = PowerReduction.FULL_POWER;
        this.joystickAxis = joystickAxis;
        this.joystickSide = joystickSide;
        this.gamepad = gamepad;
    }
    //*********************************************************************************************
    //          UTILITY METHODS
    //*********************************************************************************************

    /**
     * Utility method that checks if the input is to be changed to the opposite sign
     *
     * @param joyStickValue
     * @return If the INVERT_SIGN is set, then returns input * -1
     */
    public double invertJoyStick(double joyStickValue) {
        if (this.invertSign == InvertSign.INVERT_SIGN) {
            return -joyStickValue;
        } else {
            return joyStickValue;
        }
    }

    //*********************************************************************************************
    //          MAIN METHOD TO CALL
    //*********************************************************************************************

    /**
     * Wrapper method that calls the proper scaling method. A switch is used in case other scaling
     * methods are added in the future.
     *
     * @param joyStickValue The value to be scaled.
     * @return The scaled input.
     */
    public double scaleInput(double joyStickValue) {
        double output = 0;

        switch (this.joyStickMode) {
            case LINEAR:
                output = scaleInputLinear(invertJoyStick(joyStickValue), this.reductionFactor);
                break;
            case SQUARE:
                output = scaleInputSquared(invertJoyStick(joyStickValue), this.reductionFactor, this.deadBand);
                break;
        }
        return output;
    }

    public double getValue() {
        double joystickValue = 0;
        if (joystickAxis == JoystickAxis.X) {
            if (joystickSide == JoystickSide.LEFT) {
                joystickValue = gamepad.left_stick_x;
            } else {
                joystickValue = gamepad.right_stick_x;
            }
        } else {
            if (joystickSide == JoystickSide.LEFT) {
                joystickValue = gamepad.left_stick_y;
            } else {
                joystickValue = gamepad.right_stick_y;
            }
        }
        return scaleInput(joystickValue);
    }

    //*********************************************************************************************
    //          SCALING METHODS
    //*********************************************************************************************

    // The joystick input will be a value from its min to its max.
    // In order to scale it to a motor power we need to scale it so that the joystick max
    // maps to the maximum motor power. For example if the max joystick is +127 and the max
    // motor power is +100, then we need to scale the joystick 127 down to 100:
    // power = joystick * (100/127).
    //
    // Sometimes the driver may want to cut the joystick command down. For example, they may
    // want to make it so full joystick only gives them 1/2 the max power. For that we can give
    // them a reductionFactor.

    // This function scales the joystick and applies any reduction factor. The output is a linear
    // function (a straight line if you graph output vs input. This may not be the best since
    // the driver will have less control at the lower joystick inputs.

    /**
     * @param joyStickValue
     * @param reductionFactor
     * @return
     */
    public static double scaleInputLinear(double joyStickValue, double reductionFactor) {
        // clip the joystick input to its min and max favlues
        joyStickValue = Range.clip(joyStickValue, JOYSTICK_MIN, JOYSTICK_MAX);
        // scale the joystick so that its max value maps to the max value of the motor power and
        // then apply the reduction factor
        joyStickValue = joyStickValue * OUTPUT_MAX / JOYSTICK_MAX * reductionFactor;
        // limit the output
        joyStickValue = Range.clip(joyStickValue, OUTPUT_MIN, OUTPUT_MAX);
        return joyStickValue;
    }

    // another version without the reduction factor (ie reduction factor = 1, full power).

    /**
     * Method that scales the input Linearly, with a default of no power reduction
     *
     * @param joyStickValue
     * @return
     */
    public static double scaleInputLinear(double joyStickValue) {
        // in this case the reduction factor = 1
        return scaleInputLinear(joyStickValue, 1);
    }

    // Use a square function to give more control at lower joystick values. Higher joystick values
    // have an output that is close to max.
    // Can remove deadband in the robot. This is when you give a joystick input but the robot
    // does not respond. The deadband is due to things like friction that has to be overcome before
    // movement can start. The deadband value has to be determined by experimenting with the robot.
    // The reductionFactor allows the driver to reduce the max output by a factor, like 1/2 power.
    // see https://ftc-team-3486.wikispaces.com/Joysticks for more details.

    /**
     * @param joyStickValue
     * @param reductionFactor
     * @param deadBand
     * @return
     */
    public static double scaleInputSquared(double joyStickValue, double reductionFactor, double deadBand) {
        double outputValue = 0;
        double maxSquaredTerm = 0;
        double deadBandTerm = 0;
        double signControl = 1;

        // in order to keep the square function from giving positive values for negative input
        // we need to make the output negative if the input is negative. And keep the output postive
        // if the input is positive. x / abs(x) will perform that trick for us.

        // if the joystick input is 0, then we have to hardwire the result of this signControl to 1,
        // otherwise we have a divde by 0 error.
        if (joyStickValue == 0) {
            signControl = 1;
        } else {
            signControl = joyStickValue / Math.abs(joyStickValue);
        }

        // clip the joystick input to its min and max values
        joyStickValue = Range.clip(joyStickValue, JOYSTICK_MIN, JOYSTICK_MAX);

        // square the input and then adjust its sign
        // This results in a factor between -1 and 1.
        outputValue = Math.pow((joyStickValue / JOYSTICK_MAX), 2) * signControl;

        // The output will be made up of 2 terms (portions):
        // 1 - the squared portion
        // 2 - the deadBand portion (shifting the curve up)
        // The total of the 2 portions cannot add up to more than the max output.
        // So if the deadband is .2, and the max ouptut is 1, then the max
        // contribution that the squared part can be is .8.
        // 0.8 + 0.2 = 1

        // If you have taken Alegbra II, this is a translation of a squared function in the y
        // direction.

        // Figure out the deadband portion of the output.
        // This will get added to the squared term to shift the curve up.
        // If the joystick value is very close to 0, the deadband term should not get added because
        // the output should be 0.
        if (Math.abs(joyStickValue) < .01 * JOYSTICK_MAX) {
            // since the input is less than 1% of the max, make the output 0
            deadBandTerm = 0;
        } else {
            deadBandTerm = deadBand;
        }

        // Figure out the max portion the squared term can be:
        maxSquaredTerm = OUTPUT_MAX - deadBandTerm;

        // Calculate the squared term and add the deadband term with its sign adjusted
        outputValue = outputValue * maxSquaredTerm + deadBandTerm * signControl;

        // Finally apply any reduction factor
        outputValue = outputValue * reductionFactor;

        // range clip the output - this should not be needed but just in case
        outputValue = Range.clip(outputValue, OUTPUT_MIN, OUTPUT_MAX);

        return outputValue;
    }

    public static double scaleInputSquared(double joyStickValue, double reductionFactor) {
        // deadband percent = 0
        return scaleInputSquared(joyStickValue, 0, reductionFactor);
    }

    public static double scaleInputSquared(double joyStickValue) {
        // deadband percent = 0
        // reduction factor = 1 (no reduction in power)
        return scaleInputSquared(joyStickValue, 0, 1);
    }

    /**
     * A small state machine that allows the reduction factor to toggle between .25 and 1 each time
     * the method is called.
     */
    public void toggleQuarterPower() {
        if (toggleQuarterPower == PowerReduction.FULL_POWER) {
            toggleQuarterPower = PowerReduction.QUARTER_POWER;
            reductionFactor = .25;
        } else {
            toggleQuarterPower = PowerReduction.FULL_POWER;
            reductionFactor = 1.0;
        }
    }

    /**
     * A small state machine that allows the reduction factor to toggle between .5 and 1 each time
     * the method is called.
     */
    public void toggleHalfPower() {
        if (toggleHalfPower == PowerReduction.FULL_POWER) {
            toggleHalfPower = PowerReduction.HALF_POWER;
            reductionFactor = .5;
        } else {
            toggleHalfPower = PowerReduction.FULL_POWER;
            reductionFactor = 1.0;
        }
    }

    /**
     * A small state machine that allows the reduction factor to toggle between .2 and 1 each time
     * the method is called.
     */
    public void toggle20PercentPower() {
        if (toggle_20_Power == PowerReduction.FULL_POWER) {
            toggle_20_Power = PowerReduction.TWENTY_PERCENT_POWER;
            reductionFactor = .2;
        } else {
            toggle_20_Power = PowerReduction.FULL_POWER;
            reductionFactor = 1.0;
        }
    }

    /**
     * A small state machine that allows the reduction factor to toggle between .1 and 1 each time
     * the method is called.
     */
    public void toggle10PercentPower() {
        if (toggle_10_Power == PowerReduction.FULL_POWER) {
            toggle_10_Power = PowerReduction.TEN_PERCENT_POWER;
            reductionFactor = .1;
        } else {
            toggle_10_Power = PowerReduction.FULL_POWER;
            reductionFactor = 1.0;
        }
    }

    /**
     * Set the power to full power
     */
    public void setFullPower() {
        reductionFactor = 1.0;
        toggleHalfPower = PowerReduction.FULL_POWER;
        toggleQuarterPower = PowerReduction.FULL_POWER;
        toggle_20_Power = PowerReduction.FULL_POWER;
        toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Set the power to 50%
     */
    public void setHalfPower() {
        reductionFactor = .5;
        toggleHalfPower = PowerReduction.HALF_POWER;
        toggleQuarterPower = PowerReduction.FULL_POWER;
        toggle_20_Power = PowerReduction.FULL_POWER;
        toggle_10_Power = PowerReduction.FULL_POWER;
    }

    public void set30PercentPower() {
        reductionFactor = .3;
        toggleHalfPower = PowerReduction.HALF_POWER;
        toggleQuarterPower = PowerReduction.FULL_POWER;
        toggle_20_Power = PowerReduction.FULL_POWER;
        toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Set the power to 25%
     */
    public void setQuarterPower() {
        reductionFactor = .25;
        toggleHalfPower = PowerReduction.FULL_POWER;
        toggleQuarterPower = PowerReduction.QUARTER_POWER;
        toggle_20_Power = PowerReduction.FULL_POWER;
        toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Set the power to 20%
     */
    public void set20PercentPower() {
        reductionFactor = .2;
        toggleHalfPower = PowerReduction.FULL_POWER;
        toggleQuarterPower = PowerReduction.FULL_POWER;
        toggle_20_Power = PowerReduction.TWENTY_PERCENT_POWER;
        toggle_10_Power = PowerReduction.FULL_POWER;
    }

    /**
     * Set the power to 10%
     */
    public void set10PercentPower() {
        reductionFactor = .1;
        toggleHalfPower = PowerReduction.FULL_POWER;
        toggleQuarterPower = PowerReduction.FULL_POWER;
        toggle_20_Power = PowerReduction.FULL_POWER;
        toggle_10_Power = PowerReduction.TEN_PERCENT_POWER;

    }
}
