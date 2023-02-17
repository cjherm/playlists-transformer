package net.mrglas.transformer.app;

import org.junit.Test;

import java.io.File;

public class M3UPlaylistTransformerTest {

    private static final String PATH_RESOURCES = "src\\test\\resources";
        public static final String PATH_PLAYLISTS = PATH_RESOURCES + "\\test_playlists\\";
    public static final String FOLDER_CONTAINING_MULTIPLE_PLAYLISTS = PATH_RESOURCES + "\\test_playlists";
    public static final String ORIGIN_PLAYLIST_FILE_PATH = PATH_PLAYLISTS + "testPlayList.net.mrglas.m3u.m3u";
    public static final String OUTPUT_FOLDER_PATH = PATH_RESOURCES + "\\output_folder";
    public static final String TRANSFORMED_PLAYLIST_FILE_PATH = PATH_RESOURCES + "\\testPlayList (transformed).net.mrglas.m3u.m3u";
    public static final String RELATIVE_ANDROID_OUTPUT_PATH = "../Music/Private/";


    @Test
    public void testMain() {
       // String[] args = {ORIGIN_PLAYLIST_FILE_PATH, OUTPUT_FOLDER_PATH, RELATIVE_ANDROID_OUTPUT_PATH};
        //    M3UPlaylistTransformer.main(args);
        //  deleteNewFile(TRANSFORMED_PLAYLIST_FILE_PATH);
    }

    private void deleteNewFile(String fileToDeletePath) {
        File fileToDelete = new File(fileToDeletePath);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            fileToDelete.delete();
        }
    }

    @Test
    public void test_multiplePlaylists(){
        //   String[] args = {FOLDER_CONTAINING_MULTIPLE_PLAYLISTS, OUTPUT_FOLDER_PATH, RELATIVE_ANDROID_OUTPUT_PATH};
        //  M3UPlaylistTransformer.main(args);
    }
}