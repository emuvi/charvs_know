package br.com.pointel.charvs_know;

import java.io.File;

public class SelectedRef {

    public final Ref ref;
    public final File refFile;
    public final File sourceFile;
    public final String refWithExtension;
    public final Runnable updateStatus;

    public SelectedRef(Ref ref, File refFile, File sourceFile, String refWithExtension, Runnable updateStatus) {
        this.ref = ref;
        this.refFile = refFile;
        this.sourceFile = sourceFile;
        this.refWithExtension = refWithExtension;
        this.updateStatus = updateStatus;
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
