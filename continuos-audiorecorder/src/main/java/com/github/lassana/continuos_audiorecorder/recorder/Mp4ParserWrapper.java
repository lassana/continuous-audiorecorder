package com.github.lassana.continuos_audiorecorder.recorder;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.List;

/**
 * @author lassana
 * @since 8/25/13
 */
public class Mp4ParserWrapper {

    public static void append(String fileName, String anotherFileName) throws IOException {
        File targetFile = new File(fileName);
        File anotherFile = new File(anotherFileName);
        if (targetFile.exists() && targetFile.length()>0) {
            File tmpFile = new File(fileName + ".tmp");
            append(fileName, anotherFileName, tmpFile.getAbsolutePath());
            moveFile(new FileInputStream(tmpFile), new FileOutputStream(targetFile));
            anotherFile.delete();
            tmpFile.delete();
        } else {
            targetFile.createNewFile();
            moveFile(new FileInputStream(anotherFile), new FileOutputStream(targetFile));
        }
    }

    public static void moveFile(FileInputStream in, FileOutputStream out) {
        try {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void append(
            final String firstFile,
            final String secondFile,
            final String newFile) throws IOException {
        File fileOne = new File(secondFile);
        File fileTwo = new File(firstFile);
        FileInputStream fisOne = new FileInputStream(fileOne);
        FileInputStream fisTwo = new FileInputStream(fileTwo);

        Movie video = MovieCreator.build(Channels.newChannel(fisOne));
        Movie videoTwo = MovieCreator.build(Channels.newChannel(fisTwo));
        Movie finalVideo = new Movie();

        List<Track> videoTracks = video.getTracks();
        List<Track> videoTwoTracks = videoTwo.getTracks();

        for (int i = 0; i < videoTracks.size() || i < videoTwoTracks.size(); ++i) {
            finalVideo.addTrack(new AppendTrack(videoTwoTracks.get(i), videoTracks.get(i)));
        }

        IsoFile out = new DefaultMp4Builder().build(finalVideo);
        FileOutputStream fos = new FileOutputStream(new File(String.format(newFile)));
        out.getBox(fos.getChannel());
        fos.close();
    }

}
