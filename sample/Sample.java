package test.park.src.Main;

import com.boxfox.reconizer.STTListener;
import com.boxfox.reconizer.VoiceReconizer;

public class Sample {
	public static void main(String args[]){
		VoiceReconizer voiceReconizer = new VoiceReconizer("API-KEY");
		voiceReconizer.setSTTListener(new STTListener(){

			@Override
			public void OnVoiceReconized(String arg) {
				System.out.println(arg);
			}});
		voiceReconizer.start();
		//VoiceReconuzer voiceReconizer = new VoiceReconuzer("API-KEY", 5000);
		//String str = voiceReconizer.doRecognize();
	}
}
