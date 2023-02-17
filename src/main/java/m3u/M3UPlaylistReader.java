package m3u;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates internal data structure for a given .m3u-playlist
 */
public class M3UPlaylistReader {

    private static final String M3U_FILE_FIRST_LINE = "#EXTM3U";
    private static final String M3U_FILE_META_LINE_BEGINNING = "#EXTINF:";
    private static final String M3U_FILE_DURATION_NAME_SPLITTER = ",";
    private static final String M3U_FILE_BEGINNING_DURATION_SPLITTER = ":";

    private final File sourcePlaylistFile;

    private final List<String> fileContentAsList;
    private String extractedTrackTitle;
    private String extractedTrackDuration;
    private String extractedTrackPath;
    private final M3UList extractedList;

    public M3UPlaylistReader(File sourcePlaylistFile) {
        this.sourcePlaylistFile = sourcePlaylistFile;
        fileContentAsList = new ArrayList<>();
        extractedList = new M3UList(sourcePlaylistFile);
    }

    public M3UList extractM3UList() {
        try (BufferedReader br = new BufferedReader(new FileReader(sourcePlaylistFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContentAsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateM3uList();
        return extractedList;
    }

    private void generateM3uList() {
        for (String line : fileContentAsList) {
            if (isThisLineTheMetaLine(line)) {
                extractedTrackTitle = null;
                extractedTrackDuration = null;
                extractedTrackPath = null;
                extractTitleAndDurationFromLine(line);
            } else if (isThisLineThePathLine(line)) {
                extractedTrackPath = line;
            }
            addToExtractedList();
        }
    }

    private void addToExtractedList() {
        if (extractedTrackTitle == null || extractedTrackTitle.isEmpty()) {
            return;
        }
        if (extractedTrackDuration == null || extractedTrackDuration.isEmpty()) {
            return;
        }
        if (extractedTrackPath == null || extractedTrackPath.isEmpty()) {
            return;
        }
        extractedList.addTrack(new M3UTrack(extractedTrackTitle, extractedTrackDuration, extractedTrackPath));
    }

    private boolean isThisLineTheMetaLine(String line) {
        return line.contains(M3U_FILE_META_LINE_BEGINNING);
    }

    private boolean isThisLineThePathLine(String line) {
        return M3UFilesManager.isValidTrackFile(new File(line));
    }

    private void extractTitleAndDurationFromLine(String line) {

        if (line == null || !line.startsWith(M3U_FILE_META_LINE_BEGINNING)) {
            return;
        }

        String[] firstSplit;
        String[] secondSplit;

        firstSplit = line.split(M3U_FILE_DURATION_NAME_SPLITTER);
        secondSplit = firstSplit[0].split(M3U_FILE_BEGINNING_DURATION_SPLITTER);

        extractedTrackDuration = secondSplit[1];
        extractedTrackTitle = firstSplit[1];
    }

    public static boolean isListAValidM3uList(File sourcePlaylistFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(sourcePlaylistFile))) {
            String line;
            line = br.readLine();
            return line.equals(M3U_FILE_FIRST_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String createFirstLineOfFile() {
        return M3U_FILE_FIRST_LINE;
    }

    public static String createMetaLine(M3UTrack track) {
        return M3U_FILE_META_LINE_BEGINNING + track.getLength() + M3U_FILE_DURATION_NAME_SPLITTER + track.getTitle();
    }
}