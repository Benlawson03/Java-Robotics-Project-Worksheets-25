package lejos_test4;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

import lejos.hardware.port.MotorPort;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.BaseRegulatedMotor;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.Battery;
import lejos.robotics.SampleProvider;

import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.WheeledChassis;

import lejos.robotics.navigation.MovePilot;

import lejos.robotics.subsumption.Behavior;
import lejos.robotics.subsumption.Arbitrator;

// Trundle - Always moves forward
class Trundle implements Behavior {
	private MovePilot pilot;

	public Trundle(MovePilot pilot) {
		this.pilot = pilot;
	}

	public boolean takeControl() {
		return true;
	}

	public void action() {
		pilot.forward();
	}

	public void suppress() {
		pilot.stop();
	}
}

// Backup - Reverse and turn when obstacle detected
class Backup implements Behavior {
	private MovePilot pilot;
	private SampleProvider distance;
	private float[] sample;

	public Backup(MovePilot pilot, EV3UltrasonicSensor sensor) {
		this.pilot = pilot;
		this.distance = sensor.getDistanceMode();
		this.sample = new float[distance.sampleSize()];
	}

	public boolean takeControl() {
		distance.fetchSample(sample, 0);
		return sample[0] < 0.2; // If obstacle is closer than 20cm
	}

	public void action() {
		pilot.travel(-20);
		pilot.rotate(Math.random() > 0.5 ? 90 : -90);
	}

	public void suppress() {
		pilot.stop();
	}
}

//Dark - Slow down when dark
class Dark implements Behavior {
	private MovePilot pilot;
	private SampleProvider light;
	private float[] sample;
	private boolean suppressed = false;

	public Dark(MovePilot pilot, EV3ColorSensor sensor) {
		this.pilot = pilot;
		this.light = sensor.getAmbientMode();
		this.sample = new float[light.sampleSize()];
	}

	public boolean takeControl() {
		light.fetchSample(sample, 0);
		return (sample[0] < 0.5 && pilot.getLinearSpeed() > 150);
	}

	public void action() {
		suppressed = false;
		pilot.setLinearSpeed(100);
	}

	public void suppress() {
		suppressed = true;
	}
}

// Light - Speed up when bright
class Light implements Behavior {
	private MovePilot pilot;
	private SampleProvider light;
	private float[] sample;
	private boolean suppressed = false;

	public Light(MovePilot pilot, EV3ColorSensor sensor) {
		this.pilot = pilot;
		this.light = sensor.getAmbientMode();
		this.sample = new float[light.sampleSize()];
	}

	public boolean takeControl() {
		light.fetchSample(sample, 0);
		return (sample[0] > 0.5 && pilot.getLinearSpeed() < 150);
	}

	public void action() {
		suppressed = false;
		pilot.setLinearSpeed(200);
	}

	public void suppress() {
		suppressed = true;
	}
}

// Emergency STOP
class EmergencyStop implements Behavior {
	private SampleProvider pressed;
	private float[] sample;

	public EmergencyStop(EV3TouchSensor sensor) {
		this.pressed = sensor.getTouchMode();
		this.sample = new float[pressed.sampleSize()];
	}

	public boolean takeControl() {
		return (Button.ESCAPE.isDown() || sample[0] == 1);
	}

	public void action() {
		System.exit(0);
	}

	public void suppress() {
	}
}

// Battery Level Warning
class BatteryLevel implements Behavior {
	public boolean takeControl() {
		return Battery.getVoltage() < 7.0;
	}

	public void action() {
		while (Battery.getVoltage() < 7.0) {
			LCD.clear();
			LCD.drawString("Battery Low!", 0, 0);
			Sound.beep();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void suppress() {
	}
}

// Bluetooth Handler
class BluetoothHandler implements Behavior {
	private boolean messageReceived = false;

	public boolean takeControl() {
		return messageReceived;
	}

	public void action() {
		System.out.println("Bluetooth message received!");
		messageReceived = false;
	}

	public void suppress() {
	}
}

// Sensor Calibration
class Calibrate implements Behavior {
	private boolean calibrated = false;

	public boolean takeControl() {
		return !calibrated;
	}

	public void action() {
		LCD.clear();
		LCD.drawString("Calibrating...", 0, 0);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		calibrated = true;
		LCD.drawString("Done", 0, 0);
	}

	public void suppress() {
	}
}

// Driver Class
public class Driver {
	final static float WHEEL_DIAMETER = 55; // The diameter (mm) of the wheels
	final static float AXLE_LENGTH = 145; // The distance (mm) your two driven wheels

	public static void main(String[] args) {
		BaseRegulatedMotor LMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		BaseRegulatedMotor RMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		Wheel LWheel = WheeledChassis.modelWheel(LMotor, WHEEL_DIAMETER).offset(-AXLE_LENGTH / 2);
		Wheel Rwheel = WheeledChassis.modelWheel(RMotor, WHEEL_DIAMETER).offset(AXLE_LENGTH / 2);
		Chassis chassis = new WheeledChassis(new Wheel[] { LWheel, Rwheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		MovePilot pilot = new MovePilot(chassis);

		EV3UltrasonicSensor us = new EV3UltrasonicSensor(SensorPort.S1);
		EV3TouchSensor ts = new EV3TouchSensor(SensorPort.S2);
		EV3ColorSensor cs = new EV3ColorSensor(SensorPort.S3);

		Behavior[] behaviors = { new Backup(pilot, us), new Dark(pilot, cs), new Light(pilot, cs), new BatteryLevel(),
				new BluetoothHandler(), new Calibrate(), new Trundle(pilot), new EmergencyStop(ts) };

		Arbitrator arbitrator = new Arbitrator(behaviors);
		arbitrator.go();
	}
}
