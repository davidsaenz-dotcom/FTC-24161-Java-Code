/*   MIT License
 *   Copyright (c) [2026] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;

/** Shared hardware for the stock goBILDA BIOBUZZ mecanum StarterBot. */
public final class BioBuzzHardware24161 {
    public static final double TARGET_TPS = 1250, MIN_TPS = 1200;
    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcher;
    public CRServo leftIntake, rightIntake, windmill;

    public void init(HardwareMap map) {
        frontLeft = map.get(DcMotor.class, "left_front_drive");
        frontRight = map.get(DcMotor.class, "right_front_drive");
        backLeft = map.get(DcMotor.class, "left_back_drive");
        backRight = map.get(DcMotor.class, "right_back_drive");
        intake = map.get(DcMotor.class, "intake");
        launcher = map.get(DcMotorEx.class, "launcher");
        leftIntake = map.get(CRServo.class, "left_intake_servo");
        rightIntake = map.get(CRServo.class, "right_intake_servo");
        windmill = map.get(CRServo.class, "windmillServo");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        windmill.setDirection(DcMotorSimple.Direction.REVERSE);
        for (DcMotor motor : new DcMotor[]{frontLeft, frontRight, backLeft, backRight, intake}) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        launcher.setPower(0);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, 12.5));
        stop();
    }
    private double clip(double power) { return Math.max(-1, Math.min(1, power)); }
    public void drive(double forward, double strafe, double turn, double scale) {
        double fl = forward + strafe + turn, fr = forward - strafe - turn;
        double bl = forward - strafe + turn, br = forward + strafe - turn;
        double denominator = Math.max(1, Math.max(Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))));
        scale = Math.max(0, Math.min(1, scale));
        frontLeft.setPower(fl / denominator * scale);
        frontRight.setPower(fr / denominator * scale);
        backLeft.setPower(bl / denominator * scale);
        backRight.setPower(br / denominator * scale);
    }
    public void intake(double power) {
        intake.setPower(clip(power));
        leftIntake.setPower(clip(power));
        rightIntake.setPower(clip(power));
    }
    public void launcher(boolean enabled) { launcher.setVelocity(enabled ? TARGET_TPS : 0); }
    public boolean ready() { return launcher.getVelocity() >= MIN_TPS; }
    public void feed(boolean enabled) { windmill.setPower(enabled ? 1 : 0); }
    public void stop() {
        drive(0, 0, 0, 0);
        intake(0);
        launcher(false);
        feed(false);
    }
}
