package org.knime.knip.javacv.nodes.testio;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 *
 */
public class TestIONodeFactory extends NodeFactory<TestIONodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<TestIONodeModel> createNodeView(final int viewIndex,
			final TestIONodeModel nodeModel) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TestIONodeModel createNodeModel() {
		return new TestIONodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new DefaultNodeSettingsPane() {
			{

			}
		};
	}
}
