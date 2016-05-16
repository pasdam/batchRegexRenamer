package com.pasdam.regexren.gui;

import java.awt.Component;
import java.awt.Image;

/**
 * Utility class used to load images
 * 
 * @author paco
 * @version 0.1
 */
public class ImageProvider {
	
	/** Prefix of images paths */
	private static final String IMAGES_PATH_PREFIX = "/com/pasdam/regexren/gui/images/";
	
	/** Singleton instance */
	private static ImageProvider instance = new ImageProvider();
	
	/** Private constructor: avoids direct instantiation */
	private ImageProvider() {}
	
	public static ImageProvider getInstance() {
		return instance;
	}

	/**
	 * Get the specified image
	 * 
	 * @param context
	 *            GUI component for which load the image
	 * @param imageName
	 *            name of the image (including the extension)
	 * @return the specified {@link Image}
	 */
	public Image getImage(Component context, String imageName) {
		return context.getToolkit().getImage(getClass().getResource(IMAGES_PATH_PREFIX + imageName));
	}
}
