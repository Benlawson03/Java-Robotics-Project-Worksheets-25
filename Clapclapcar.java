import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.robotics.SampleProvider;

public class Clapclapcar {
    public static void main(String[] args) {
        TextLCD lcd = BrickFinder.getDefault().getTextLCD();
        NXTSoundSensor soundSensor = new NXTSoundSensor(SensorPort.S1);
        SampleProvider sound = soundSensor.getDBAMode();
        
        float[] level = new float[1];
        float maxSoundLevel = Float.MIN_VALUE;
        float minSoundLevel = Float.MAX_VALUE;

        lcd.clear();
        lcd.drawString("Monitoring Sound", 1, 1);
        lcd.drawString("Press ENTER to stop", 1, 2);

        while (!Button.ENTER.isDown()) {
            sound.fetchSample(level, 0);
            float currentSound = level[0];
            
            if (currentSound > maxSoundLevel) {
                maxSoundLevel = currentSound;
                lcd.drawString("Max: " + maxSoundLevel, 1, 4);
            }
            
            if (currentSound < minSoundLevel) {
                minSoundLevel = currentSound;
                lcd.drawString("Min: " + minSoundLevel, 1, 5);
            }
        }

        lcd.clear();
        lcd.drawString("Final Values:", 1, 1);
        lcd.drawString("Max: " + maxSoundLevel, 1, 2);
        lcd.drawString("Min: " + minSoundLevel, 1, 3);

        soundSensor.close();
    }
}
