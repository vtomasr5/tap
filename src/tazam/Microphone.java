package tazam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.JFileChooser;

/**
 *  Contains functionality for record audio from Microphone. The audio recorded
 *  it can be saved into a file with a file-save dialog.
 *
 */
public class Microphone {
    
    private boolean running;
//    private ByteArrayOutputStream out;
    private File f;
    
    /**
     * Constructor of this class
     */
    public Microphone() {}
    
    /**
     * Capture audio with a simple audio format (WAVE).
     */
    private void captureAudio() {
        try {
//            final AudioFormat format = getFormat();
            final AudioFormat format = new AudioFormat(8000.0F, 16, 1, true, false); // this works
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                @Override
                public void run() {
//                    out = new ByteArrayOutputStream();
                    running = true;
                    try {
                        while (running) {
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
//                                out.write(buffer, 0, count);
                                AudioSystem.write(new AudioInputStream(line), AudioFileFormat.Type.WAVE, f);
                            }
                        }
//                        out.close();
                    } catch (IOException e) {
                        System.err.println("problemes E/S: " + e);
//                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Línia no disponible: " + e);
//            System.exit(-2);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
//        AudioFormat aF = new AudioFormat(8000.0F, 16, 1, true, false); // this works
    }
    
    /**
     * Stops loop thread from capturing audio.
     */
    public void stopRunning() {
        running = false;
    }
    
    /**
     * Prompts a save dialog for save your audio recorded from microphone.
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
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
                StartFrame.getArea().append("\nGravant des del micròfon...");
                captureAudio();
            }
            return true; // accept (Ok)
        } else {
            return false; // cancel
        }
    }
}

//    AudioFileFormat.Type aFF_T = AudioFileFormat.Type.WAVE;
//    AudioFormat aF = new AudioFormat(8000.0F, 16, 1, true, false);
//    TargetDataLine tD;
//    File f;// = new File("Grabacio.wav");
//    OutputStream out = new ByteArrayOutputStream();
//    boolean running = true;
//    CapThread capThread;// = new CapThread();
//
//    public void stopRunning() {
//        tD.close();
//        this.running = false;
//        StartFrame.getArea().append("\nGravació acabada!");
//    }
//
//    public boolean getRunning() {
//        return running;
//    }
//    
//    public Microphone() {
//        DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
//        try {
//            tD = (TargetDataLine) AudioSystem.getLine(dLI);
//        } catch (LineUnavailableException ex) {
//            Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        capThread = new CapThread();
//    }
//
////    public Microphone() {
////        try {
////            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
////            tD = (TargetDataLine) AudioSystem.getLine(dLI);
////            
////            CapThread capThread = new CapThread();
////            capThread.start();
////            Thread.sleep(1);
////            tD.close();
////            stopRunning();
////
////        } catch (LineUnavailableException | InterruptedException e) {
////            Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, e);
////        }
////    }
//    
//    public void startRecording() {
////        try {
////            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
////            tD = (TargetDataLine) AudioSystem.getLine(dLI);            
//              capThread.start();
////            Thread.sleep(1);
////            tD.close();
////            stopRunning();
////        } catch (InterruptedException e) {
////        }
//    }
//    
//    /**
//     * Save file dialog recorded with a microphone
//     * @throws FileNotFoundException
//     * @throws IOException 
//     */
//    public boolean saveFileRecorded() throws FileNotFoundException, IOException {
//        JFileChooser fc = new JFileChooser();
//        int res = fc.showSaveDialog(null);
//        if (res == JFileChooser.APPROVE_OPTION) {
//            String path = fc.getSelectedFile().getPath(); 
//            if (!path.endsWith(".wav")) {
//                path += ".wav";
//            }
//            File filename = new File(path);
//            if (filename != null) {
//                this.f = filename;
//                // start recording
//                StartFrame.getArea().append("\nGravant des del micròfon...");
//                startRecording();
//            }
//            return true; // accept (Ok)
//        } else {
//            return false; // cancel
//        }
//    }
//
//    private class CapThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                tD.open(aF);
//                tD.start();             
//                while (getRunning()) {
////                    tD.open(aF);
////                    tD.start();
//                    AudioSystem.write(new AudioInputStream(tD), aFF_T, f);
////                    Thread.sleep(10000); // max 10 seg.
////                    stopRunning();
//                }
//            } catch (LineUnavailableException | IOException e) {
//                Logger.getLogger(Microphone.class.getName()).log(Level.SEVERE, null, e);
//            }
//        }
//    }
