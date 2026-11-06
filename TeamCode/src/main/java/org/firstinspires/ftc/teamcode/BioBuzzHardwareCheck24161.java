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
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name = "24161 BioBuzz Hardware Check", group = "24161")
public class BioBuzzHardwareCheck24161 extends OpMode {
    private final BioBuzzHardware24161 robot = new BioBuzzHardware24161();
    private int selected;
    private boolean previousLeft, previousRight;
    private final String[] names = {"front left", "front right", "back left", "back right",
            "intake motor", "left intake servo", "right intake servo", "windmill servo", "launcher"};
    @Override public void init() { robot.init(hardwareMap); }
    @Override public void loop() {
        if (gamepad1.dpad_right && !previousRight) selected = (selected + 1) % names.length;
        if (gamepad1.dpad_left && !previousLeft) selected = (selected + names.length - 1) % names.length;
        previousLeft = gamepad1.dpad_left;
        previousRight = gamepad1.dpad_right;
        robot.stop();
        double power = gamepad1.b ? 0 : (gamepad1.a ? 0.20 : gamepad1.x ? -0.20 : 0);
        DcMotor[] motors = {robot.frontLeft, robot.frontRight, robot.backLeft, robot.backRight, robot.intake};
        if (selected < 5) motors[selected].setPower(power);
        else if (selected < 8) {
            CRServo[] servos = {robot.leftIntake, robot.rightIntake, robot.windmill};
            servos[selected - 5].setPower(power);
        } else robot.launcher.setVelocity(power * BioBuzzHardware24161.TARGET_TPS);
        telemetry.addData("Selected", names[selected]);
        telemetry.addLine("D-pad left/right selects; hold A: +20%; X: -20%; B: stop");
        telemetry.addData("Launcher encoder ticks/sec", robot.launcher.getVelocity());
        telemetry.addLine("Test with wheels clear, hopper empty. Launcher test is low-speed only.");
    }
    @Override public void stop() { robot.stop(); }
}
