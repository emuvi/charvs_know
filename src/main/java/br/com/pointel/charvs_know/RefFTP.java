package br.com.pointel.charvs_know;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.flow.FTP;
import br.com.pointel.jarch.mage.WizBytes;
import br.com.pointel.jarch.mage.WizEnv;

public class RefFTP {


    public static final String BASE_URI = WizEnv.get("CHARVS_KNOW_REFS_FTP_BASE_URI", "https://urvs.com.br/");
    public static final String BASE_FOLDER = WizEnv.get("CHARVS_KNOW_REFS_FTP_BASE_FOLDER", "know/refs/");
    public static final String BASE_PUBLIC = WizEnv.get("CHARVS_KNOW_REFS_FTP_BASE_PUBLIC", "public_html/");
    public static final String BASE_URI_FOLDER = BASE_URI + BASE_FOLDER;


    private static final FTP ftp = new FTP(
        WizEnv.get("CHARVS_KNOW_REFS_FTP_HOST", "ftp.urvs.com.br"),
        WizEnv.get("CHARVS_KNOW_REFS_FTP_PORT", 21),
        WizEnv.get("CHARVS_KNOW_REFS_FTP_USER", "urvs1"),
        WizEnv.get("CHARVS_KNOW_REFS_FTP_PASS", ""));

    public static void upload(File origin) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var md5 = WizBytes.getMD5(origin);
        var ext = FilenameUtils.getExtension(origin.getName());
        var refWithExtension = md5 + (ext.isEmpty() ? "" : "." + ext);
        ftp.openWithMakeDir(getBaseFTPFolder(refWithExtension));
        ftp.upload(origin, refWithExtension);
    }

    public static void upload(File origin, String refWithExtension) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        ftp.openWithMakeDir(getBaseFTPFolder(refWithExtension));
        ftp.upload(origin, refWithExtension);
    }

    public static void download(String refWithExtension, File destiny) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var remote = getBaseFTPFolder(refWithExtension) + "/" + refWithExtension;
        ftp.download(remote, destiny);
    }

    public static String getBaseFTPFolder(String refWithExtension) {
        return BASE_PUBLIC + BASE_FOLDER + refWithExtension.substring(0, 3);
    }

    public static String getBaseURIFolder(String refWithExtension) {
        return BASE_URI + BASE_FOLDER + refWithExtension.substring(0, 3);
    }

    public static String getBaseURI(String refWithExtension) {
        return getBaseURIFolder(refWithExtension) + "/" + refWithExtension;
    }

}
