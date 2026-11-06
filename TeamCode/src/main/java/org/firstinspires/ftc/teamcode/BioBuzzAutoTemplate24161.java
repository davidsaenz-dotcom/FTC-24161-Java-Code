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
import com.qualcomm.robotcore.util.ElapsedTime;

/** Disabled until the team measures and validates its starting pose and route. */
@Disabled
@Autonomous(name = "24161 BioBuzz Auto TEMPLATE", group = "24161")
public class BioBuzzAutoTemplate24161 extends LinearOpMode {
    // Defaults intentionally do nothing. Tune on your field before removing @Disabled.
    private static final boolean SHOOT_PRELOAD = false;
    private static final double SHOOT_SECONDS = 3.0;
    private static final double DRIVE_SECONDS = 0.0;
    private static final double DRIVE_POWER = 0.20;
    private final BioBuzzHardware24161 robot = new BioBuzzHardware24161();
    @Override public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        try {
            telemetry.addLine("Template: pre-aim at a tested close-range target before enabling shooting.");
            telemetry.update();
            waitForStart();
            if (isStopRequested()) return;
            if (SHOOT_PRELOAD) {
                robot.launcher(true);
                ElapsedTime spinup = new ElapsedTime();
                while (opModeIsActive() && !robot.ready() && spinup.seconds() < 4.0) idle();
                if (opModeIsActive() && robot.ready()) {
                    ElapsedTime shooting = new ElapsedTime();
                    while (opModeIsActive() && shooting.seconds() < SHOOT_SECONDS) {
                        boolean ready = robot.ready();
                        robot.feed(ready);
                        robot.intake(ready ? 0.5 : 0);
                        idle();
                    }
                }
                robot.stop();
            }
            ElapsedTime driving = new ElapsedTime();
            while (opModeIsActive() && driving.seconds() < DRIVE_SECONDS) {
                robot.drive(DRIVE_POWER, 0, 0, 1);
                idle();
            }
        } finally { robot.stop(); }
    }
}
