package m3u;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for a .m3u playlist
 */
public class M3UList {

    private final File file;
    private final List<M3UTrack> list;

    public M3UList(File file) {
        this.file = file;
        list = new ArrayList<>();
    }

    public void addTrack(M3UTrack newTrack) {
        if (newTrack != null) {
            list.add(newTrack);
        }
    }

    public List<M3UTrack> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public File getPlaylistFile() {
        return file;
    }
}