package br.com.pointel.charvs_know;

public enum SetupRefBaseKind {

    FTP(RefBaseFTP.class);

    private final Class<? extends RefBase> refBaseClass;

    SetupRefBaseKind(Class<? extends RefBase> refBaseClass) {
        this.refBaseClass = refBaseClass;
    }

    public Class<? extends RefBase> getRefBaseClass() {
        return refBaseClass;
    }
    
}
