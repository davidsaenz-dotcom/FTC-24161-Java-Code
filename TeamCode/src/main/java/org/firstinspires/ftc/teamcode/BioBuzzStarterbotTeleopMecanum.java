/*
 * MIT License
 * Copyright (c) [2026] [Base 10 Assets, LLC]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
public class BioBuzzStarterbotTeleopMecanum extends OpMode {

    private DcMotor leftFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;

    private DcMotorEx launcher;
    private DcMotor intake;

    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;
    private CRServo windmillServo;

    private static final double LAUNCHER_TARGET_VELOCITY = 1250;
    private static final double LAUNCHER_MIN_VELOCITY = 1200;

    private double leftFrontPower;
    private double rightFrontPower;
    private double leftBackPower;
    private double rightBackPower;

    @Override
    public void init() {
        leftFrontDrive =
                hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive =
                hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive =
                hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive =
                hardwareMap.get(DcMotor.class, "right_back_drive");

        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        leftIntakeServo =
                hardwareMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo =
                hardwareMap.get(CRServo.class, "right_intake_servo");
        windmillServo =
                hardwareMap.get(CRServo.class, "windmillServo");

        // These directions depend on your drivetrain assembly.
        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        DcMotor[] driveMotors = {
                leftFrontDrive,
                leftBackDrive,
                rightFrontDrive,
                rightBackDrive
        };

        for (DcMotor motor : driveMotors) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        intake.setPower(0);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launcher.setPower(0);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, 12.5)
        );
        launcher.setVelocity(0);

        leftIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        rightIntakeServo.setDirection(DcMotorSimple.Direction.FORWARD);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);

        telemetry.addData("Status", "Ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Left stick: drive and strafe. Right stick: turn.
        mecanumDrive(
                -gamepad1.left_stick_x,
                gamepad1.left_stick_y,
                gamepad1.right_stick_x
        );

        double intakePower =
                gamepad1.left_trigger - gamepad1.right_trigger;

        // Hold right bumper to spin the launcher.
        boolean launcherOn = gamepad1.right_bumper;

        launcher.setVelocity(
                launcherOn ? LAUNCHER_TARGET_VELOCITY : 1
        );

        double launcherVelocity = launcher.getVelocity();

        boolean launcherReady =
                launcherOn
                && launcherVelocity >= LAUNCHER_MIN_VELOCITY;

        // Left bumper manually runs the feeder.
        // Right bumper feeds automatically once the launcher is ready.
        boolean feed = gamepad1.left_bumper || launcherReady;

        windmillServo.setPower(feed ? 1.0 : 0.0);

        if (feed) {
            intakePower -= 0.5;
        }

        intakePower = Math.max(-1.0, Math.min(1.0, intakePower));

        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

        telemetry.addData("Front wheels", "L %.2f | R %.2f",
                leftFrontPower, rightFrontPower);
        telemetry.addData("Back wheels", "L %.2f | R %.2f",
                leftBackPower, rightBackPower);
        telemetry.addData("Launcher velocity", "%.0f", launcherVelocity);
        telemetry.addData("Launcher ready", launcherReady);
        telemetry.addData("Windmill running", feed);
        telemetry.addData("Intake power", "%.2f", intakePower);
        telemetry.update();
    }

    private void mecanumDrive(
            double forward,
            double strafe,
            double rotate
    ) {
        forward = deadband(forward);
        strafe = deadband(strafe);
        rotate = deadband(rotate);

        // Hold A for slower, precise movement.
        double speed = gamepad1.a ? 0.35 : 0.75;

        double denominator = Math.max(
                Math.abs(forward)
                        + Math.abs(strafe)
                        + Math.abs(rotate),
                1.0
        );

        leftFrontPower =
                (forward + strafe + rotate) / denominator * speed;
        rightFrontPower =
                (forward - strafe + rotate) / denominator * speed;
        leftBackPower =
                (forward - strafe - rotate) / denominator * speed;
        rightBackPower =
                (forward + strafe - rotate) / denominator * speed;

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }

    private double deadband(double value) {
        double threshold = 0.05;

        if (Math.abs(value) <= threshold) {
            return 0;
        }

        return Math.copySign(
                (Math.abs(value) - threshold) / (1.0 - threshold),
                value
        );
    }

    @Override
    public void stop() {
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);

        launcher.setVelocity(0);
        launcher.setPower(0);
        intake.setPower(0);

        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);
    }
}