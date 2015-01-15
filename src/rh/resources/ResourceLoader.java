package rh.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceLoader {
	private static Logger logger = Logger.getLogger(ResourceLoader.class.getName());
	
	static {
		logger.setLevel(Level.ALL);
	}
	
	
	private static boolean runsFromJarFile = false;
	
	private static JarFile jarFile = null;
	
	static {
		File file = new File(ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		runsFromJarFile = file.exists() && file.isFile();
		if(runsFromJarFile) {
			try {
				jarFile = new JarFile(file.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Resource getFileResource(String path) throws IOException {
		URL resource = null;
		// URL resource = ResourceLoader.class.getClassLoader().getResource(path);
		Enumeration<URL> resources = ResourceLoader.class.getClassLoader().getResources(path);
		
		while(resources.hasMoreElements()) {
			resource = resources.nextElement();
		}
		
		logger.finest("getFileResource: " + path + " -> " + resource);
		
		if(resource == null) {
			return null;
		}
		
		if(runsFromJarFile && resource.toExternalForm().startsWith("jar:")) {
			// Try to get file from within
			String resourcePath = resource.getPath();
			// Skip ! and "/" at the beginning
			resourcePath = resourcePath.substring(resourcePath.indexOf("!")+2);
			JarEntry jarEntry = jarFile.getJarEntry(resourcePath);
			if(/*jarEntry == null || */jarEntry.isDirectory()) {
				return null;
			} else {
				return new Resource((int) jarEntry.getSize(), jarFile.getInputStream(jarEntry));
			}
		} else {
			try {
				logger.finest("Resource URI: " + resource.toURI());
				File file = new File(resource.toURI());
				if(file.isDirectory()) {
					return null;
				}
				return new Resource((int) file.length(), new FileInputStream(file));
			} catch (URISyntaxException e) {
				return null;
			}
		}
		
	}
}
