package br.com.pointel.charvs_know;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.flow.FTP;
import br.com.pointel.jarch.mage.WizBytes;
import br.com.pointel.jarch.mage.WizEnv;

public class RefBase {


    public static final String BASE_URI = WizEnv.get("CHARVS_KNOW_REFS_BASE_BASE_URI", "https://urvs.com.br/");
    public static final String BASE_REFS = WizEnv.get("CHARVS_KNOW_REFS_BASE_BASE_FOLDER", "Conhecimento/+ Refs/");
    public static final String BASE_INSIDE = WizEnv.get("CHARVS_KNOW_REFS_BASE_BASE_INSIDE", "public_html/");
    public static final String BASE_URI_REFS = BASE_URI + BASE_REFS;


    private static final FTP ftp = new FTP(
        WizEnv.get("CHARVS_KNOW_REFS_BASE_FTP_HOST", "ftp.urvs.com.br"),
        WizEnv.get("CHARVS_KNOW_REFS_BASE_FTP_PORT", 21),
        WizEnv.get("CHARVS_KNOW_REFS_BASE_FTP_USER", "urvs1"),
        WizEnv.get("CHARVS_KNOW_REFS_BASE_FTP_PASS", ""));

    public static void upload(File origin) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var hashMD5 = "& " + WizBytes.getMD5(origin);
        var ext = FilenameUtils.getExtension(origin.getName());
        var refWithExtension = hashMD5 + (ext.isEmpty() ? "" : "." + ext);
        ftp.openWithMakeDir(getInsideRefsFolder(refWithExtension));
        ftp.upload(origin, refWithExtension);
    }

    public static void upload(File origin, String refWithExtension) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        ftp.openWithMakeDir(getInsideRefsFolder(refWithExtension));
        ftp.upload(origin, refWithExtension);
    }

    public static void download(String refWithExtension, File destiny) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var remote = getInsideRefsFolder(refWithExtension) + "/" + refWithExtension;
        ftp.download(remote, destiny);
    }

    public static String getURIRefs(String refWithExtension) {
        return getURIRefsFolder(refWithExtension) + "/" + refWithExtension;
    }

    private static String getInsideRefsFolder(String refWithExtension) {
        return BASE_INSIDE + BASE_REFS + refWithExtension.substring(0, 4);
    }

    private static String getURIRefsFolder(String refWithExtension) {
        return BASE_URI + BASE_REFS + refWithExtension.substring(0, 4);
    }

}
