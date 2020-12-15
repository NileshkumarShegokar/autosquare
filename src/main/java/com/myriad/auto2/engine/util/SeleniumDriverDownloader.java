package com.myriad.auto2.engine.util;

import com.google.googlejavaformat.Op;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class SeleniumDriverDownloader {

    public static boolean downloadDriver() throws IOException {

        Elements select = Jsoup.connect("https://chromedriver.chromium.org")
                .get()
                .select("#sites-canvas-main-content > table > tbody > tr > td > div > div:nth-child(5) > ul > li:nth-child(1) > a");
        String latestVersionURL=select.first().attr("href");
        //find latest version from URL
        String version=getVersion
                (latestVersionURL);
        Document document=Jsoup.connect(latestVersionURL).get();

        //check OS to download specific version
        String downloadURL ="https://chromedriver.storage.googleapis.com/"+version;
        String fileName="";
        if(OperatingSystemDetector.isWindows())
                fileName="chromedriver_win32.zip";
         else if(OperatingSystemDetector.isUnix())
                fileName="chromedriver_linux64.zip";
         else if(OperatingSystemDetector.isMac())
                fileName="chromedriver_mac64.zip";

         if(fileName.isEmpty()){
             return false;
         }
        boolean download=downloadFromInternet(downloadURL,fileName);

        boolean extract=extractDriverZip(fileName);
        return (download && extract);
        }

    private static boolean downloadFromInternet(String downloadURL,String fileName) {

        try {
            InputStream inputStream = new URL(downloadURL+fileName).openStream();
            Files.copy(inputStream, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean extractDriverZip(String driverPath) {
        File dir = new File("driver");
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(driverPath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(  fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                if(newFile.getParent()!=null)
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getVersion(String latestVersionURL) {
        return latestVersionURL.substring(latestVersionURL.indexOf("=")+1);
    }


}
