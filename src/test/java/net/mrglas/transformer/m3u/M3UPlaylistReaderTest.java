package net.mrglas.transformer.m3u;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class M3UPlaylistReaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void extractM3UList_singleValidTrack_parsesTitleLengthAndPath() throws IOException {
        File trackFile = tempFolder.newFile("song.mp3");
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Arrays.asList(
                "#EXTM3U",
                "#EXTINF:239,SOS - Rihanna",
                trackFile.getAbsolutePath()
        ));

        M3UList result = new M3UPlaylistReader(playlistFile).extractM3UList();

        assertEquals(1, result.getList().size());
        M3UTrack track = result.getList().get(0);
        assertEquals("SOS - Rihanna", track.getTitle());
        assertEquals("239", track.getLength());
        assertEquals(trackFile.getAbsolutePath(), track.getOriginFilePath());
    }

    @Test
    public void extractM3UList_multipleTracks_parsesAllInOrder() throws IOException {
        File track1 = tempFolder.newFile("a.mp3");
        File track2 = tempFolder.newFile("b.mp3");
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Arrays.asList(
                "#EXTM3U",
                "#EXTINF:100,Track A",
                track1.getAbsolutePath(),
                "#EXTINF:200,Track B",
                track2.getAbsolutePath()
        ));

        M3UList result = new M3UPlaylistReader(playlistFile).extractM3UList();

        assertEquals(2, result.getList().size());
        assertEquals("Track A", result.getList().get(0).getTitle());
        assertEquals("Track B", result.getList().get(1).getTitle());
    }

    @Test
    public void extractM3UList_trackFileDoesNotExist_isSkipped() throws IOException {
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Arrays.asList(
                "#EXTM3U",
                "#EXTINF:100,Missing Track",
                new File(tempFolder.getRoot(), "missing.mp3").getAbsolutePath()
        ));

        M3UList result = new M3UPlaylistReader(playlistFile).extractM3UList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void extractM3UList_trackFileHasUnsupportedExtension_isSkipped() throws IOException {
        File trackFile = tempFolder.newFile("notes.txt");
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Arrays.asList(
                "#EXTM3U",
                "#EXTINF:100,Not A Track",
                trackFile.getAbsolutePath()
        ));

        M3UList result = new M3UPlaylistReader(playlistFile).extractM3UList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void isListAValidM3uList_correctHeader_returnsTrue() throws IOException {
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Collections.singletonList("#EXTM3U"));

        assertTrue(M3UPlaylistReader.isListAValidM3uList(playlistFile));
    }

    @Test
    public void isListAValidM3uList_wrongHeader_returnsFalse() throws IOException {
        File playlistFile = tempFolder.newFile("playlist.m3u");
        Files.write(playlistFile.toPath(), Collections.singletonList("NOT A HEADER"));

        assertFalse(M3UPlaylistReader.isListAValidM3uList(playlistFile));
    }

    @Test
    public void createFirstLineOfFile_returnsExtm3uHeader() {
        assertEquals("#EXTM3U", M3UPlaylistReader.createFirstLineOfFile());
    }

    @Test
    public void createMetaLine_buildsExtinfLine() {
        M3UTrack track = new M3UTrack("My Song", "180", "C:\\music\\song.mp3");
        assertEquals("#EXTINF:180,My Song", M3UPlaylistReader.createMetaLine(track));
    }
}
