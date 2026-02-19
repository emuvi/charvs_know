package br.com.pointel.charvs_know;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectedRef {


    private static final Logger logger = LoggerFactory.getLogger(SelectedRef.class);


    public final File baseFolder;
    public final Ref ref;
    public final File refFile;
    public final File sourceFile;
    public final String refWithExtension;
    public final Runnable updateStatus;
    public final Talker talker;

    public SelectedRef(File baseFolder, Ref ref, File refFile, File sourceFile, String refWithExtension, Runnable updateStatus) {
        this.baseFolder = baseFolder;
        this.ref = ref;
        this.refFile = refFile;
        this.sourceFile = sourceFile;
        this.refWithExtension = refWithExtension;
        this.updateStatus = updateStatus;
        this.talker = Talker.get();
    }

    public String talk(String command) {
        logger.info("Command: {}", command);
        return talker.talk(command);
    }

    public String talkWithAttach(String command) {
        var baseURI = RefFTP.getBaseURI(refWithExtension);
        logger.info("Command: {} Base URI: {}", command, baseURI);
        return talker.talk(command, UriMime.of(baseURI));
    }

    public void read() throws Exception {
        RefDatex.read(ref, refFile);
        updateStatus.run();
    }

    public void write() throws Exception {
        RefDatex.write(ref, refFile);
        updateStatus.run();
    }

}
