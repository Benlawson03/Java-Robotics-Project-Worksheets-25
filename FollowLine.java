import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class FollowLine {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
        EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);

        SampleProvider redMode = colorSensor.getRedMode();
        float[] sample = new float[1];

        float maxLight = 0;
        float minLight = 1;

        lcd.drawString("Calibrating...", 0, 1);
        lcd.drawString("Move sensor over", 0, 2);
        lcd.drawString("black & white area", 0, 3);
        lcd.drawString("Press ENTER when done", 0, 4);

        while (!Button.ENTER.isDown()) {
            redMode.fetchSample(sample, 0);
            float lightLevel = sample[0];

            if (lightLevel > maxLight) maxLight = lightLevel;
            if (lightLevel < minLight) minLight = lightLevel;

            lcd.clear();
            lcd.drawString("Max: " + maxLight, 0, 1);
            lcd.drawString("Min: " + minLight, 0, 2);
        }

        float LIGHT_AVERAGE = (maxLight + minLight) / 2;

        lcd.clear();
        lcd.drawString("Calibration done!", 0, 1);
        lcd.drawString("Threshold: " + LIGHT_AVERAGE, 0, 2);
        lcd.drawString("Press ENTER to start", 0, 3);
        Button.ENTER.waitForPressAndRelease();

        lcd.clear();
        lcd.drawString("Following line...", 0, 1);

        while (!Button.ENTER.isDown()) {
            redMode.fetchSample(sample, 0);
            float lightLevel = sample[0];

            if (lightLevel > LIGHT_AVERAGE) {
                leftMotor.forward();
                rightMotor.stop();
            } else {
                rightMotor.forward();
                leftMotor.stop();
            }
        }

        leftMotor.stop();
        rightMotor.stop();
        colorSensor.close();
        lcd.clear();
        lcd.drawString("Stopped", 0, 1);
    }
}
