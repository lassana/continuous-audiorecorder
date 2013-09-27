package com.github.lassana.recorder;

import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.List;

/**
 * @author lassana
 * @since 8/25/13
 */
public class Mp4ParserWrapper {

    public static final String TAG = Mp4ParserWrapper.class.getSimpleName();

    public static final int FILE_BUFFER_SIZE = 1024;

    /**
     * Appends mp4 audio/video from {@code anotherFileName} to {@code mainFileName}.
     */
    public static boolean append(String mainFileName, String anotherFileName) {
        boolean rvalue = false;
        try {
            File targetFile = new File(mainFileName);
            File anotherFile = new File(anotherFileName);
            if (targetFile.exists() && targetFile.length()>0) {
                String tmpFileName = mainFileName + ".tmp";
                append(mainFileName, anotherFileName, tmpFileName);
                copyFile(tmpFileName, mainFileName);
                anotherFile.delete();
                new File(tmpFileName).delete();
                rvalue = true;
            } else if ( targetFile.createNewFile() ) {
                copyFile(anotherFileName, mainFileName);
                anotherFile.delete();
                rvalue = true;
            }
        } catch (IOException e) {
            Log.e(TAG, "Append two mp4 files exception", e);
        }
        return rvalue;
    }


    public static void copyFile(final String from, final String destination)
            throws IOException {
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(destination);
        copy(in, out);
        in.close();
        out.close();
    }

    public static void copy(FileInputStream in, FileOutputStream out) throws IOException {
        byte[] buf = new byte[FILE_BUFFER_SIZE];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    public static void append(
            final String firstFile,
            final String secondFile,
            final String newFile) throws IOException {
        final FileInputStream fisOne = new FileInputStream(new File(secondFile));
        final FileInputStream fisTwo = new FileInputStream(new File(firstFile));
        final FileOutputStream fos = new FileOutputStream(new File(String.format(newFile)));

        append(fisOne, fisTwo, fos);

        fisOne.close();
        fisTwo.close();
        fos.close();
    }

    // FIXME remove deprecated code
    public static void append(
            final FileInputStream fisOne,
            final FileInputStream fisTwo,
            final FileOutputStream out) throws IOException {
        final Movie movieOne = MovieCreator.build(Channels.newChannel(fisOne));
        final Movie movieTwo = MovieCreator.build(Channels.newChannel(fisTwo));
        final Movie finalMovie = new Movie();

        final List<Track> movieOneTracks = movieOne.getTracks();
        final List<Track> movieTwoTracks = movieTwo.getTracks();

        for (int i = 0; i <movieOneTracks.size() || i < movieTwoTracks.size(); ++i) {
            finalMovie.addTrack(new AppendTrack(movieTwoTracks.get(i), movieOneTracks.get(i)));
        }

        final IsoFile isoFile = new DefaultMp4Builder().build(finalMovie);
        isoFile.getBox(out.getChannel());
    }

}
