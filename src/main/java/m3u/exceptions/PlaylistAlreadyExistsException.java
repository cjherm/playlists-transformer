package m3u.exceptions;

public class PlaylistAlreadyExistsException extends Throwable {
    public PlaylistAlreadyExistsException(String msg) {
        super(msg);
    }
}
