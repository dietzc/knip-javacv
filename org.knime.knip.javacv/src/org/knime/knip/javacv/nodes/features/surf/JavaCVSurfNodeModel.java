package org.knime.knip.javacv.nodes.features.surf;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d.KeyPoint;
import org.bytedeco.javacpp.opencv_nonfree.SURF;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.knip.base.node.NodeUtils;
import org.knime.knip.javacv.IplImageCell;
import org.knime.knip.javacv.IplImageValue;

/**
 * This is the model implementation of JavaCVSurf. OpenCV Surf
 * 
 * @author Daniel Seebacher
 */
public class JavaCVSurfNodeModel extends NodeModel {

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_IMGSELECT = "img_column_selection";
	static final String CFGKEY_THRESHOLD = "hessian";
	static final String CFGKEY_OCTAVES = "octaves";
	static final String CFGKEY_LAYERS = "layers";
	static final String CFGKEY_EXTENDED = "extended";
	static final String CFGKEY_UPRIGHT = "upright";

	/** initial default count value. */
	static final String DEFAULT_IMGSELECT = "";
	static final double DEFAULT_THRESHOLD = 2500d;
	static final int DEFAULT_OCTAVES = 4;
	static final int DEFAULT_LAYERS = 2;
	static final boolean DEFAULT_EXTENDED = true;
	static final boolean DEFAULT_UPRIGHT = false;

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".
	private final SettingsModelString m_imgColumn = new SettingsModelString(
			CFGKEY_IMGSELECT, DEFAULT_IMGSELECT);

	private final SettingsModelDoubleBounded m_threshold = new SettingsModelDoubleBounded(
			JavaCVSurfNodeModel.CFGKEY_THRESHOLD,
			JavaCVSurfNodeModel.DEFAULT_THRESHOLD, 0, Integer.MAX_VALUE);

	private final SettingsModelIntegerBounded m_octaves = new SettingsModelIntegerBounded(
			JavaCVSurfNodeModel.CFGKEY_OCTAVES,
			JavaCVSurfNodeModel.DEFAULT_OCTAVES, 0, Integer.MAX_VALUE);

	private final SettingsModelIntegerBounded m_layers = new SettingsModelIntegerBounded(
			JavaCVSurfNodeModel.CFGKEY_LAYERS,
			JavaCVSurfNodeModel.DEFAULT_LAYERS, 0, Integer.MAX_VALUE);

	private final SettingsModelBoolean m_extended = new SettingsModelBoolean(
			JavaCVSurfNodeModel.CFGKEY_EXTENDED,
			JavaCVSurfNodeModel.DEFAULT_EXTENDED);

	private final SettingsModelBoolean m_upright = new SettingsModelBoolean(
			JavaCVSurfNodeModel.CFGKEY_UPRIGHT,
			JavaCVSurfNodeModel.DEFAULT_UPRIGHT);

	/**
	 * Constructor for the node model.
	 */
	protected JavaCVSurfNodeModel() {

		// TODO one incoming port and one outgoing port is assumed
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {

		BufferedDataContainer container = exec
				.createDataContainer(createSpec());

		int imgColIdx = getImgColIdx(inData[0].getDataTableSpec());
		if (imgColIdx != -1) {

			CloseableRowIterator rowIt = inData[0].iterator();
			while (rowIt.hasNext()) {
				// get image
				DataRow row = rowIt.next();
				IplImageCell cell = (IplImageCell) row.getCell(imgColIdx);
				IplImage iplImage = cell.getIplImage();

				// image to matrix
				Mat mat = new Mat(iplImage);

				KeyPoint keyPoints = new KeyPoint();
				double hessianThreshold = 2500d;
				int nOctaves = 4;
				int nOctaveLayers = 2;
				boolean extended = true;
				boolean upright = false;
				SURF surf = new SURF(hessianThreshold, nOctaves, nOctaveLayers,
						extended, upright);
				surf.detect(mat, keyPoints);
			}
		}

		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	private int getImgColIdx(final DataTableSpec inSpec)
			throws InvalidSettingsException {
		int imgColIndex = -1;

		imgColIndex = inSpec.findColumnIndex(m_imgColumn.getStringValue());
		if (imgColIndex == -1) {
			if ((imgColIndex = NodeUtils.autoOptionalColumnSelection(inSpec,
					m_imgColumn, IplImageValue.class)) >= 0) {
				setWarningMessage("Auto-configure Image Column: "
						+ m_imgColumn.getStringValue());
			} else {
				throw new InvalidSettingsException("No column selected!");
			}
		}

		return imgColIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		int imgColIdx = getImgColIdx(inSpecs[0]);
		if (imgColIdx == -1) {
			throw new IllegalArgumentException(
					"At least one IplImage Column must be present!");
		}

		return new DataTableSpec[] { createSpec() };
	}

	private DataTableSpec createSpec() {
		return new DataTableSpec(new DataColumnSpecCreator("SURF Features",
				ListCell.getCollectionType(DoubleCell.TYPE)).createSpec());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_imgColumn.saveSettingsTo(settings);
		m_threshold.saveSettingsTo(settings);
		m_octaves.saveSettingsTo(settings);
		m_layers.saveSettingsTo(settings);
		m_extended.saveSettingsTo(settings);
		m_upright.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_imgColumn.loadSettingsFrom(settings);
		m_threshold.loadSettingsFrom(settings);
		m_octaves.loadSettingsFrom(settings);
		m_layers.loadSettingsFrom(settings);
		m_extended.loadSettingsFrom(settings);
		m_upright.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_imgColumn.validateSettings(settings);
		m_threshold.validateSettings(settings);
		m_octaves.validateSettings(settings);
		m_layers.validateSettings(settings);
		m_extended.validateSettings(settings);
		m_upright.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// nothing to do
	}

}
