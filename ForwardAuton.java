package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Drives forward, pauses, drives backward, then stops. No encoders required. */
@Autonomous(name = "Forward Auton", group = "Team 24161")
public class ForwardAuton extends LinearOpMode {
    // Tune these after testing. Time controls travel duration, not exact distance.
    private static final double DRIVE_POWER = 0.25;
    private static final double FORWARD_SECONDS = 1.5;
    private static final double BACKWARD_SECONDS = 1.5;

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "left_front_drive");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "right_front_drive");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "left_back_drive");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "right_back_drive");
        DcMotor[] motors = {leftFront, rightFront, leftBack, rightBack};

        // Same directions as BioBuzzStarterbotTeleopMecanum.
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        for (DcMotor motor : motors) {
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addLine("Ready: moves forward only after START.");
        telemetry.addData("Power", DRIVE_POWER);
        telemetry.addData("Forward seconds", FORWARD_SECONDS);
        telemetry.addData("Backward seconds", BACKWARD_SECONDS);
        telemetry.update();
        waitForStart();

        try {
            if (isStopRequested()) {
                return;
            }
            driveFor(motors, DRIVE_POWER, FORWARD_SECONDS);
            if (opModeIsActive()) {
                sleep(250); // Brief stop before reversing.
            }
            if (opModeIsActive()) {
                driveFor(motors, -DRIVE_POWER, BACKWARD_SECONDS);
            }
        } finally {
            // Stop on completion, STOP button, or interruption.
            for (DcMotor motor : motors) {
                motor.setPower(0);
            }
        }
        telemetry.addLine("Finished: drive motors stopped.");
        telemetry.update();
    }

    private void driveFor(DcMotor[] motors, double power, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < seconds) {
            for (DcMotor motor : motors) {
                motor.setPower(power);
            }
            idle();
        }
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }
}
