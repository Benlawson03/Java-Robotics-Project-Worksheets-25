import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class SimpleChap {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);

        NXTSoundSensor soundSensor = new NXTSoundSensor(SensorPort.S1);
        SensorMode soundMode = (SensorMode) soundSensor.getDBAMode();
        SampleProvider clapDetector = new ClapFilter(soundMode, 0.6f, 100);

        EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        SampleProvider distanceProvider = ultrasonicSensor.getDistanceMode();
        float[] distanceSample = new float[1];

        int state = 0;

        lcd.drawString("Waiting for clap...", 0, 1);

        while (!Button.ENTER.isDown()) {
            float[] clapSample = new float[1];
            clapDetector.fetchSample(clapSample, 0);

            if (clapSample[0] == 1.0f) {
                if (state == 0) {
                    lcd.clear();
                    lcd.drawString("Moving forward!", 0, 1);
                    leftMotor.forward();
                    rightMotor.forward();
                    state = 1;
                } else if (state == 1) {
                    lcd.clear();
                    lcd.drawString("Turning right!", 0, 1);
                    leftMotor.rotate(180, true);
                    rightMotor.rotate(-180);
                    state = 2;
                }
            }

            distanceProvider.fetchSample(distanceSample, 0);
            float distance = distanceSample[0] * 100;

            if (distance < 50) {
                lcd.clear();
                lcd.drawString("Wall detected!", 0, 1);
                leftMotor.stop();
                rightMotor.stop();
                break;
            }
        }

        leftMotor.stop();
        rightMotor.stop();
        soundSensor.close();
        ultrasonicSensor.close();
        lcd.clear();
        lcd.drawString("Stopped", 0, 1);
    }
}

class ClapFilter implements SampleProvider {
    private final float threshold;
    private final int timeGap;
    private final SampleProvider ss;
    private long lastHeard;

    public ClapFilter(SensorMode soundMode, float level, int gap) {
        timeGap = gap;
        ss = soundMode;
        threshold = level;

        if (!soundMode.getName().startsWith("Sound")) {
            throw new IllegalArgumentException("A ClapFilter can only filter sound sensors");
        }
        
        lastHeard = -2 * timeGap;
    }

    @Override
    public void fetchSample(float level[], int index) {
        level[index] = 0.0f;
        long now = System.currentTimeMillis();

        if (now - lastHeard > timeGap) {
            ss.fetchSample(level, index);
            if (level[index] >= threshold) {
                level[index] = 1.0f;
                lastHeard = now;
            }
        }
    }

    @Override
    public int sampleSize() {
        return 1;
    }
}
