package org.knime.knip.javacv.nodes.haardetector;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class HaarDetectorNodeFactory extends NodeFactory<HaarDetectorNodeModel> {

	private static HaarDetectorNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HaarDetectorNodeModel createNodeModel() {
		if (m_nodeModel == null)
			m_nodeModel = new HaarDetectorNodeModel();

		return m_nodeModel;
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
	public NodeView<HaarDetectorNodeModel> createNodeView(int viewIndex,
			HaarDetectorNodeModel nodeModel) {
		return new IplImageStreamingView<HaarDetectorNodeModel>(nodeModel,
				"Haar Face Detection");

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
		return new HaarDetectorNodeDialog();
	}

}
