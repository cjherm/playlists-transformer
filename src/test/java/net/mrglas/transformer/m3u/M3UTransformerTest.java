package net.mrglas.transformer.m3u;

import net.mrglas.transformer.m3u.exceptions.InvalidFilePathException;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class M3UTransformerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void transformList_appendsFileNameToOutputFolder() throws IOException, InvalidFilePathException {
        File outputFolder = tempFolder.newFolder("Output");
        M3UTrack track = new M3UTrack("Song Title", "200", "C:\\Source\\song.mp3");
        M3UList list = new M3UList(new File("playlist.m3u"));
        list.addTrack(track);

        M3UTransformer.transformList(list, outputFolder, "../Music/Private");

        assertEquals(outputFolder.getAbsolutePath() + "\\song.mp3", track.getTargetFilePath());
        assertEquals("../Music/Private/song.mp3", track.getPathInPlaylist());
    }

    @Test
    public void transformList_trailingSeparatorAlreadyPresent_doesNotDuplicateSeparator() throws IOException, InvalidFilePathException {
        File outputFolder = tempFolder.newFolder("Output");
        M3UTrack track = new M3UTrack("Title", "100", "C:\\Source\\track.mp3");
        M3UList list = new M3UList(new File("playlist.m3u"));
        list.addTrack(track);

        M3UTransformer.transformList(list, outputFolder, "../Music/Private/");

        assertEquals("../Music/Private/track.mp3", track.getPathInPlaylist());
    }

    @Test
    public void transformList_multipleTracks_transformsEachTrack() throws IOException, InvalidFilePathException {
        File outputFolder = tempFolder.newFolder("Output");
        M3UList list = new M3UList(new File("playlist.m3u"));
        M3UTrack track1 = new M3UTrack("A", "100", "C:\\Source\\a.mp3");
        M3UTrack track2 = new M3UTrack("B", "200", "C:\\Source\\b.mp3");
        list.addTrack(track1);
        list.addTrack(track2);

        M3UTransformer.transformList(list, outputFolder, "../Music/Private/");

        assertEquals(outputFolder.getAbsolutePath() + "\\a.mp3", track1.getTargetFilePath());
        assertEquals(outputFolder.getAbsolutePath() + "\\b.mp3", track2.getTargetFilePath());
    }

    @Test(expected = InvalidFilePathException.class)
    public void transformList_mixedSeparatorsInPath_throwsInvalidFilePathException() throws IOException, InvalidFilePathException {
        File outputFolder = tempFolder.newFolder("Output");
        M3UTrack track = new M3UTrack("Title", "100", "C:\\Source\\track.mp3");
        M3UList list = new M3UList(new File("playlist.m3u"));
        list.addTrack(track);

        M3UTransformer.transformList(list, outputFolder, "folder/sub\\path");
    }
}
