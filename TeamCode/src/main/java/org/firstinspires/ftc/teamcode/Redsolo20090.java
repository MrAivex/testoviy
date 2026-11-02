package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;

@Config
@Autonomous(name = "Soloauto_red", group = "Autonom")
public class Redsolo20090 extends LinearOpMode {

    public static double X_toballs = -20;
    public static double Y_toballs = -47;

    public static double X_throw = 1;
    public static double Y_throw = 0;

    public static double X_takeballs = 10;

    public static double timetoshoot_first = 9;
    public static double timetoshoot = 9;

    DcMotor throw_motor = null;
    DcMotor leftUpperMotor  = null;
    DcMotor rightUpperMotor = null;
    CRServo servo1  = null;
    CRServo servo2  = null;
    CRServo fd_roll_servo = null;
    CRServo bk_roll_servo = null;
    CRServo rt_up_servo = null;
    CRServo lt_up_servo = null;


    public class Gun{
        private DcMotor gun; // не помню имя мотора
        public Gun(HardwareMap hardwareMap) {
            gun = hardwareMap.get(DcMotor.class, "throw_motor");
            gun.setDirection(DcMotor.Direction.FORWARD);
            gun.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        public Action ActGun(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    gun.setPower(0.8);
                    return false;
                }
            };
        }
        public Action StopGun() {
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    gun.setPower(0);
                    return false;
                }
            };
        }
    }

    public class Balls {

        public Balls(HardwareMap hardwareMap) {
            servo1 = hardwareMap.get(CRServo.class, "servo0");
            servo2 = hardwareMap.get(CRServo.class, "servo2");
            fd_roll_servo = hardwareMap.get(CRServo.class, "fd_roll_servo");
            bk_roll_servo = hardwareMap.get(CRServo.class, "bk_roll_servo");
            rt_up_servo = hardwareMap.get(CRServo.class, "rt_up_servo");
            lt_up_servo = hardwareMap.get(CRServo.class, "lt_up_servo");

            servo1.setDirection(CRServo.Direction.FORWARD);
            servo2.setDirection(CRServo.Direction.REVERSE);
            fd_roll_servo.setDirection(CRServo.Direction.FORWARD);
            bk_roll_servo.setDirection(CRServo.Direction.REVERSE);
            lt_up_servo.setDirection(CRServo.Direction.FORWARD);
            rt_up_servo.setDirection(CRServo.Direction.REVERSE);
        }

        public Action TakeOn(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(1);
                    servo1.setPower(1);
                    servo2.setPower(1);
                    return false;
                }
            };
        }

        public Action TakeOff(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(0);
                    servo1.setPower(0);
                    servo2.setPower(0);
                    return false;
                }
            };
        }

        public Action ToGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    bk_roll_servo.setPower(1);
                    rt_up_servo.setPower(1);
                    lt_up_servo.setPower(1);
                    return false;
                }
            };
        }

        public Action NoGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    bk_roll_servo.setPower(0);
                    rt_up_servo.setPower(0);
                    lt_up_servo.setPower(0);
                    return false;
                }
            };
        }
    }

    @Override
    public void runOpMode() {



        // Начальная позиция: (0, 0), робот смотрит ВПРАВО (0 градусов)
        Pose2d beginPose = new Pose2d(0, 0, Math.toRadians(45));

        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Gun Gun = new Gun(hardwareMap);
        Balls Robot = new Balls(hardwareMap);


        // Строим цепочку действий
        Action trajectory = drive.actionBuilder(beginPose)

                // 1. ПРЯМАЯ (STRAFE) - Ехать боком/прямо без поворота корпуса
                // Робот сместится в точку (20, 0), но нос будет смотреть туда же (0 рад)
                .lineToX(20)

                // 2. SPLINE (КЛАССИКА) - Ехать "как машина"
                // Робот поедет по дуге в точку (40, 20).
                // Нос робота будет поворачиваться по ходу движения.
                // Math.PI / 2 (90 град) - это касательная (куда робот должен "влететь" в точку)
                .splineTo(new Vector2d(40, 20), Math.PI / 2)
                .waitSeconds(4)
                // 3. SPLINE CONSTANT HEADING - Ехать по дуге "лунной походкой"
                // Робот сделает дугу к точке (20, 40), но его НОС останется смотреть
                // в ту же сторону, где закончил предыдущий шаг (на 90 градусов)
                // tangent = Math.PI (180 град) задает форму дуги (выгибается влево)
                .splineToConstantHeading(new Vector2d(20, 40), Math.PI)
                .waitSeconds(4)
                // 4. SPLINE LINEAR HEADING - Самое сложное и крутое
                // Едем в точку (0, 0)
                // При этом плавно крутимся, чтобы в конце смотреть на 180 градусов (Math.PI)
                // Tangent (Math.PI * 1.5) задает подход к точке снизу
                .splineToLinearHeading(new Pose2d(0, 0, Math.PI), Math.PI * 1.5)
                .waitSeconds(4)
                // 5. TURN - Просто разворот на месте
                // Повернуться на 90 градусов (радианы) против часовой
                .turn(Math.toRadians(90))
                .waitSeconds(4)
                .build();

        Action toballs = drive.actionBuilder(beginPose).stopAndAdd(Gun.ActGun()).stopAndAdd(Robot.TakeOn()).stopAndAdd(Robot.ToGun()).setTangent(Math.toRadians(-90)).splineToLinearHeading(new Pose2d(X_throw, Y_throw+1, Math.PI/4), 0)
                .waitSeconds(timetoshoot_first).stopAndAdd(Robot.NoGun()).stopAndAdd(Gun.StopGun()) //стреляем
                .setTangent(Math.toRadians(180+45)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs, 0), -Math.PI/2) //отъезжаем
                .setTangent(0).lineToX(X_takeballs,new TranslationalVelConstraint(8.5)).stopAndAdd(Robot.ToGun())//.stopAndAdd(Robot.TakeOff()) //берем шарики

                .setTangent(Math.toRadians(90)).splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.PI/4), Math.PI/4) //едем к пушке
                .stopAndAdd(Gun.ActGun()).waitSeconds(timetoshoot).stopAndAdd(Robot.NoGun()).stopAndAdd(Gun.StopGun()) //стреляем

                .setTangent(Math.toRadians(180+45)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs-24, 0), -Math.PI/2) //отъезжаем ко вторым
                .setTangent(0).lineToX(X_takeballs)//.stopAndAdd(Robot.TakeOff()) //берем шарики второй раз

                .setTangent(Math.toRadians(90)).splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.PI/4), Math.PI/4)
                .stopAndAdd(Gun.ActGun()).stopAndAdd(Robot.ToGun()).waitSeconds(timetoshoot).stopAndAdd(Robot.NoGun()).stopAndAdd(Gun.StopGun())//стреляем

                .setTangent(Math.toRadians(180+45)).splineToLinearHeading(new Pose2d(X_toballs, Y_toballs-48, 0), -Math.PI/2) //отъезжаем ко вторым
                .setTangent(0).lineToX(X_takeballs)//.stopAndAdd(Robot.TakeOff()) //берем шарики третий раз

                .setTangent(Math.toRadians(90)).splineToLinearHeading(new Pose2d(X_throw, Y_throw, Math.PI/4), Math.PI/4) //едем к пушке
                .stopAndAdd(Gun.ActGun()).stopAndAdd(Robot.ToGun()).waitSeconds(timetoshoot).stopAndAdd(Robot.NoGun()).stopAndAdd(Gun.StopGun())

                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(toballs);
    }
}