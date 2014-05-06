package org.knime.knip.javacv.nodes.features.surf;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.knip.javacv.IplImageValue;

/**
 * <code>NodeDialog</code> for the "JavaCVSurf" Node. OpenCV Surf
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Daniel Seebacher
 */
public class JavaCVSurfNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring JavaCVSurf node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	@SuppressWarnings("unchecked")
	protected JavaCVSurfNodeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(JavaCVSurfNodeModel.CFGKEY_IMGSELECT,
						JavaCVSurfNodeModel.DEFAULT_IMGSELECT),
				"IplImage Column:", 0, IplImageValue.class));

		createNewGroup("SURF Options");

		addDialogComponent(new DialogComponentNumber(
				new SettingsModelDoubleBounded(
						JavaCVSurfNodeModel.CFGKEY_THRESHOLD,
						JavaCVSurfNodeModel.DEFAULT_THRESHOLD, 0,
						Integer.MAX_VALUE), "Hessian Threshold:", /* step */
				1, /* componentwidth */
				5));

		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(
						JavaCVSurfNodeModel.CFGKEY_OCTAVES,
						JavaCVSurfNodeModel.DEFAULT_OCTAVES, 0,
						Integer.MAX_VALUE), "Octaves:", /* step */
				1, /* componentwidth */
				5));

		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(
						JavaCVSurfNodeModel.CFGKEY_LAYERS,
						JavaCVSurfNodeModel.DEFAULT_LAYERS, 0,
						Integer.MAX_VALUE), "Layers:", /* step */
				1, /* componentwidth */
				5));

		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				JavaCVSurfNodeModel.CFGKEY_EXTENDED,
				JavaCVSurfNodeModel.DEFAULT_EXTENDED), "Extended?"));

		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				JavaCVSurfNodeModel.CFGKEY_UPRIGHT,
				JavaCVSurfNodeModel.DEFAULT_UPRIGHT), "Upright?"));

	}
}
