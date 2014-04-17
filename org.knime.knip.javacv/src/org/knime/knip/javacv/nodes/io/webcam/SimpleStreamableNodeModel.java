package org.knime.knip.javacv.nodes.io.webcam;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface SimpleStreamableNodeModel {

	IplImage getFrame();

}
