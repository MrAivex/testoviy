package org.firstinspires.ftc.teamcode;

import static java.lang.Double.max;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TEL_20090", group = "TeleOp")
public class TeleopDrive extends LinearOpMode{
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

        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
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
