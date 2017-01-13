package examples.raspberryPi1.brickPi.actuators.motors;

import ev3dev.actuators.motors.EV3LargeRegulatedMotor;
import ev3dev.hardware.ports.MotorPortBrickPi;
import lejos.utility.Delay;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class LargeMotorDemo {

    public static void main(String[] args) throws InterruptedException {

        log.info("Starting motors on A");
        final EV3LargeRegulatedMotor mA = new EV3LargeRegulatedMotor(MotorPortBrickPi.A);
        mA.setSpeed(500);
        mA.hold();
        mA.forward();
        log.info(String.format("Large Motor is moving: %s at speed %d", mA.isMoving(), mA.getSpeed()));
        Delay.msDelay(2000);
        mA.stop();
        log.info("Stopped motors");
    }

}
