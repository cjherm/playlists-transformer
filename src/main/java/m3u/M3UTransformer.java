package m3u;

import m3u.exceptions.InvalidFilePathException;

import java.io.File;

/**
 * Transforms a given playlist into the android friendly playlist
 */
public class M3UTransformer {

    /**
     * @param extractedPlaylist          the extracted but not yet transformed .m3u playlist
     * @param outputFolder               the folder to be hosting all track files
     * @param outputFolderPathInPlaylist the folder path represented in the playlist
     * @return extractedPlaylist the transformed playlist
     */
    public static M3UList transformList(M3UList extractedPlaylist, File outputFolder, String outputFolderPathInPlaylist)throws InvalidFilePathException {
        for (M3UTrack track : extractedPlaylist.getList()) {
            track.setTargetFilePath(createNewTrackPath(track, outputFolder.getAbsolutePath()));
            track.setPathInPlaylist(createNewTrackPath(track, outputFolderPathInPlaylist));
        }
        return extractedPlaylist;
    }

    private static String createNewTrackPath(M3UTrack track, String parentFolderPath) throws InvalidFilePathException {
        if (parentFolderPath.contains(M3UFilesManager.SEP_LINUX) && !parentFolderPath.contains(M3UFilesManager.SEP_WINDOWS)) {
            if (!parentFolderPath.endsWith(M3UFilesManager.SEP_LINUX)) {
                return createNewTrackPathAdjusted(parentFolderPath + M3UFilesManager.SEP_LINUX, track);
            } else {
                return createNewTrackPathAdjusted(parentFolderPath, track);
            }
        } else if (!parentFolderPath.contains(M3UFilesManager.SEP_LINUX) && parentFolderPath.contains(M3UFilesManager.SEP_WINDOWS)) {
            if (!parentFolderPath.endsWith(M3UFilesManager.SEP_WINDOWS)) {
                return createNewTrackPathAdjusted(parentFolderPath + M3UFilesManager.SEP_WINDOWS, track);
            } else {
                return createNewTrackPathAdjusted(parentFolderPath, track);
            }
        }
        throw new InvalidFilePathException(parentFolderPath);
    }

    private static String createNewTrackPathAdjusted(String parentFolderPath, M3UTrack track) {
        return parentFolderPath + track.getFileName();
    }
}