package tazam;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * Contains the waveform panel and the spectrogram panel. Displays the track and
 * allows for play back.
 */
public class ClipFrame extends JFrame {

    /**
     * The waveform panel
     */
    private WaveformPanel waveformPanel;
    /**
     * The spectrogram panel
     */
    private SpectrogramPanel spectrogramPanel;
    /**
     * The spectrogram inside the clip
     */
//    private Spectrogram spectrogram;
    /**
     * The data of the current audio clip
     */
//    private final Signal signal;
    /**
     * The index to match against for the audio file
     */
    private TrackIndex trackIndex;
    /**
     * Menu item to play the clip
     */
    private JMenuItem play;
    /**
     * Menu item to match selected clip against index
     */
    private JMenuItem match;
    /**
     * List of the threads started
     */
//    private ArrayList<Thread> threadList = new ArrayList<Thread>();

    /**
     * Constructs a clip frame open user selection of a clip.
     *
     * @param signal The signal to use.
     */
    public ClipFrame(final Signal signal, StartFrame startFrame) {
        super("Audio mostrat: " + signal.getName() + "@" + signal.getFrameRate() + "Hz/" + signal.getSamplesLength() + " mostres");
//        this.signal = signal;
        trackIndex = startFrame.getTrackIndex();//get the track index

        setPreferredSize(new Dimension(750, 450));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arxiu");
        menuBar.add(fileMenu);

        play = new JMenuItem("Play");
        play.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(signal);
//                threadList.add(t);
                t.start();
            }
        });
        fileMenu.add(play);

        //TO match tracks
        match = new JMenuItem("Pista concordant");
        match.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    trackIndex.matchSignal(signal);
                } catch (NullPointerException x) {
                    JOptionPane.showMessageDialog(null, "No hi ha res a l'índex.");
                }
            }
        });
        fileMenu.add(match);

        JMenu zoomMenu = new JMenu("Zoom");
        menuBar.add(zoomMenu);

        JMenuItem zin = new JMenuItem("Zoom +");
        zin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        zin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                waveformPanel.zoomIn();
                spectrogramPanel.zoomIn();
            }
        });
        zoomMenu.add(zin);

        JMenuItem zout = new JMenuItem("Zoom -");
        zout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        zout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                waveformPanel.zoomOut();
                spectrogramPanel.zoomOut();
            }
        });
        zoomMenu.add(zout);

        JMenuItem reset = new JMenuItem("Reiniciar");
        reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                waveformPanel.resetZoom();
                spectrogramPanel.resetZoom();
            }
        });
        zoomMenu.add(reset);

        //For allowing user to tweak with the settings.
        JMenu adjustMenu = new JMenu("Especificar Paràmetres");
        JMenuItem inc = new JMenuItem("Aumentar la selectivitat de les sondes");
        adjustMenu.add(inc);
        inc.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Probe.increaseSelectivity();

            }
        });

        JMenuItem dec = new JMenuItem("Decrementar la selectivitat de les sondes");
        adjustMenu.add(dec);
        dec.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Probe.decreaseSelectivity();

            }
        });
        menuBar.add(adjustMenu);

        setJMenuBar(menuBar);

        waveformPanel = new WaveformPanel(signal);
        spectrogramPanel = new SpectrogramPanel(signal);
        Container c = getContentPane();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(waveformPanel);
        splitPane.setBottomComponent(spectrogramPanel);
        JScrollPane scrollPane = new JScrollPane(splitPane);
        c.add(scrollPane);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Sets the trackIndex.
     *
     * @param trackIndex The index to set in the clip frame.
     */
    public void addTrackIndex(TrackIndex trackIndex) {
        this.trackIndex = trackIndex;
    }
}
