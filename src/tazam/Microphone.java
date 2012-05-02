/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tazam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.*;

public class Microphone {

    AudioFileFormat.Type aFF_T = AudioFileFormat.Type.WAVE;
    AudioFormat aF = new AudioFormat(8000.0F, 16, 1, true, false);
    TargetDataLine tD;
    File f = new File("Grabacion.wav");
    OutputStream out = new ByteArrayOutputStream();
    boolean running =true;
    
    public void stopRunning(){
        this.running = false;
    }

    public boolean getRunning(){
        return running;
    }
    public Microphone() {
        try {
            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class, aF);
            tD = (TargetDataLine) AudioSystem.getLine(dLI);
            new Microphone.CapThread().start();
            System.out.println("Grabando durante 10s...");
            Thread.sleep(10000);
            tD.close();
            stopRunning();
           
        } catch (Exception e) {
        }
    }

    class CapThread extends Thread {
     
        public void run() {
            try {
                tD.open(aF);
                tD.start();
                AudioSystem.write(new AudioInputStream(tD), aFF_T, f);    
                stopRunning();
            } catch (Exception e) {
            }
        }
    }


    
}
