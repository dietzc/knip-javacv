package org.knime.knip.javacv.nodes.io.webcam;

import org.bytedeco.javacpp.opencv_core.IplImage;

public interface SimpleStreamableNodeModel {

	IplImage getFrame();

}
