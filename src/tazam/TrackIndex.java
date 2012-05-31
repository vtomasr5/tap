package tazam;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * Contains the underlying index of the tracks the user loads. Used when there
 * is an attempt to match a track. It contains both a TrackMap and
 * CachedIndexMap.
 */
public class TrackIndex {

    /**
     * The map of probes to their data points
     */
    private CachedIndexMap cachedIndexMap;
    /**
     * The map of the track id's to their track data
     */
    private TrackMap trackMap;
    /**
     * The base directory of the trackIndex, if a folder was indexed
     */
    private File baseDirectory = null;

    /**
     * Iterates over all the track ID's
     */
//    private Iterator<TrackID> trackIDIterator;
    /**
     * Constructs a trackIndex.
     *
     * @param baseDir The file or folder name.
     */
    public TrackIndex(File baseDir) {
        baseDirectory = baseDir;
        trackMap = new TrackMap(baseDir);//create the map of ID to TrackInfo
//        trackIDIterator = trackMap.getTrackIDIterator();//get the iterator
        cachedIndexMap = new CachedIndexMap();
        initializeIndex(baseDir);
    }

    /**
     * Sets up the cachedIndexMap
     *
     * @param baseDir The base directory - a file or folder.
     */
    private void initializeIndex(File baseDir) {
        try {
            if (baseDir.isDirectory()) {
                addFolder(baseDir);
            } else {
                addTrack(baseDir);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    /**
     * Adds a track to the trackIndex.
     *
     * @param file The file to be indexed.
     */
    public void addTrack(File file) {
        if (!trackMap.containsTrack(file)) {//if track map does not have the current file.
            trackMap.addTrack(file);
        }
        try {
            TrackID id = new TrackID(TrackMap.trackNo - 1);
            //System.out.println(id.toString());
            int numberIndexed = cachedIndexMap.indexFile(file, id);
            TrackInfo info = trackMap.getTrackInfo(id);
            info.setNumberHashPoints(numberIndexed);
            //JOptionPane.showMessageDialog(null, "Indexat " + file.getName(), "Informació", JOptionPane.PLAIN_MESSAGE);
            StartFrame.getArea().append("\n");
            StartFrame.getArea().append("Indexat '" + file.getName() + "'");
            StartFrame.getArea().append("\n");
            StartFrame.getArea().repaint();
        } catch (UnsupportedAudioFileException | IOException | HeadlessException e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Adds a folder to the track index object.
     *
     * @param folder The folder to be indexed
     */
    public void addFolder(File folder) {
        File[] files = folder.listFiles(new TrackMap.AudioFileFilter());
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            addTrack(f);
        }
        if (files.length == 0) {
            JOptionPane.showMessageDialog(null, "La carpeta -"
                    + folder.getName() + "-\nno te cap pista.");
        } else {
            JOptionPane.showMessageDialog(null, "La carpeta -"
                    + folder.getName() + "-\nha estat correctament indexada.");
        }
    }

    /**
     * Tries to match a signal against the index. It will not be added to the
     * index. Displays the results on a new JFrame.
     *
     * @param s The signal to query.
     */
    public void matchSignal(Signal s) {
        MatchResults results = cachedIndexMap.query(s);

        ShowMatches(results); // muestra resultados en textarea
        getMatch(results); // muestra resultados en textarea
    }

    public void matchSignalDialog(Signal s) {
        MatchResults results = cachedIndexMap.query(s);

        MatchResultsFrame matchFrame = new MatchResultsFrame(results, trackMap); // muestra resultados en jdialog
    }

    public void ShowMatches(MatchResults results) {
        //Displays the contents of the match results
        for (Iterator<TrackID> it = results.getContentsIterator(); it.hasNext();) {
            TrackID id = it.next();
            Histogram h = results.getHistogramAt(id);
            StringBuilder builder = new StringBuilder("ID pista: " + id.getIntID() + " / Nom pista: "
                    + trackMap.getTrackInfo(id).getDescription() + "\n");
            builder.append(h.toString());
            StartFrame.getArea().append(builder.toString());
            StartFrame.getArea().append("\n");
        }
        StartFrame.getArea().repaint();
    }

    public void getMatch(MatchResults results) {
        int maxNumberOfMatches = Integer.MIN_VALUE;
        double matchRate = 0;//this is the one we use - the percentage of matches at the max match must be the highest percentage
        TrackID matchID = null;
        int timeOffset = 0;
        for (Iterator<TrackID> it = results.getContentsIterator(); it.hasNext();) {
            TrackID id = it.next();
            Histogram h = results.getHistogramAt(id);
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
        StartFrame.getArea().append("La concordança de la pista es: [" + matchInfo.toString() + "]\nDesplaçament índex: " + timeOffset / Spectrogram.SAMPLE_SIZE + " / Total de concordançes de hashing: " + maxNumberOfMatches + "\nPercentatge de concordançes del desplaçament: " + matchRate * 100 + " %");
        StartFrame.getArea().repaint();
    }

    /**
     * Gets the trackInfo for the given track ID.
     *
     * @param id The trackID
     * @return The trackInfo for the trackID.
     */
    public TrackInfo getTrackInfo(TrackID id) {
        return trackMap.getTrackInfo(id);
    }

    /**
     * Gets an iterator over the track id's.
     *
     * @return An iterator.
     */
    public Iterator<TrackID> getTrackIDIterator() {
        return trackMap.getTrackIDIterator();
    }

    /**
     * Gets the number of tracks in the index.
     *
     * @return The number of tracks indexed.
     */
    public int getNumberOfTracks() {
        return trackMap.getNumberOfTracks();
    }

    /**
     * Gets the base directory of the index.
     *
     * @return The file base directory.
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }
}
