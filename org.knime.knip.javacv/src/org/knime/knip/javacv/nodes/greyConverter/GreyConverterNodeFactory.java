package org.knime.knip.javacv.nodes.greyConverter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.IplImageStreamingView;

/**
 * 
 * @author dietzc
 */
public class GreyConverterNodeFactory extends NodeFactory<GreyConverterNodeModel> {

	private static GreyConverterNodeModel m_nodeModel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GreyConverterNodeModel createNodeModel() {
		if (m_nodeModel == null)
			m_nodeModel = new GreyConverterNodeModel();
		
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
	public NodeView<GreyConverterNodeModel> createNodeView(int viewIndex,
			GreyConverterNodeModel nodeModel) {
		return new IplImageStreamingView<GreyConverterNodeModel>(nodeModel,
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
		return new GreyConverterNodeDialog();
	}

}
