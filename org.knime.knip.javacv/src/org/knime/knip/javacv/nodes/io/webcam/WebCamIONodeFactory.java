package org.knime.knip.javacv.nodes.io.webcam;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class WebCamIONodeFactory extends NodeFactory<WebCamIONodeModel> {

	private static WebCamIONodeModel m_singleton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WebCamIONodeModel createNodeModel() {
		// System.out.println("test");

		if (m_singleton == null)
			m_singleton = new WebCamIONodeModel();

		return m_singleton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNrNodeViews() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<WebCamIONodeModel> createNodeView(int viewIndex,
			final WebCamIONodeModel nodeModel) {
		return new IplImageStreamingView<WebCamIONodeModel>(nodeModel, "WebCam");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new WebCamIONodeDialog();
	}

}
