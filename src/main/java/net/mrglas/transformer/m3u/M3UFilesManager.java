package net.mrglas.transformer.m3u;

import net.mrglas.transformer.app.M3UPlaylistTransformer;
import com.google.common.io.Files;
import net.mrglas.transformer.m3u.exceptions.PlaylistAlreadyExistsException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all file and folder operations
 */
public class M3UFilesManager {

    public static final String M3U_FILE_ENDING = ".m3u";
    public static final String MP3_FILE_ENDING = ".mp3";
    public static final String M4A_FILE_ENDING = ".m4a";
    public static final String WMA_FILE_ENDING = ".wma";
    public static final String PLAYLISTS_FOLDER = "transformed_playlists";
    public static final String SEP_WINDOWS = "\\";
    public static final String SEP_LINUX = "/";
    private static final String[] validTrackFileTypesList = {MP3_FILE_ENDING, M4A_FILE_ENDING, WMA_FILE_ENDING};

    public static boolean isDirectoryValid(File directoryToCheck) {
        if (!directoryToCheck.exists()) {
            M3UPlaylistTransformer.logError("PATH \"" + directoryToCheck + "\" DOES NOT EXIST!");
            return false;
        }
        if (!directoryToCheck.isDirectory()) {
            M3UPlaylistTransformer.logError("PATH \"" + directoryToCheck + "\" IS NOT A DIRECTORY!");
            return false;
        }
        return true;
    }

    public static void copyFilesToTarget(M3UList resultList) throws IOException {
        for (M3UTrack track : resultList.getList()) {
            File sourceFile = new File(track.getOriginFilePath());
            File targetFile = new File(track.getTargetFilePath());

            if (!targetFile.exists()) {
                M3UPlaylistTransformer.logInfo("Copying file from: " + sourceFile.getAbsolutePath());
                M3UPlaylistTransformer.logInfo("               to: " + targetFile.getAbsolutePath());
                Files.copy(sourceFile, targetFile);
                M3UPlaylistTransformer.logInfo("\t...success!");
            }
        }
    }

    public static boolean isValidTrackFile(File fileToCheck) {
        for (String validTrackFileType : validTrackFileTypesList) {
            if (fileToCheck.getAbsolutePath().toLowerCase().endsWith(validTrackFileType)) {
                return isFileTypeValid(fileToCheck);
            }
        }
        M3UPlaylistTransformer.logError("PATH \"" + fileToCheck.getAbsolutePath() + "\" IS NOT A VALID TRACK FILE! IT WILL BE NOT ADDED TO NEW PLAYLIST...");
        return false;
    }

    private static boolean isFileTypeValid(File fileToCheck) {
        if (!fileToCheck.exists()) {
            M3UPlaylistTransformer.logError("PATH \"" + fileToCheck.getAbsolutePath() + "\" DOES NOT EXIST!");
            return false;
        }
        if (!fileToCheck.isFile()) {
            M3UPlaylistTransformer.logError("PATH \"" + fileToCheck.getAbsolutePath() + "\" IS NOT A FILE!");
            return false;
        }
        return true;
    }

    public static void createNewPlaylistFile(M3UList resultList, File outputFolder) throws IOException, PlaylistAlreadyExistsException {
        String playlistsFolderPath = outputFolder.getAbsolutePath() + SEP_WINDOWS + PLAYLISTS_FOLDER;

        File playlistsFolder = new File(playlistsFolderPath);
        boolean mkdirs = playlistsFolder.mkdirs();
        if (!mkdirs && !playlistsFolder.exists()) {
            throw new IOException("Error creating necessary directory: \"" + playlistsFolderPath + "\"");
        }

        String newPlaylistFilePath = playlistsFolderPath + SEP_WINDOWS + resultList.getPlaylistFile().getName();
        newPlaylistFilePath = newPlaylistFilePath.replace(M3U_FILE_ENDING, " (transformed)" + M3U_FILE_ENDING);

        File newPlaylistFile = new File(newPlaylistFilePath);

        // do we already have one?
        if (newPlaylistFile.exists()) {
            throw new PlaylistAlreadyExistsException("PLAYLIST \"" + newPlaylistFile.getName() + "\" ALREADY EXISTS!");
        }

        List<String> fileAsStringList = new ArrayList<>();
        fileAsStringList.add(M3UPlaylistReader.createFirstLineOfFile());

        for (M3UTrack track : resultList.getList()) {
            fileAsStringList.add(M3UPlaylistReader.createMetaLine(track));
            fileAsStringList.add(track.getPathInPlaylist());
        }

        FileOutputStream fos = new FileOutputStream(newPlaylistFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String line : fileAsStringList) {
            bw.write(line);
            bw.newLine();
        }

        bw.close();
    }

    public static String getFileTypeFromPath(String originPath) {
        int index = originPath.lastIndexOf(".");
        return originPath.substring(index + 1);
    }

    public static boolean isValidPlaylistFile(File playlistFile) {
        if (!isFileTypeValid(playlistFile)) {
            return false;
        }
        if (!playlistFile.getAbsolutePath().endsWith(M3U_FILE_ENDING)) {
            return false;
        }
        if (!M3UPlaylistReader.isListAValidM3uList(playlistFile)) {
            return false;
        }
        return true;
    }

    public static String getFileNameFromPath(String originPath) {
        return new File(originPath).getName();
    }

    public static List<File> retrieveValidPlaylistFiles(File folderContainingPlaylists) {
        if (!isDirectoryValid(folderContainingPlaylists)) {
            return null;
        }
        List<File> retrievedValidPlaylistFiles = new ArrayList<>();
        File[] folderContent = folderContainingPlaylists.listFiles();
        if (folderContent == null) {
            return null;
        }
        for (File fileInFolder : folderContent) {
            if (fileInFolder != null && isValidPlaylistFile(fileInFolder)) {
                retrievedValidPlaylistFiles.add(fileInFolder);
            }
        }
        return retrievedValidPlaylistFiles;
    }
}