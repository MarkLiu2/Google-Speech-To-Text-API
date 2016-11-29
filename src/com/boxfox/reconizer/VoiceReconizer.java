package com.boxfox.reconizer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import net.sourceforge.javaflacencoder.FLACFileWriter;
import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.recognizer.GoogleResponse;

public class VoiceReconizer extends Thread {
	private String API_KEY;
	private STTListener listener;
	private Microphone mic;
	private File file;
	private Recognizer recognizer;
	private int delay;

	public VoiceReconizer(String apikey) {
		this.API_KEY = apikey;
		this.delay = 5000;
		mic = new Microphone(FLACFileWriter.FLAC);
		file = new File("/tmp/testfile2.flac");
		new File(file.getParent()).mkdirs();
		recognizer = new Recognizer(Recognizer.Languages.KOREAN, API_KEY);
	}

	public VoiceReconizer(String apikey, int delay) {
		this.API_KEY = apikey;
		this.delay = delay;
		mic = new Microphone(FLACFileWriter.FLAC);
		file = new File("/tmp/testfile2.flac");
		new File(file.getParent()).mkdirs();
		recognizer = new Recognizer(Recognizer.Languages.KOREAN, API_KEY);
	}
	
	public void setSTTListener(STTListener listener){
		this.listener = listener;
	}

	@Override
	public void run() {
		while (true) {
			String text = cognize();
			if(listener!=null){
				listener.OnVoiceReconized(text);
			}
		}
	}
	
	public String doRecognize(){
		return cognize();
	}
	
	private String cognize(){
		try {
			mic.captureAudioToFile(file);
		} catch (Exception ex) {
			System.out.println("ERROR: Microphone is not availible.");
			ex.printStackTrace();
		}

		try {
			System.out.println("Recording...");
			Thread.sleep(delay);
			mic.close();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		mic.close();
		System.out.println("Recording stopped.");
		return requestGoogle();
	}
	
	private String requestGoogle(){
		String str = null;
		try {
			int maxNumOfResponses = 4;
			GoogleResponse response = recognizer.getRecognizedDataForFlac(file, maxNumOfResponses,
					(int) mic.getAudioFormat().getSampleRate());
			str = response.getResponse();
		} catch (Exception ex) {
			System.out.println("ERROR: Google cannot be contacted");
			ex.printStackTrace();
		}
		file.delete();
		return str;
	}
	
	public static void buildLib() {
		try {
			final File[] libs = new File[] {
					new File("libs", "java-flac-encoder-0.3.7.jar"),
					new File("libs", "java-json.jar")};
			for (final File lib : libs) {
				if (!lib.exists()) {
					JarUtils.extractFromJar(lib.getName(),
							lib.getAbsolutePath());
				}
			}
			for (final File lib : libs) {
				if (!lib.exists()) {
					System.err.println("Error can't find library : "+ lib.getName());
					return;
				}
				addClassPath(JarUtils.getJarUrl(lib));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void addClassPath(final URL url) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url
					+ " to system classloader");
		}
	}
	static{
		buildLib();
	}
}