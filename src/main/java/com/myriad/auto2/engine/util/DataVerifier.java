package com.myriad.auto2.engine.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class DataVerifier {

	public static String processIdentifier(String identifier) {
		if(identifier !=null ) {
			return identifier.replaceAll("\u00A0", "");
		}
		return null;
	}
	
	public static String generateIdentifier(String identifier) {
		return identifier.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	public static Rectangle getBounds(int width,int height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x=(screenSize.width-width)/2;
		int y=(screenSize.height-height)/2;		
		return new Rectangle(x, y, width, height);
	}
}
