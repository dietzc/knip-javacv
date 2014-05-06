package org.knime.knip.javacv.nodes.features.surf;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "JavaCVSurf" Node.
 * OpenCV Surf 
 *
 * @author Daniel Seebacher
 */
public class JavaCVSurfNodeFactory 
        extends NodeFactory<JavaCVSurfNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaCVSurfNodeModel createNodeModel() {
        return new JavaCVSurfNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<JavaCVSurfNodeModel> createNodeView(final int viewIndex,
            final JavaCVSurfNodeModel nodeModel) {
        return new JavaCVSurfNodeView(nodeModel);
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
    public NodeDialogPane createNodeDialogPane() {
        return new JavaCVSurfNodeDialog();
    }

}

