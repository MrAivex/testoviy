package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Teleop_20090", group = "TeleOp")
public class Teleop2 extends LinearOpMode {

    ElapsedTime runtime = new ElapsedTime();
    DcMotor throw_motor = null;
    DcMotor leftUpperMotor = null;
    DcMotor rightUpperMotor = null;
    CRServo get_right = null;
    CRServo get_left = null;
    CRServo fd_roll_servo = null;
    CRServo bk_roll_servo = null;
    Servo rt_up_servo = null;
    Servo lt_up_servo = null;
    boolean aisa = false;

    boolean fa = false;
    boolean pepe = false;
    boolean sh = false;
    boolean flagUpper = false;
    boolean bkse = false;
    boolean ro = false;
    double oldTime = 0;

    @Override
    public void runOpMode() {
        // Используем тот же класс, что и в SplineTest
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        throw_motor = hardwareMap.get(DcMotor.class, "throw_motor");

        leftUpperMotor = hardwareMap.get(DcMotor.class, "left_upper_motor");
        rightUpperMotor = hardwareMap.get(DcMotor.class, "right_upper_motor");
        get_right = hardwareMap.get(CRServo.class, "get_right");
        get_left = hardwareMap.get(CRServo.class, "get_left");
        fd_roll_servo = hardwareMap.get(CRServo.class, "fd_roll_servo");
        bk_roll_servo = hardwareMap.get(CRServo.class, "bk_roll_servo");
        rt_up_servo = hardwareMap.get(Servo.class, "rt_up_servo");
        lt_up_servo = hardwareMap.get(Servo.class, "lt_up_servo");

        throw_motor.setDirection(DcMotor.Direction.FORWARD);

        rt_up_servo.setDirection(Servo.Direction.REVERSE);
        lt_up_servo.setDirection(Servo.Direction.FORWARD);
        leftUpperMotor.setDirection(DcMotor.Direction.FORWARD);
        rightUpperMotor.setDirection(DcMotor.Direction.FORWARD);
        get_right.setDirection(CRServo.Direction.FORWARD);
        get_left.setDirection(CRServo.Direction.REVERSE);
        fd_roll_servo.setDirection(CRServo.Direction.FORWARD);
        bk_roll_servo.setDirection(CRServo.Direction.REVERSE);


        waitForStart();
        runtime.reset();
        resetRuntime();

        while (opModeIsActive()) {
            if ( ){

            }
            if (gamepad1.b && !sh && runtime.seconds() > 0.2) {
                sh = true;
                throw_motor.setPower(0.6);
                runtime.reset();
                gamepad1.
            }
            if (gamepad1.b && sh == true && runtime.seconds() > 0.2) {
                sh = false;
                throw_motor.setPower(0);
                runtime.reset();
            }
            if (gamepad1.y && fa == false && runtime.seconds() > 0.2) {
                fa = true;
                fd_roll_servo.setPower(-1);
                bk_roll_servo.setPower(1);
                runtime.reset();
            }
            if (gamepad1.y && fa == true && runtime.seconds() > 0.2) {
                fa = false;
                fd_roll_servo.setPower(0);
                bk_roll_servo.setPower(0);
                runtime.reset();
            }
            if (gamepad1.dpad_up) {
                leftUpperMotor.setPower(-0.5);
                leftUpperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftUpperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftUpperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                leftUpperMotor.setTargetPosition(100);


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

            if (gamepad1.left_bumper) {
                rt_up_servo.setPosition(0.6);
                lt_up_servo.setPosition(0.45);
                fd_roll_servo.setPower(0);
            } else {
                rt_up_servo.setPosition(0.3);
                lt_up_servo.setPosition(0.15);
                if (!gamepad1.y){
                    fd_roll_servo.setPower(1);
                }
            }

            if (gamepad1.right_bumper && ro == false && runtime.seconds() > 0.2) {
                ro = true;
                get_right.setPower(1);
                get_left.setPower(1);
                runtime.reset();
            }
            if (gamepad1.right_bumper && ro == true && runtime.seconds() > 0.2) {
                ro = false;
                get_right.setPower(0);
                get_left.setPower(0);
                runtime.reset();
            }
            if (gamepad1.x && pepe == false && runtime.seconds() > 0.2) {
                pepe = true;
                fd_roll_servo.setPower(1);
                bk_roll_servo.setPower(-1);
                runtime.reset();
            }
            if (gamepad1.x && pepe == true && runtime.seconds() > 0.2) {
                pepe = false;
                fd_roll_servo.setPower(0);
                bk_roll_servo.setPower(0);
                runtime.reset();
            }

//            if (gamepad1.left_bumper && bkse == false && runtime.seconds() > 0.2) {
//                bkse = true;
//                fd_roll_servo.setPower(1);
//                get_right.setPower(1);
//                get_left.setPower(1);
//                runtime.reset();
//            }
//            if (gamepad1.left_bumper && bkse == true && runtime.seconds() > 0.2) {
//                bkse = false;
//                fd_roll_servo.setPower(0);
//                get_right.setPower(0);
//                get_left.setPower(0);
//                runtime.reset();
//            }



                // 1. Обновляем расчеты одометрии
                drive.updatePoseEstimate();

// 2. Достаем координаты через локализатор (БЕЗ использования .pose)
                Pose2d currentPose = drive.localizer.getPose();
                double botHeading = currentPose.heading.toDouble();

// 3. Сброс угла (кнопка OPTIONS или START)
                if (gamepad1.options || gamepad1.start) {
                    // Внимание: если здесь .pose тоже выдаст ошибку, используй:
                    // drive.localizer.setPose(new Pose2d(0, 0, 0));
                    drive.localizer.setPose(new Pose2d(0, 0, 0));
                }

// 4. Считываем стики
                double y = -gamepad1.left_stick_y;
                double x = -gamepad1.left_stick_x;
                double rx = -gamepad1.right_stick_x;

// Мертвая зона
                if (Math.abs(y) < 0.1) y = 0;
                if (Math.abs(x) < 0.1) x = 0;
                if (Math.abs(rx) < 0.1) rx = 0;

                // 5. Математика Field Centric
                // Поворачиваем ввод с джойстика на текущий угол робота
                double rotX = x * Math.cos(0) - y * Math.sin(0); //botHeading
                double rotY = x * Math.sin(0) + y * Math.cos(0); //botHeading

                // 6. Подача мощности
                if (y == 0 && x == 0 && rx == 0) {
                    drive.setDrivePowers(new PoseVelocity2d(new Vector2d(0, 0), 0));
                } else {
                    drive.setDrivePowers(new PoseVelocity2d(
                            new Vector2d(rotY, rotX),
                            rx
                    ));
                }
            }
        }
    }