package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Config
@Autonomous(name = "AutoMeterSpin", group = "Tests")
public class AutoMeterSpin extends LinearOpMode {

    // Road Runner работает в ДЮЙМАХ, поэтому 1 метр = 39.37 дюйма
    public static double forwardInches = 39.37;

    // Количество оборотов на месте
    public static int spins = 5;

    @Override
    public void runOpMode() {

        // Начальная позиция: (0, 0), робот смотрит вдоль оси +X (вперед)
        Pose2d beginPose = new Pose2d(0, 0, 0);

        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        Action trajectory = drive.actionBuilder(beginPose)

                // 1. Едем ВПЕРЕД на 1 метр (по оси +X, heading не меняется)
                .lineToX(forwardInches)

                // 2. Крутимся НА МЕСТЕ: 5 оборотов = 5 * 360° = 1800° = 10 * PI радиан
                //    (положительное значение = против часовой стрелки)
                .turn(spins * 2 * Math.PI)

                // 3. Возвращаемся НАЗАД в начальную точку (X = 0)
                .lineToX(0)

                .build();

        telemetry.addLine("Готово! Нажмите PLAY");
        telemetry.update();
        waitForStart();

        if (isStopRequested()) return;

        // Запускаем всю цепочку действий
        Actions.runBlocking(trajectory);
    }
}