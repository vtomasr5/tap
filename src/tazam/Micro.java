package tazam;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.JFileChooser;

public class Micro {

    AudioFileFormat.Type aFF_T = AudioFileFormat.Type.WAVE;
    AudioFormat aF = new AudioFormat(8000.0F, 16, 1, true, false);
    TargetDataLine tD;
    File f;
    OutputStream out = new ByteArrayOutputStream();
    boolean running = true;

    private void stopRunning() {
        this.running = false;
    }

    public boolean getRunning() {
        return running;
    }
    
    public void startRecording() {
        try {
            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
            tD = (TargetDataLine) AudioSystem.getLine(dLI);
            new CapThread().start();
            Thread.sleep(10000);
            tD.close();
            stopRunning();
            StartFrame.getArea().append("\nGravació aturada...");
        } catch (LineUnavailableException | InterruptedException e) {
        } 
    }

//    public Micro() {
//        try {
//            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
//            tD = (TargetDataLine) AudioSystem.getLine(dLI);
//            new CapThread().start();
//            Thread.sleep(10000);
//            tD.close();
//            stopRunning();
//        } catch (LineUnavailableException | InterruptedException e) {
//        }
//    }
    
    public boolean saveFileRecorded() throws FileNotFoundException, IOException {
        JFileChooser fc = new JFileChooser();
        int res = fc.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath(); 
            if (!path.endsWith(".wav")) {
                path += ".wav";
            }
            File filename = new File(path);
            if (filename != null) {
                this.f = filename;
                // start recording
                StartFrame.getArea().append("\nGravant des del micròfon (10 segons)...");
                startRecording();
            }
            return true; // accept (Ok)
        } else {
            return false; // cancel
        }
    }

    class CapThread extends Thread {

        @Override
        public void run() {
            try {
                tD.open(aF);
                tD.start();
                AudioSystem.write(new AudioInputStream(tD), aFF_T, f);
//                stopRunning();
            } catch (LineUnavailableException | IOException e) {
            }
        }
    }
}