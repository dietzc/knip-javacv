package org.knime.knip.javacv.nodes.io.conversion;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.knip.javacv.IplImageValue;

public class PNGImageToIplImageNodeDialog extends DefaultNodeSettingsPane {

	@SuppressWarnings("unchecked")
	protected PNGImageToIplImageNodeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(
				PNGImageToIplImageNodeModel.createPNGImgColumnModel(),
				"PNG Image Column:", 0, IplImageValue.class));
	}

}
