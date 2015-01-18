package org.knime.knip.javacv.nodes.hsvthresholder;

import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

import org.bytedeco.javacpp.opencv_core.CvScalar;
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


public class HSVThresholderNodeModel extends SimpleStreamableFunctionNodeModel
		implements SimpleStreamableNodeModel {

	private IplImage m_currentImg;

	protected double m_lowerH = 20;
	protected double m_lowerS = 30;
	protected double m_lowerV = 170;
	protected double m_upperV = 255;
	protected double m_upperS = 255;
	protected double m_upperH = 255;

	public HSVThresholderNodeModel() {

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
		return new CellFactory() {

			private IplImage m_hsvImg;
			private IplImage m_imgThreshed;
			private CvScalar m_upper;
			private CvScalar m_lower;

			@Override
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {

			}

			@Override
			public DataColumnSpec[] getColumnSpecs() {
				return new DataColumnSpec[] { new DataColumnSpecCreator(
						"Thresholder", IplImageCell.TYPE).createSpec() };
			}

			@Override
			public DataCell[] getCells(DataRow row) {

				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }

				IplImage in = ((IplImageValue) row.getCell(0)).getIplImage();

				m_lower = cvScalar(m_lowerH, m_lowerS, m_lowerV, 0);
				m_upper = cvScalar(m_upperH, m_upperS, m_upperV, 0);

				if (m_hsvImg == null)
					m_hsvImg = IplImage.create(in.width(), in.height(), 8, 3);

				cvCvtColor(in, m_hsvImg, CV_BGR2HSV);

				m_imgThreshed = IplImage.create(in.width(), in.height(), 8, 1);

				cvInRangeS(m_hsvImg, m_lower, m_upper, m_imgThreshed);

				m_currentImg = m_imgThreshed;
				stateChanged();
				return new DataCell[] { new IplImageCell(m_imgThreshed) };
			}
		};
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

	@Override
	public IplImage getFrame() {
		return m_currentImg;
	}

	public void setHSV(double lowerH, double upperH, double lowerS,
			double upperS, double lowerV, double upperV) {
		m_lowerH = lowerH;
		m_upperH = upperH;
		m_lowerS = lowerS;
		m_upperS = upperS;
		m_lowerV = lowerV;
		m_upperV = upperV;
	}
}
