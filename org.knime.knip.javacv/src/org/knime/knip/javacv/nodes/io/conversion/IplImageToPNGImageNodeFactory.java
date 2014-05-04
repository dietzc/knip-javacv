package org.knime.knip.javacv.nodes.io.conversion;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IplImageToPNGImageNodeFactory extends
		NodeFactory<IplImageToPNGImageNodeModel> {

	@Override
	public IplImageToPNGImageNodeModel createNodeModel() {
		return new IplImageToPNGImageNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<IplImageToPNGImageNodeModel> createNodeView(int viewIndex,
			IplImageToPNGImageNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new IplImageToPNGImageNodeDialog();
	}

}
