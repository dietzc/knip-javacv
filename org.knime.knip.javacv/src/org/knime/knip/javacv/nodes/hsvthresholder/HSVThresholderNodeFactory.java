package org.knime.knip.javacv.nodes.hsvthresholder;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * 
 * @author dietzc
 */
public class HSVThresholderNodeFactory extends
		NodeFactory<HSVThresholderNodeModel> {

	private static HSVThresholderNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HSVThresholderNodeModel createNodeModel() {
		if (m_nodeModel == null)
			m_nodeModel = new HSVThresholderNodeModel();

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
	public NodeView<HSVThresholderNodeModel> createNodeView(int viewIndex,
			HSVThresholderNodeModel nodeModel) {
		return new InteractiveThresholdingStreamingView(nodeModel,
				"HSV Thresholder") {

		};
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
		return new HSVThresholderNodeDialog();
	}

}
