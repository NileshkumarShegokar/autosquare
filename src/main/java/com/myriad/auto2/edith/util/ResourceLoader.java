package com.myriad.auto2.edith.util;

import com.myriad.auto2.controllers.SidebarController;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * Created by nileshkumar_shegokar on 6/9/2017.
 */
public class ResourceLoader {

	static Logger log = LogManager.getLogger(ResourceLoader.class.getName());

	public static InputStream getRestrictions() throws URISyntaxException {
		InputStream url = ResourceLoader.class.getResourceAsStream("/restrict.properties");
		return url;
	}
	
	public static String getChromeExtension() throws URISyntaxException {
		URI exe;
		try {
			exe = getFile(getJarURI(), "chrome-extension.crx");
			return new File(exe).getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
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

	public static String getChomeDriverWindowsPath() throws ZipException, IOException, URISyntaxException {
		URI exe = getFile(getJarURI(), "drivers/windows/chromedriver.exe");
		return new File(exe).getAbsolutePath();
	}

	public static String getChomeDriverMacPath() throws ZipException, IOException, URISyntaxException {
		URI exe = getFile(getJarURI(), "drivers/mac/chromedriver");
		log.info("URL : " + exe);
		return new File(exe).getAbsolutePath();
	}

	public static String getChomeDriverLinuxPath() throws ZipException, IOException, URISyntaxException {
		URI exe = getFile(getJarURI(), "drivers/linux/chromedriver");
		log.info("URL : " + exe);
		return new File(exe).getAbsolutePath();
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
                System.out.println("where:"+where.toString());
		location = new File(where);

		// not in a JAR, just return the path on disk
		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
                        System.out.println("File URI:"+fileURI.toString());
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
		System.out.println("Extraction Location :" + tDir);
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
