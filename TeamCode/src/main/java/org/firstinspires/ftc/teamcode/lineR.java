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
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous(name = "NOLINERed", group = "Autonom")
public class lineR extends LinearOpMode {

    public static double X_toballs = -20;
    public static double Y_toballs = 45;

    public static double X_throw = 0;
    public static double Y_throw = 0;

    public static double X_takeballs = 3;

    public static double timetoshoot_first = 9;
    public static double timetoshoot = 9;

    DcMotor throw_motor = null;
    DcMotor leftUpperMotor  = null;
    DcMotor rightUpperMotor = null;
    CRServo get_right  = null;
    CRServo get_left  = null;
    CRServo fd_roll_servo = null;
    CRServo bk_roll_servo = null;
    Servo rt_up_servo = null;
    Servo lt_up_servo = null;


    public class Gun{
        private DcMotor gun;
        public Gun(HardwareMap hardwareMap) {
            gun = hardwareMap.get(DcMotor.class, "throw_motor");
            gun.setDirection(DcMotor.Direction.FORWARD);
            gun.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        public Action ActGun(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    gun.setPower(0.6);
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
            get_right = hardwareMap.get(CRServo.class, "get_right");
            get_left = hardwareMap.get(CRServo.class, "get_left");
            fd_roll_servo = hardwareMap.get(CRServo.class, "fd_roll_servo");
            bk_roll_servo = hardwareMap.get(CRServo.class, "bk_roll_servo");
            rt_up_servo = hardwareMap.get(Servo.class, "rt_up_servo");
            lt_up_servo = hardwareMap.get(Servo.class, "lt_up_servo");

            get_right.setDirection(CRServo.Direction.FORWARD);
            get_left.setDirection(CRServo.Direction.REVERSE);
            fd_roll_servo.setDirection(CRServo.Direction.FORWARD);
            bk_roll_servo.setDirection(CRServo.Direction.REVERSE);
            lt_up_servo.setDirection(Servo.Direction.FORWARD);
            rt_up_servo.setDirection(Servo.Direction.REVERSE);
        }

        public Action RollTakeOn(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(1);
                    bk_roll_servo.setPower(-1);
                    return false;
                }
            };
        }
        
        public Action RollTakeOff(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    fd_roll_servo.setPower(0);
                    bk_roll_servo.setPower(0);
                    return false;
                }
            };
        }
        public Action ScTakeOn(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    get_right.setPower(1);
                    get_left.setPower(1);
                    return false;
                }
            };
        }

        public Action ScTakeOff(){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    get_right.setPower(0);
                    get_left.setPower(0);
                    return false;
                }
            };
        }

        public Action ToGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    rt_up_servo.setPosition(0.6);
                    lt_up_servo.setPosition(0.5);
                    return false;
                }
            };
        }

        public Action NoGun(){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    rt_up_servo.setPosition(0.3);
                    lt_up_servo.setPosition(0.15);
                    return false;
                }
            };
        }
    }

    @Override
    public void runOpMode() {



        // Начальная позиция: (0, 0), робот смотрит ВПРАВО (0 градусов)
        Pose2d beginPose = new Pose2d(0, 0, Math.toRadians(90-53.28));

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

        Action toballs = drive.actionBuilder(beginPose).lineToX(-6).lineToY(-6)


                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(toballs);
    }
}