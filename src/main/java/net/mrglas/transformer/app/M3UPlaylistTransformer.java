package net.mrglas.transformer.app;


import net.mrglas.transformer.m3u.M3UFilesManager;
import net.mrglas.transformer.m3u.M3UList;
import net.mrglas.transformer.m3u.M3UPlaylistReader;
import net.mrglas.transformer.m3u.M3UTransformer;
import net.mrglas.transformer.m3u.exceptions.InvalidFilePathException;
import net.mrglas.transformer.m3u.exceptions.PlaylistExtractionException;
import net.mrglas.transformer.m3u.exceptions.PlaylistAlreadyExistsException;
import net.mrglas.transformer.m3u.exceptions.PlaylistTransformationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class M3UPlaylistTransformer {
    private static final String ERROR_PREFIX = "ERROR";
    private static final String INFO_PREFIX = "INFO";

    /**
     * @param args containing...
     */
    public static void main(String[] args) {

        if (args == null || args.length != 3) {
            logError("I need exactly THREE arguments: <PLAYLIST_FILE_PATH> <OUTPUT_FOLDER_PATH> <ANDROID_PATH>");
            return;
        }

        // The File of the origin M3U-Playlist or the folder containing all playlists
        File playlistInputFile = new File(args[0]);

        // The Folder into which all Music files and the new playlist will be copied
        File outputFolder = new File(args[1]);

        // The relative android folder path which will be written in the new playlist
        String outputFolderPathInPlaylist = args[2];

        List<File> playlistFiles = new ArrayList<>();

        if (M3UFilesManager.isValidPlaylistFile(playlistInputFile)) {
            playlistFiles.add(playlistInputFile);
        } else {
            List<File> retrievedValidPlaylists = M3UFilesManager.retrieveValidPlaylistFiles(playlistInputFile);
            if (retrievedValidPlaylists != null && !retrievedValidPlaylists.isEmpty()) {
                playlistFiles.addAll(retrievedValidPlaylists);
            } else {
                logError("Invalid playlist path! \"" + playlistInputFile.getAbsolutePath() + "\"");
                return;
            }
        }

        if (!M3UFilesManager.isDirectoryValid(outputFolder)) {
            logError("Invalid target path! \"" + outputFolder + "\"");
            return;
        }

        logInfo("Windows-Folder which will contain all music files: " + outputFolder.getAbsolutePath());
        logInfo(" relative Android-Path containing all music files: " + outputFolderPathInPlaylist);

        for (File playlist : playlistFiles) {
            try {
                runMainProcess(playlist, outputFolder, outputFolderPathInPlaylist);
            } catch (PlaylistExtractionException | InvalidFilePathException | IOException | PlaylistAlreadyExistsException | PlaylistTransformationException e) {
                logError(e.getMessage());
            }
        }
        logInfo("We are done!");
    }

    private static void runMainProcess(File playlistFile, File outputFolder, String outputFolderPathInPlaylist) throws PlaylistExtractionException, InvalidFilePathException, IOException, PlaylistAlreadyExistsException, PlaylistTransformationException {
        logInfo("               Path of playlist to be transformed: " + playlistFile.getAbsolutePath());

        M3UList extractedPlaylist = runExtractionProcess(playlistFile);

        M3UList transformedPlaylist = runTransformationProcess(playlistFile, outputFolder, outputFolderPathInPlaylist, extractedPlaylist);

        M3UFilesManager.createNewPlaylistFile(transformedPlaylist, outputFolder);

        M3UFilesManager.copyFilesToTarget(transformedPlaylist);

        logInfo("Transformation process of \"" + playlistFile.getName() + "\" was successful!");
    }

    private static M3UList runTransformationProcess(File playlistFile, File outputFolder, String outputFolderPathInPlaylist, M3UList extractedPlaylist) throws InvalidFilePathException, PlaylistTransformationException {
        M3UList transformedPlaylist = M3UTransformer.transformList(extractedPlaylist, outputFolder, outputFolderPathInPlaylist);

        if (transformedPlaylist.isEmpty()) {
            throw new PlaylistTransformationException("Transformed playlist \"" + playlistFile.getName() + "\" is empty!");
        }

        return transformedPlaylist;
    }

    private static M3UList runExtractionProcess(File playlistFile) throws PlaylistExtractionException {
        M3UPlaylistReader reader = new M3UPlaylistReader(playlistFile);
        M3UList extractedPlaylist = reader.extractM3UList();

        if (extractedPlaylist == null || extractedPlaylist.isEmpty()) {
            throw new PlaylistExtractionException("COULD NOT EXTRACT ANY TRACK FROM PLAYLIST: \"" + playlistFile.getName() + "\"");
        }
        return extractedPlaylist;
    }

    public static void logError(String msg) {
        log(ERROR_PREFIX, msg);
    }

    public static void logInfo(String msg) {
        log(INFO_PREFIX, msg);
    }

    private static void log(String prefix, String msg) {
        System.out.println("[" + prefix + "]" + " " + msg);
    }
}