package com.myriad.auto2.engine.util;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.myriad.auto2.controllers.SidebarController;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by nileshkumar_shegokar on 6/9/2017.
 */
public class ResourceLoader {
	private static final int BUFFER_SIZE = 4096;
	static Logger log = LogManager.getLogger(ResourceLoader.class.getName());

	//Spring Content Resource Loader here
	public static String getWebSocketMessage(){
		return getContents("/templates/spring/WebSocketMessage.template");
	}

	public static String getWebSocketConfiguration(){
		return getContents("/templates/spring/WebSocketConfiguration.template");
	}
	public static String getReportController(){
		return getContents("/templates/spring/ReportController.template");
	}
	public static String getAutoSquareReportListner(){
		return getContents("/templates/spring/AutoSquareReportListener.template");
	}
	public static String getApplication(){
		return getContents("/templates/spring/Application.template");
	}
	public static String getApp(){
		return getContents("/templates/spring/App.template");
	}
	public static String getModelReport(){
		return getContents("/templates/spring/model/Report.template");
	}
	public static String getModelAutoSquareTest(){
		return getContents("/templates/spring/model/AutoSquareTest.template");
	}
	public static String getModelAutoSquareSuite(){
		return getContents("/templates/spring/model/AutoSquareSuite.template");
	}

	public static void extractUI(String location,String artifactId) throws IOException {
		String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources";
		unzip("/static.zip",newPath);
	}

	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}

		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(ResourceLoader.class.getResource(zipFilePath).getFile()));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}
	/**
	 * Extracts a zip entry (file entry)
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	//Spring Content Resource Loader ends here
	public static InputStream getRestrictions() throws URISyntaxException {
		InputStream url = ResourceLoader.class.getResourceAsStream("/restrict.properties");
		return url;
	}


	
	public static String getChromeExtension() throws URISyntaxException {
		URI exe;
		try {
			log.info("getting chrome extension from setup");
			exe = getFile(getJarURI(), "chrome-extension.crx");
			//exe = getFile(getJarURI(), "uirecorder.crx");
			return new File(exe).getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("failed to get chrome extension :"+e.getMessage());
		}
		return "";
	}

	public static String getContents(String file)
	{
		InputStream url = ResourceLoader.class.getResourceAsStream(file);
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getScript() throws URISyntaxException {
		InputStream url = ResourceLoader.class.getResourceAsStream("/script/eventsRecorder.js");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}



	public static String getChomeDriverPath() throws IOException{
		log.info("getting chrome driver path");
		File file=checkDriverFile();
		log.info("Chrome Driver :"+file);
		if(!file.exists()){
			log.info("Downloading new Chrome drivers");
			SeleniumDriverDownloader.downloadDriver();
			log.info("Checking file after download");
			file=checkDriverFile();
		}
		log.info("New Chrome driver path :"+file.getAbsolutePath());
		return file.getAbsolutePath();

	}

	private static File checkDriverFile() {
		File file= null;
		if(OperatingSystemDetector.isWindows()) {
			file=new File("chromedriver.exe");
		}else{
			file=new File("chromedriver");
		}
		return file;
	}


	public static String getWindowUtilityTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/windowUtility.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	public static String getScreenUtilityTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/screenshotUtility.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getTestTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/test.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getSnapshotTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/snapshot.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static String getAssertTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/testAssertProperties.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	
	public static String getPOMTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/pom.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getDataProviderTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/dataProvider.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getDataProviderUtilTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/dataProviderUtil.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getDriverFactoryTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/driverFactory.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getTestNGTemplate() {
		URL url = ResourceLoader.class.getResource("/templates/testNG.template");
		return new File(url.getFile()).getAbsolutePath();
	}

	public static String getReportListenerTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/reportListener.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getPropertyManagerTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/propertyManager.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getApplicationTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/application.template");

		String content = "";
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getPageObjectModelTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/pageObjectModel.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getFirefoxDriverPath() throws ZipException, IOException, URISyntaxException {
		URI exe = getFile(getJarURI(), "drivers/geckodriver.exe");
		log.info("Extracted Location  : " + exe);
		return new File(exe).getAbsolutePath();
	}

	private static URI getJarURI() throws URISyntaxException {
		final ProtectionDomain domain;
		final CodeSource source;
		final URL url;
		final URI uri;

		domain = ResourceLoader.class.getProtectionDomain();
		source = domain.getCodeSource();
		url = source.getLocation();
		uri = url.toURI();

		return (uri);
	}

	private static URI getFile(final URI where, final String fileName) throws ZipException, IOException {
		final File location;
		final URI fileURI;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {
			final ZipFile zipFile;

			zipFile = new ZipFile(location);

			try {
				fileURI = extract(zipFile, fileName);
			} finally {
				zipFile.close();
			}
		}

		return (fileURI);
	}

	private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {
		final File tempFile;
		final ZipEntry entry;
		final InputStream zipStream;
		OutputStream fileStream;
		File file = new File(fileName);
		String tDir = System.getProperty("java.io.tmpdir");
		log.info("Zip Extraction Location :" + tDir);
		tempFile = new File(tDir + file.getName());
		// tempFile.deleteOnExit();
		entry = zipFile.getEntry(fileName);

		if (entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
		}

		zipStream = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			final byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while ((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}
		tempFile.setExecutable(true, false);
		return (tempFile.toURI());
	}

	private static void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getLanguageTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/language.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getXPathPropertiesTemplate() {
		InputStream url = ResourceLoader.class.getResourceAsStream("/templates/xpathProperties.template");
		String content = null;
		try {
			content = IOUtils.toString(url, StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
