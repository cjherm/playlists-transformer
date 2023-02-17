package net.mrglas.transformer.m3u;

/**
 * Data structure for a track in a .net.mrglas.m3u.m3u playlist
 */
public class M3UTrack {

    private final String title;
    private final String length;
    private final String originFilePath;
    private final String fileType;
    private final String fileName;
    private String targetFilePath;
    private String pathInPlaylist;

    /**
     *
     * @param title
     * @param length
     * @param originPath
     */
    public M3UTrack(String title, String length, String originPath) {
        this.title = title;
        this.length = length;
        this.originFilePath = originPath;
        this.fileType = M3UFilesManager.getFileTypeFromPath(originPath);
        this.fileName = M3UFilesManager.getFileNameFromPath(originPath);
    }

    /**
     *
     * @param targetFilePath
     */
    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    /**
     *
     * @param pathInPlaylist
     */
    public void setPathInPlaylist(String pathInPlaylist) {
        this.pathInPlaylist = pathInPlaylist.replace("\\", "/");
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public String getLength() {
        return length;
    }

    /**
     *
     * @return
     */
    public String getTargetFilePath() {
        return targetFilePath;
    }

    /**
     *
     * @return
     */
    public String getOriginFilePath() {
        return originFilePath;
    }

    /**
     *
     * @return
     */
    public String getPathInPlaylist() {
        return pathInPlaylist;
    }

    /**
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }
}