package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "goBILDA Servo Test", group = "Test")
public class GoBildaServoTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Set the servo's configuration name to test_servo on the Driver Station.
        CRServo servo = hardwareMap.get(CRServo.class, "test_servo");
        servo.setPower(0);

        telemetry.addLine("Hold A: spin one direction");
        telemetry.addLine("Hold B: spin the other direction");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            double power = 0;
            if (gamepad1.a && !gamepad1.b) {
                power = 0.5;
            } else if (gamepad1.b && !gamepad1.a) {
                power = -0.5;
            }
            servo.setPower(power);
            telemetry.addData("Servo power", power);
            telemetry.update();
        }
        servo.setPower(0);
    }
}
