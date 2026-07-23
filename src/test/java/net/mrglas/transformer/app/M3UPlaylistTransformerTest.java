package net.mrglas.transformer.app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class M3UPlaylistTransformerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void main_validPlaylist_createsTransformedPlaylistAndCopiesTrack() throws IOException {
        File sourceTrack = tempFolder.newFile("song.mp3");
        Files.write(sourceTrack.toPath(), "audio".getBytes());
        File playlist = tempFolder.newFile("playlist.m3u");
        Files.write(playlist.toPath(), Arrays.asList(
                "#EXTM3U",
                "#EXTINF:200,My Song",
                sourceTrack.getAbsolutePath()
        ));
        File outputFolder = tempFolder.newFolder("output");

        M3UPlaylistTransformer.main(new String[]{
                playlist.getAbsolutePath(), outputFolder.getAbsolutePath(), "../Music/Private/"
        });

        File transformedPlaylist = new File(new File(outputFolder, "transformed_playlists"), "playlist (transformed).m3u");
        assertTrue(transformedPlaylist.exists());
        assertTrue(new File(outputFolder, "song.mp3").exists());
    }

    @Test
    public void main_folderWithMultiplePlaylists_transformsEachOne() throws IOException {
        File playlistsFolder = tempFolder.newFolder("playlists");
        File track1 = new File(playlistsFolder, "a.mp3");
        Files.write(track1.toPath(), "a".getBytes());
        File track2 = new File(playlistsFolder, "b.mp3");
        Files.write(track2.toPath(), "b".getBytes());

        File playlist1 = new File(playlistsFolder, "one.m3u");
        Files.write(playlist1.toPath(), Arrays.asList("#EXTM3U", "#EXTINF:100,A", track1.getAbsolutePath()));
        File playlist2 = new File(playlistsFolder, "two.m3u");
        Files.write(playlist2.toPath(), Arrays.asList("#EXTM3U", "#EXTINF:200,B", track2.getAbsolutePath()));

        File outputFolder = tempFolder.newFolder("output");

        M3UPlaylistTransformer.main(new String[]{
                playlistsFolder.getAbsolutePath(), outputFolder.getAbsolutePath(), "../Music/Private/"
        });

        File transformedFolder = new File(outputFolder, "transformed_playlists");
        assertTrue(new File(transformedFolder, "one (transformed).m3u").exists());
        assertTrue(new File(transformedFolder, "two (transformed).m3u").exists());
    }

    @Test
    public void main_wrongArgumentCount_doesNotThrow() {
        M3UPlaylistTransformer.main(new String[]{"only-one-arg"});
    }

    @Test
    public void main_nullArgs_doesNotThrow() {
        M3UPlaylistTransformer.main(null);
    }

    @Test
    public void main_invalidPlaylistPath_producesNoOutput() throws IOException {
        File outputFolder = tempFolder.newFolder("output");
        File missingPlaylist = new File(tempFolder.getRoot(), "missing.m3u");

        M3UPlaylistTransformer.main(new String[]{
                missingPlaylist.getAbsolutePath(), outputFolder.getAbsolutePath(), "../Music/Private/"
        });

        assertFalse(new File(outputFolder, "transformed_playlists").exists());
    }

    @Test
    public void main_invalidOutputFolder_doesNotThrow() throws IOException {
        File playlist = tempFolder.newFile("playlist.m3u");
        Files.write(playlist.toPath(), Collections.singletonList("#EXTM3U"));
        File missingOutputFolder = new File(tempFolder.getRoot(), "does-not-exist");

        M3UPlaylistTransformer.main(new String[]{
                playlist.getAbsolutePath(), missingOutputFolder.getAbsolutePath(), "../Music/Private/"
        });

        assertFalse(missingOutputFolder.exists());
    }

    @Test
    public void main_playlistWithNoValidTracks_producesNoOutputAndDoesNotThrow() throws IOException {
        File playlist = tempFolder.newFile("playlist.m3u");
        Files.write(playlist.toPath(), Collections.singletonList("#EXTM3U"));
        File outputFolder = tempFolder.newFolder("output");

        M3UPlaylistTransformer.main(new String[]{
                playlist.getAbsolutePath(), outputFolder.getAbsolutePath(), "../Music/Private/"
        });

        assertFalse(new File(outputFolder, "transformed_playlists").exists());
    }
}
