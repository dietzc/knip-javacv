package org.knime.knip.javacv.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;

public class Converter {

	public static IplImage bufferdImageToIplImage(BufferedImage bi) {
		return IplImage.createFrom(bi);
	}

	public static IplImage imageToIplImage(Image i) {
		BufferedImage bi = null;

		if (i instanceof BufferedImage) {
			bi = (BufferedImage) i;
		} else {
			bi = new BufferedImage(i.getWidth(null), i.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(i, 0, 0, null);
			g.dispose();
		}

		return bufferdImageToIplImage(bi);
	}
}
