package org.knime.knip.javacv.nodes.contour;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class ContourDetectorNodeFactory extends
		NodeFactory<ContourDetectorNodeModel> {

	private static ContourDetectorNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContourDetectorNodeModel createNodeModel() {
		if (m_nodeModel == null)
			m_nodeModel = new ContourDetectorNodeModel();

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
	public NodeView<ContourDetectorNodeModel> createNodeView(int viewIndex,
			ContourDetectorNodeModel nodeModel) {
		return new InteractiveContourDetectorStreamingView(
				nodeModel, "BitTracker");
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
		return new ContourDetectorNodeDialog();
	}

}
