package ch.astrepto.robot.capteurs;

import java.rmi.RemoteException;

import lejos.remote.ev3.RMISampleProvider;

public class CapteurRemote {

	public RMISampleProvider sensor;
	
	public  float getValue() {
		
		float value[] = new float[1];
		try {
			value = sensor.fetchSample();
		} catch (RemoteException e) {
			System.out.println("erreur fetch sensor");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value[0]*100;
	}
	
	public void close() {
		try {
			sensor.close();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
