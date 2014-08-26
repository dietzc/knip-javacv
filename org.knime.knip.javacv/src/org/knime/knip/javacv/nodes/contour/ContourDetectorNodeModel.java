package org.knime.knip.javacv.nodes.contour;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourPerimeter;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

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

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ContourDetectorNodeModel extends SimpleStreamableFunctionNodeModel
		implements SimpleStreamableNodeModel {

	private CellFactory m_cellfactory;
	private IplImage m_currentImg;
	private double m_threshold = 64;

	public ContourDetectorNodeModel() {

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

			private CvMemStorage m_storage;

			@Override
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {

			}

			@Override
			public DataColumnSpec[] getColumnSpecs() {
				return new DataColumnSpec[] { new DataColumnSpecCreator(
						"Contours", IplImageCell.TYPE).createSpec() };
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

				if (m_storage == null)
					m_storage = CvMemStorage.create();

				cvClearMemStorage(m_storage);

				// cvSmooth(in, in, CV_GAUSSIAN, 4)

				IplImage thresholded = IplImage.create(in.width(), in.height(),
						8, 1);

				// Let's find some contours! but first some thresholding...
				cvThreshold(in, thresholded, m_threshold, 255, CV_THRESH_BINARY);

				// To check if an output argument is null we may call either
				// isNull() or equals(null).
				CvSeq contour = new CvSeq(null);
				cvFindContours(thresholded, m_storage, contour,
						Loader.sizeof(CvContour.class), CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE);
				while (contour != null && !contour.isNull()) {
					if (contour.elem_size() > 0) {
						CvSeq points = cvApproxPoly(contour,
								Loader.sizeof(CvContour.class), m_storage,
								CV_POLY_APPROX_DP,
								cvContourPerimeter(contour) * 0.02, 0);
						cvDrawContours(in, points, CvScalar.BLUE,
								CvScalar.BLUE, -1, 1, CV_AA);
					}
					contour = contour.h_next();
				}

				m_currentImg = in;

				stateChanged();

				return new DataCell[] { new IplImageCell(in) };
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

	public void setThreshold(double threshold) {
		m_threshold = threshold;
	}
}
