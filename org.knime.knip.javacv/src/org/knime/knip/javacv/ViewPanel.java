package org.knime.knip.javacv;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;

public class ViewPanel extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Image image = null;

	public ViewPanel() {
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		// Calling BufferStrategy.show() here sometimes throws
		// NullPointerException or IllegalStateException,
		// but otherwise seems to work fine.

		BufferStrategy strategy = getBufferStrategy();
		do {
			do {
				if (strategy == null)
					return;
				g = strategy.getDrawGraphics();

				if (image != null) {
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
				}
				g.dispose();
			} while (strategy.contentsRestored());
			strategy.show();
		} while (strategy.contentsLost());

		setVisible(true);
	}

	public void showImage(IplImage iplImg) {

		image = iplImg
				.getBufferedImage(
						iplImg.getBufferedImageType() == BufferedImage.TYPE_CUSTOM ? 1.0
								: 1.0, false);
	}
}
