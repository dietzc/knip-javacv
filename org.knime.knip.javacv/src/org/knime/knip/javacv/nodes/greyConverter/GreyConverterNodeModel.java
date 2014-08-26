package org.knime.knip.javacv.nodes.greyConverter;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.streamable.simple.SimpleStreamableFunctionNodeModel;
import org.knime.knip.javacv.IplImageCell;
import org.knime.knip.javacv.IplImageValue;
import org.knime.knip.javacv.nodes.io.webcam.SimpleStreamableNodeModel;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


public class GreyConverterNodeModel extends SimpleStreamableFunctionNodeModel
		implements SimpleStreamableNodeModel {

	private CellFactory m_cellfactory;
	private IplImage m_currentImg;

	public GreyConverterNodeModel() {

	}

	@Override
	protected boolean isDistributable() {
		return false;
	}

	@Override
	protected ColumnRearranger createColumnRearranger(DataTableSpec spec)
			throws InvalidSettingsException {
		ColumnRearranger columnRearranger = new ColumnRearranger(spec);
		columnRearranger.replace(createCellFactory(), 0);
		return columnRearranger;
	}

	private CellFactory createCellFactory() {
		m_cellfactory = new CellFactory() {

			@Override
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {

			}

			@Override
			public DataColumnSpec[] getColumnSpecs() {
				return new DataColumnSpec[] { new DataColumnSpecCreator("Gray",
						IplImageCell.TYPE).createSpec() };
			}

			@Override
			public DataCell[] getCells(DataRow row) {

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				IplImage in = ((IplImageValue) row.getCell(0)).getIplImage();

				IplImage grayImage = IplImage.create(in.width(), in.height(),
						IPL_DEPTH_8U, 1);

				cvCvtColor(in, grayImage, CV_BGR2GRAY);

				m_currentImg = grayImage;

				stateChanged();

				return new DataCell[] { new IplImageCell(grayImage) };
			}
		};

		return m_cellfactory;
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
		super.reset();
	}

	abstract interface MyCellFactory extends CellFactory {
		void clearFrame();
	}

	@Override
	public IplImage getFrame() {
		return m_currentImg;
	}
}
