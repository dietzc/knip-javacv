package org.knime.knip.javacv.nodes.io.conversion;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.knip.javacv.IplImageValue;

public class IplImageToPNGImageNodeDialog extends DefaultNodeSettingsPane {

	@SuppressWarnings("unchecked")
	protected IplImageToPNGImageNodeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(
				IplImageToPNGImageNodeModel.createIplImgColumnModel(),
				"IplImage Column:", 0, IplImageValue.class));
	}

}
