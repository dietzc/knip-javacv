package org.knime.knip.javacv.nodes.bittracker;

import static org.bytedeco.javacpp.opencv_core.cvLine;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
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

public class BitTrackerNodeModel extends SimpleStreamableFunctionNodeModel
		implements SimpleStreamableNodeModel {

	private CellFactory m_cellfactory;
	private IplImage m_currentImg;

	public BitTrackerNodeModel() {

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

			private IplImage m_scribble;
			private int m_lastX;
			private int m_lastY;
			private CvScalar m_lineColor;

			@Override
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {

			}

			@Override
			public DataColumnSpec[] getColumnSpecs() {
				return new DataColumnSpec[] { new DataColumnSpecCreator(
						"Tracked Moment", IplImageCell.TYPE).createSpec() };
			}

			@Override
			public DataCell[] getCells(DataRow row) {

				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				IplImage in = ((IplImageValue) row.getCell(0)).getIplImage();

				if (m_lineColor == null)
					m_lineColor = new CvScalar().val(0, 0).val(1, 255)
							.val(2, 255);

				// if (m_imgThreshed == null)
				if (m_scribble == null)
					m_scribble = IplImage.create(in.width(), in.height(), 8, 3);

				// Moments need to be calculated
				CvMoments moments = new CvMoments(1);
				cvMoments(in, moments, 1);
				// Calculate the moments to estimate the position of the ball
				moments.m10();
				moments.m01();
				cvMoments(in, moments, 1);
				double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
				double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
				double area = cvGetCentralMoment(moments, 0, 0);

				int posX = (int) (momX10 / area);
				int posY = (int) (momY01 / area);

				if (posX != 0 && posY != 0 && m_lastX != 0 && m_lastY != 0) {
					cvLine(m_scribble, cvPoint(posX, posY),
							cvPoint(m_lastX, m_lastY), m_lineColor, 3, 0, 0);
				}

				m_lastX = posX;
				m_lastY = posY;

				m_currentImg = m_scribble;

				stateChanged();

				return new DataCell[] { new IplImageCell(m_scribble) };
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
