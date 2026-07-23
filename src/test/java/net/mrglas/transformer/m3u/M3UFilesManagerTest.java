package net.mrglas.transformer.m3u;

import net.mrglas.transformer.m3u.exceptions.PlaylistAlreadyExistsException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class M3UFilesManagerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // isValidPlaylistFile

    @Test
    public void isValidPlaylistFile_validM3uFile_returnsTrue() throws IOException {
        File playlist = tempFolder.newFile("playlist.m3u");
        Files.write(playlist.toPath(), Collections.singletonList("#EXTM3U"));

        assertTrue(M3UFilesManager.isValidPlaylistFile(playlist));
    }

    @Test
    public void isValidPlaylistFile_wrongExtension_returnsFalse() throws IOException {
        File file = tempFolder.newFile("playlist.txt");
        Files.write(file.toPath(), Collections.singletonList("#EXTM3U"));

        assertFalse(M3UFilesManager.isValidPlaylistFile(file));
    }

    @Test
    public void isValidPlaylistFile_missingHeader_returnsFalse() throws IOException {
        File playlist = tempFolder.newFile("playlist.m3u");
        Files.write(playlist.toPath(), Collections.singletonList("not a header"));

        assertFalse(M3UFilesManager.isValidPlaylistFile(playlist));
    }

    @Test
    public void isValidPlaylistFile_nonExistentFile_returnsFalse() {
        File playlist = new File(tempFolder.getRoot(), "missing.m3u");

        assertFalse(M3UFilesManager.isValidPlaylistFile(playlist));
    }

    // isValidTrackFile

    @Test
    public void isValidTrackFile_supportedExtensions_returnTrue() throws IOException {
        assertTrue(M3UFilesManager.isValidTrackFile(tempFolder.newFile("song.mp3")));
        assertTrue(M3UFilesManager.isValidTrackFile(tempFolder.newFile("song.m4a")));
        assertTrue(M3UFilesManager.isValidTrackFile(tempFolder.newFile("song.wma")));
    }

    @Test
    public void isValidTrackFile_unsupportedExtension_returnsFalse() throws IOException {
        File file = tempFolder.newFile("song.txt");

        assertFalse(M3UFilesManager.isValidTrackFile(file));
    }

    @Test
    public void isValidTrackFile_nonExistentFile_returnsFalse() {
        File file = new File(tempFolder.getRoot(), "missing.mp3");

        assertFalse(M3UFilesManager.isValidTrackFile(file));
    }

    // retrieveValidPlaylistFiles

    @Test
    public void retrieveValidPlaylistFiles_mixedFolder_returnsOnlyValidPlaylists() throws IOException {
        File valid = tempFolder.newFile("valid.m3u");
        Files.write(valid.toPath(), Collections.singletonList("#EXTM3U"));
        File invalid = tempFolder.newFile("invalid.m3u");
        Files.write(invalid.toPath(), Collections.singletonList("not valid"));
        tempFolder.newFile("notes.txt");

        List<File> result = M3UFilesManager.retrieveValidPlaylistFiles(tempFolder.getRoot());

        assertEquals(1, result.size());
        assertEquals("valid.m3u", result.get(0).getName());
    }

    @Test
    public void retrieveValidPlaylistFiles_notADirectory_returnsNull() throws IOException {
        File file = tempFolder.newFile("not_a_dir.m3u");

        assertNull(M3UFilesManager.retrieveValidPlaylistFiles(file));
    }

    // path helpers

    @Test
    public void getFileTypeFromPath_returnsExtensionWithoutDot() {
        assertEquals("mp3", M3UFilesManager.getFileTypeFromPath("C:\\music\\song.mp3"));
    }

    @Test
    public void getFileNameFromPath_returnsFileNameOnly() {
        assertEquals("song.mp3", M3UFilesManager.getFileNameFromPath("C:\\music\\song.mp3"));
    }

    // copyFilesToTarget

    @Test
    public void copyFilesToTarget_copiesSourceToTargetPath() throws IOException {
        File source = tempFolder.newFile("source.mp3");
        Files.write(source.toPath(), "audio-bytes".getBytes());
        File targetDir = tempFolder.newFolder("target");
        File target = new File(targetDir, "source.mp3");

        M3UTrack track = new M3UTrack("Title", "100", source.getAbsolutePath());
        track.setTargetFilePath(target.getAbsolutePath());
        M3UList list = new M3UList(new File("playlist.m3u"));
        list.addTrack(track);

        M3UFilesManager.copyFilesToTarget(list);

        assertTrue(target.exists());
        assertArrayEquals(Files.readAllBytes(source.toPath()), Files.readAllBytes(target.toPath()));
    }

    @Test
    public void copyFilesToTarget_targetAlreadyExists_doesNotOverwrite() throws IOException {
        File source = tempFolder.newFile("source.mp3");
        Files.write(source.toPath(), "new-bytes".getBytes());
        File target = tempFolder.newFile("existing.mp3");
        Files.write(target.toPath(), "old-bytes".getBytes());

        M3UTrack track = new M3UTrack("Title", "100", source.getAbsolutePath());
        track.setTargetFilePath(target.getAbsolutePath());
        M3UList list = new M3UList(new File("playlist.m3u"));
        list.addTrack(track);

        M3UFilesManager.copyFilesToTarget(list);

        assertEquals("old-bytes", new String(Files.readAllBytes(target.toPath())));
    }

    // createNewPlaylistFile

    @Test
    public void createNewPlaylistFile_writesHeaderAndTrackLines() throws IOException, PlaylistAlreadyExistsException {
        File outputFolder = tempFolder.newFolder("Output");
        File originalPlaylist = new File(tempFolder.getRoot(), "myPlaylist.m3u");
        M3UList list = new M3UList(originalPlaylist);
        M3UTrack track = new M3UTrack("My Song", "180", "C:\\music\\song.mp3");
        track.setPathInPlaylist("../Music/Private/song.mp3");
        list.addTrack(track);

        M3UFilesManager.createNewPlaylistFile(list, outputFolder);

        File expected = new File(new File(outputFolder, "transformed_playlists"), "myPlaylist (transformed).m3u");
        assertTrue(expected.exists());
        List<String> lines = Files.readAllLines(expected.toPath());
        assertEquals("#EXTM3U", lines.get(0));
        assertEquals("#EXTINF:180,My Song", lines.get(1));
        assertEquals("../Music/Private/song.mp3", lines.get(2));
    }

    @Test(expected = PlaylistAlreadyExistsException.class)
    public void createNewPlaylistFile_alreadyExists_throwsException() throws IOException, PlaylistAlreadyExistsException {
        File outputFolder = tempFolder.newFolder("Output");
        File originalPlaylist = new File(tempFolder.getRoot(), "myPlaylist.m3u");
        M3UList list = new M3UList(originalPlaylist);
        M3UTrack track = new M3UTrack("Song", "100", "C:\\music\\song.mp3");
        track.setPathInPlaylist("song.mp3");
        list.addTrack(track);

        M3UFilesManager.createNewPlaylistFile(list, outputFolder);
        M3UFilesManager.createNewPlaylistFile(list, outputFolder);
    }
}
