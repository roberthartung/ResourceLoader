package rh.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceLoader {
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
		URL resource = ResourceLoader.class.getClassLoader().getResource(path);
		if(resource == null) {
			return null;
		}
		if(runsFromJarFile && resource.toExternalForm().startsWith("jar:")) {
			// Try to get file from within
			String resourcePath = resource.getPath();
			resourcePath = resourcePath.substring(resourcePath.indexOf("!")+2);
			JarEntry jarEntry = jarFile.getJarEntry(resourcePath);
			if(/*jarEntry == null || */jarEntry.isDirectory()) {
				return null;
			} else {
				return new Resource((int) jarEntry.getSize(), jarFile.getInputStream(jarEntry));
			}
		} else {
			File file;
			try {
				file = new File(resource.toURI());
				
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
