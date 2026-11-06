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

import com.qualcomm.robotcore.eventloop.opmode.*;

@TeleOp(name = "24161 BioBuzz TeleOp", group = "24161")
public class BioBuzzTeleOp24161 extends OpMode {
    private final BioBuzzHardware24161 robot = new BioBuzzHardware24161();
    @Override public void init() {
        robot.init(hardwareMap);
        telemetry.addLine("Left stick: drive/strafe; right stick: turn; LB: slow");
        telemetry.addLine("RT: intake; LT: reverse; RB: launch; B: hold to stop all");
    }
    private double deadband(double value) {
        return Math.abs(value) < 0.05 ? 0 : value;
    }
    @Override public void loop() {
        if (gamepad1.b) {
            robot.stop();
            telemetry.addLine("Stopped while B held; release controls before releasing B");
        } else {
            robot.drive(deadband(-gamepad1.left_stick_y), deadband(gamepad1.left_stick_x),
                    deadband(gamepad1.right_stick_x), gamepad1.left_bumper ? 0.30 : 1.0);
            boolean shooting = gamepad1.right_bumper;
            robot.launcher(shooting);
            boolean feeding = shooting && robot.ready() && gamepad1.left_trigger < 0.05;
            robot.feed(feeding);
            double intakePower = gamepad1.right_trigger - gamepad1.left_trigger;
            robot.intake(feeding ? Math.min(1, intakePower + 0.5) : intakePower);
            telemetry.addData("Feeding", feeding);
        }
        telemetry.addData("Launcher ticks/sec", "%.0f / %.0f",
                robot.launcher.getVelocity(), BioBuzzHardware24161.TARGET_TPS);
        telemetry.addData("Launcher ready", robot.ready());
    }
    @Override public void stop() { robot.stop(); }
}
