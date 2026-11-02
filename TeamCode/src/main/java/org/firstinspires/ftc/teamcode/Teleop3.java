package org.firstinspires.ftc.teamcode;

import static java.lang.Double.max;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TELEOP_20090", group = "TeleOp")
public class Teleop3 extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);

        DcMotor throw_motor = hardwareMap.get(DcMotor.class, "throw_motor");

        DcMotor leftUpperMotor = hardwareMap.get(DcMotor.class, "left_upper_motor");
        DcMotor rightUpperMotor = hardwareMap.get(DcMotor.class, "right_upper_motor");
        CRServo get_right = hardwareMap.get(CRServo.class, "get_right");
        CRServo get_left = hardwareMap.get(CRServo.class, "get_left");
        CRServo fd_roll_servo = hardwareMap.get(CRServo.class, "fd_roll_servo");
        CRServo bk_roll_servo = hardwareMap.get(CRServo.class, "bk_roll_servo");
        Servo rt_up_servo = hardwareMap.get(Servo.class, "rt_up_servo");
        Servo lt_up_servo = hardwareMap.get(Servo.class, "lt_up_servo");

        throw_motor.setDirection(DcMotor.Direction.FORWARD);

        rt_up_servo.setDirection(Servo.Direction.REVERSE);
        lt_up_servo.setDirection(Servo.Direction.FORWARD);
        leftUpperMotor.setDirection(DcMotor.Direction.FORWARD);
        rightUpperMotor.setDirection(DcMotor.Direction.FORWARD);
        get_right.setDirection(CRServo.Direction.FORWARD);
        get_left.setDirection(CRServo.Direction.REVERSE);
        fd_roll_servo.setDirection(CRServo.Direction.FORWARD);
        bk_roll_servo.setDirection(CRServo.Direction.REVERSE);

        leftUpperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightUpperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        boolean oldB = false;
        boolean throwState = true;

        boolean oldY = false;
        boolean rollState = false;

        waitForStart();
        resetRuntime();

        throw_motor.setPower(0.6);

        while (opModeIsActive()) {
            if (gamepad1.b != oldB && gamepad1.b) {
                throwState = !throwState;

                throw_motor.setPower(throwState ? 0.6 : 0.0);
            }

            oldB = gamepad1.b;

            if (oldY != gamepad1.y && gamepad1.y && !gamepad1.left_bumper) {
                rollState = !rollState;

                if (rollState) {
                    get_right.setPower(1);
                    get_left.setPower(1);
                } else {
                    get_right.setPower(0);
                    get_left.setPower(0);
                }
            }

            if (gamepad1.left_bumper) {
                fd_roll_servo.setPower(1);
                bk_roll_servo.setPower(-1);
            } else {
                if (rollState) {
                    fd_roll_servo.setPower(-1);
                    bk_roll_servo.setPower(1);
                } else {
                    fd_roll_servo.setPower(0);
                    bk_roll_servo.setPower(0);
                }
            }

            oldY = gamepad1.y;

            if (gamepad1.dpad_up) {
                leftUpperMotor.setPower(-0.5);
                rightUpperMotor.setPower(-0.5);
            } else if (gamepad1.dpad_down) {
                leftUpperMotor.setPower(0.9);
                rightUpperMotor.setPower(0.6);
            } else if (gamepad1.dpad_right) {
                rightUpperMotor.setPower(-0.5);
            } else if (gamepad1.dpad_left) {
                leftUpperMotor.setPower(-0.5);
            } else {
                leftUpperMotor.setPower(0);
                rightUpperMotor.setPower(0);
            }

            double y = -gamepad1.left_stick_y;
            double x = -gamepad1.left_stick_x;
            double rx = -gamepad1.right_stick_x;

            y *= abs(y);
            x *= abs(x);
            rx *= abs(rx);

            double leftFrontPower = x - y - rx;
            double rightBackPower = x - y + rx;
            double leftBackPower = x + y - rx;
            double rightFrontPower = x + y + rx;

            double m = max(abs(leftFrontPower), max(abs(rightBackPower), max(abs(leftBackPower), abs(rightFrontPower))));

            if(m > 1.0){
                leftFrontPower /= m;
                rightBackPower /= m;
                leftBackPower /= m;
                rightFrontPower /= m;
            }

            leftFront.setPower(leftFrontPower);
            rightBack.setPower(rightBackPower);
            leftBack.setPower(leftBackPower);
            rightFront.setPower(rightFrontPower);
        }
    }
}