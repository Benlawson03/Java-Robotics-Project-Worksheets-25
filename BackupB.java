import lejos.robotics.subsumption.Behavior;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import java.util.Random;

public class BackupB implements Behavior {
    private EV3UltrasonicSensor ultrasonicSensor;
    private SampleProvider distanceProvider;
    private float[] sample;
    private boolean suppressed = false;
    private static final float WALL_THRESHOLD = 0.20f;
    private Random random = new Random();

    public BackupB(EV3UltrasonicSensor us) {
        this.ultrasonicSensor = us;
        this.distanceProvider = ultrasonicSensor.getDistanceMode();
        this.sample = new float[distanceProvider.sampleSize()];
    }

    @Override
    public boolean takeControl() {
        distanceProvider.fetchSample(sample, 0);
        return sample[0] < WALL_THRESHOLD;
    }

    @Override
    public void suppress() {
        suppressed = true;
    }

    @Override
    public void action() {
        suppressed = false;
        Motor.A.backward();
        Motor.B.backward();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        Motor.A.stop(true);
        Motor.B.stop();
        if (random.nextBoolean()) {
            Motor.A.forward();
            Motor.B.backward();
        } else {
            Motor.A.backward();
            Motor.B.forward();
        }
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        Motor.A.stop(true);
        Motor.B.stop();
    }
}
