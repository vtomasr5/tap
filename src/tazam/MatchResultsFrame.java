package tazam;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.*;

public class MatchResultsFrame extends JFrame {

    /**
     * The results of the matching process
     */
    private MatchResults matchResults;
    /**
     * The map of trackID to track description
     */
    private TrackMap trackMap;
    /**
     * Menu item to get the top match to the selected clip.
     */
    private JMenuItem getMatch;

    /**
     * Constructs a new frame to display the match results
     *
     * @param matchResults The results to display.
     * @param trackMap The mapping to use.
     */
    public MatchResultsFrame(MatchResults matchResults, TrackMap trackMap) {
        super("Resultats de la concordança");
        this.matchResults = matchResults;
        this.trackMap = trackMap;
        //histograms = new ArrayList<Histogram>();
        setPreferredSize(new Dimension(400, 200));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu match = new JMenu("Concordança");
        menuBar.add(match);
        getMatch = new JMenuItem("Veure concordança màxima");
        match.add(getMatch);
        getMatch.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getMatch();
                    }
                });

        //Displays the contents of the match results
        for (Iterator<TrackID> it = matchResults.getContentsIterator(); it.hasNext();) {
            TrackID id = it.next();
            Histogram h = matchResults.getHistogramAt(id);
            StringBuilder builder = new StringBuilder("Track ID: " + id.getIntID() + " / Nom pista: "
                    + trackMap.getTrackInfo(id).getDescription() + "\n");
            builder.append(h.toString());
            textArea.append(builder.toString());
            textArea.append("\n");
        }
        pack();
        setVisible(true);
    }

    /**
     * Gets the matches of the selected track.
     */
    public void getMatch() {
        int maxNumberOfMatches = Integer.MIN_VALUE;
        double matchRate = 0;//this is the one we use - the percentage of matches at the max match must be the highest percentage
        TrackID matchID = null;
        int timeOffset = 0;
        for (Iterator<TrackID> it = matchResults.getContentsIterator(); it.hasNext();) {
            TrackID id = it.next();
            Histogram h = matchResults.getHistogramAt(id);
            MaxMatch thisMatch = h.getMaxMatch();
            int totalMatches = h.getTotalMatches();
            int thisNumberOfMatches = thisMatch.numberOfMatches;//at a particular delta
			/*
             * if(thisNumberOfMatches > maxNumberOfMatches){ maxNumberOfMatches
             * = thisNumberOfMatches; matchID = id; timeOffset =
             * thisMatch.delta; }
             */
            double thisMatchRate = (double) ((double) thisNumberOfMatches / (double) totalMatches);
            if (thisMatchRate > matchRate) {
                maxNumberOfMatches = thisNumberOfMatches;
                matchID = id;
                timeOffset = thisMatch.delta;
                matchRate = thisMatchRate;
            }
        }
        TrackInfo matchInfo = trackMap.getTrackInfo(matchID);
        JOptionPane.showMessageDialog(null, ("La concordança de la pista es: [" + matchInfo.toString()
      + "]\nDesplaçament índex: " + timeOffset / Spectrogram.SAMPLE_SIZE + " / Total de concordançes de hashing: " + maxNumberOfMatches
      + "\nPercentatge de concordançes del desplaçament: " + matchRate * 100 + " %"), "Millor concordança", JOptionPane.PLAIN_MESSAGE);

    }
}
