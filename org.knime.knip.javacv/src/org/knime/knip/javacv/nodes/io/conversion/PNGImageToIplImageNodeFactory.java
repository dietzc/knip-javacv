package org.knime.knip.javacv.nodes.io.conversion;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class PNGImageToIplImageNodeFactory extends
		NodeFactory<PNGImageToIplImageNodeModel> {

	@Override
	public PNGImageToIplImageNodeModel createNodeModel() {
		return new PNGImageToIplImageNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<PNGImageToIplImageNodeModel> createNodeView(int viewIndex,
			PNGImageToIplImageNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new PNGImageToIplImageNodeDialog();
	}

}
