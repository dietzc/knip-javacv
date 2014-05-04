package org.knime.knip.javacv.nodes.io.conversion;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.knip.base.node.NodeUtils;
import org.knime.knip.javacv.IplImageCell;
import org.knime.knip.javacv.IplImageValue;

public class IplImageToPNGImageNodeModel extends NodeModel {

	static SettingsModelString createIplImgColumnModel() {
		return new SettingsModelString("img_column_selection", "");
	}

	private final SettingsModelString m_imgColumn = createIplImgColumnModel();

	protected IplImageToPNGImageNodeModel() {
		super(1, 1);
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

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception {

		BufferedDataTable inputData = inData[0];
		int imageColumn = getImgColIdx(inputData.getDataTableSpec());

		BufferedDataContainer container = exec
				.createDataContainer(createOutputSpec());
		if (imageColumn != -1) {

			CloseableRowIterator iterator = inputData.iterator();
			while (iterator.hasNext()) {
				DataRow row = iterator.next();
				IplImageCell inCell = (IplImageCell) row.getCell(imageColumn);
				IplImage iplImage = inCell.getIplImage();
				BufferedImage bufferedImage = iplImage.getBufferedImage();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, "PNG", baos);
				container.addRowToTable(new DefaultRow(row.getKey(),
						new PNGImageContent(baos.toByteArray()).toImageCell()));
			}
		}

		container.close();
		return new BufferedDataTable[] { container.getTable() };
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		// check
		getImgColIdx(inSpecs[0]);

		return new DataTableSpec[] { createOutputSpec() };
	}

	private DataTableSpec createOutputSpec() {
		DataColumnSpec dcs = new DataColumnSpecCreator("PNG Image",
				PNGImageContent.TYPE).createSpec();
		return new DataTableSpec(dcs);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateSettings(NodeSettingsRO settings)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}

}
