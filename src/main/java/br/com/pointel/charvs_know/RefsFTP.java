package br.com.pointel.charvs_know;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.flow.FTP;
import br.com.pointel.jarch.mage.WizBytes;

public class RefsFTP {

    private static final FTP ftp = new FTP(
        System.getenv("CHARVS_KNOW_REFS_FTP_HOST"), 
        Integer.parseInt(System.getenv("CHARVS_KNOW_REFS_FTP_PORT")), 
        System.getenv("CHARVS_KNOW_REFS_FTP_USER"), 
        System.getenv("CHARVS_KNOW_REFS_FTP_PASS"));

    public static void upload(File origin) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var md5 = WizBytes.getMD5(origin);
        var ext = FilenameUtils.getExtension(origin.getName());
        var destiny = md5 + (ext.isEmpty() ? "" : "." + ext);
        var folder = "public_html/refs/" + md5.substring(0, 3);
        ftp.openWithMakeDir(folder);
        ftp.upload(origin, destiny);
    }

    public static void download(String ref, File destiny) throws Exception {
        if (!ftp.isConnected()) {
            ftp.connect();
        }
        var url = "public_html/refs/" + ref.substring(0, 3) + "/" + ref;
        ftp.download(url, destiny);
    }

}
