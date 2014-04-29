package org.knime.knip.javacv;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.nodes.io.webcam.SimpleStreamableNodeModel;

public class IplImageStreamingView<K extends NodeModel & SimpleStreamableNodeModel>
		extends NodeView<K> {

	private K m_model;
	// private JPanel m_validPeer;
	private ViewPanel m_canvas;
	private boolean first;

	public IplImageStreamingView(K nodeModel, String title) {
		super(nodeModel);
		m_model = nodeModel;
		m_canvas = new ViewPanel();
		setShowNODATALabel(false);
		setComponent(m_canvas);
	}

	@Override
	protected void onClose() {
		first = false;
	}

	@Override
	protected void onOpen() {
		m_canvas.setSize(400, 400);
		first = true;
	}

	@Override
	protected void modelChanged() {

		IplImage img = null;

		if (!first && m_canvas.getBufferStrategy() == null) {
			System.out.println("called buffer strategy");
			m_canvas.createBufferStrategy(2);
		}

		first = false;

		// First needs to be !=null
		if ((img = m_model.getFrame()) != null) {
			m_canvas.showImage(img);
			m_canvas.paint(null);
		}
	}
}
