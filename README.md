# playlists-transformer

A small Java CLI tool that transforms M3U playlists: it rewrites the file paths of each
track to a new base path (e.g. for use on Android or another device), copies the
referenced music files into an output folder, and writes out the transformed playlist.

## Build

Requires Java 15+ and Maven.

```
mvn clean compile test assembly:single
```

(or just run `build.bat` on Windows)

This produces `target/M3U-Playlist-Transformer-1.0-jar-with-dependencies.jar`.

## Usage

```
java -jar M3U-Playlist-Transformer-1.0-jar-with-dependencies.jar <PLAYLIST_PATH> <OUTPUT_FOLDER> <RELATIVE_PATH>
```

- `PLAYLIST_PATH` — path to a single `.m3u` file, or a folder containing multiple playlists
- `OUTPUT_FOLDER` — folder that the referenced music files and the new playlist will be copied into
- `RELATIVE_PATH` — the path written into the new playlist for each track (e.g. the folder path on the target device)
