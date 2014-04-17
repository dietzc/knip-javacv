package org.knime.knip.javacv.nodes.bypass;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class ByPassNodeFactory extends NodeFactory<ByPassNodeModel> {

	private static ByPassNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByPassNodeModel createNodeModel() {
		m_nodeModel = new ByPassNodeModel();

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
	public NodeView<ByPassNodeModel> createNodeView(int viewIndex,
			ByPassNodeModel nodeModel) {
		return new IplImageStreamingView<ByPassNodeModel>(nodeModel,
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
		return new ByPassNodeDialog();
	}

}
