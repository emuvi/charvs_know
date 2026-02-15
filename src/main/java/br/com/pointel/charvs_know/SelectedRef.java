package br.com.pointel.charvs_know;

import java.io.File;

public class SelectedRef {

    public final Ref ref;
    public final File refFile;
    public final File sourceFile;
    public final String refWithExtension;

    public SelectedRef(Ref ref, File refFile, File sourceFile, String refWithExtension) {
        this.ref = ref;
        this.refFile = refFile;
        this.sourceFile = sourceFile;
        this.refWithExtension = refWithExtension;
    }

}
