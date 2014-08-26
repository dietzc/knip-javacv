package org.knime.knip.javacv.nodes.bittracker;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class BitTrackerNodeFactory extends NodeFactory<BitTrackerNodeModel> {

	private static BitTrackerNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BitTrackerNodeModel createNodeModel() {
		if (m_nodeModel == null)
			m_nodeModel = new BitTrackerNodeModel();
		
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
	public NodeView<BitTrackerNodeModel> createNodeView(int viewIndex,
			BitTrackerNodeModel nodeModel) {
		return new IplImageStreamingView<BitTrackerNodeModel>(nodeModel,
				"BitTracker");
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
		return new BitTrackerNodeDialog();
	}

}
