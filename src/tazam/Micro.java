package tazam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import javax.swing.JFileChooser;

public class Micro {

    private AudioFileFormat.Type aFF_T = AudioFileFormat.Type.WAVE;
    private AudioFormat aF = new AudioFormat(8000.0F, 16, 1, true, false);
    private TargetDataLine tD;
    private File f;
//    private OutputStream out = new ByteArrayOutputStream();

    public Micro() {
        try {
            boolean saveFileRecorded = saveFileRecorded();
            if (saveFileRecorded) {
                StartFrame.getArea().append("\nGravant des del micròfon (10 segons)...");
                DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
                try {
                    tD = (TargetDataLine) AudioSystem.getLine(dLI);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Micro.class.getName()).log(Level.SEVERE, null, ex);
                }
                new CapThread().start();
                try {
                    Thread.sleep(10000); // 10 seg.
                } catch (InterruptedException ex) {
                    Logger.getLogger(Micro.class.getName()).log(Level.SEVERE, null, ex);
                }
                tD.close();
                StartFrame.getArea().append("\nGravació finalitzada");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Micro.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Micro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean saveFileRecorded() throws FileNotFoundException, IOException {
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
            } catch (LineUnavailableException | IOException e) {
            }
        }
    }
}