package org.knime.knip.javacv.nodes.haardetector;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
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
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class HaarDetectorNodeModel extends SimpleStreamableFunctionNodeModel
		implements SimpleStreamableNodeModel {

	private IplImage m_currentImg;

	private static CvHaarClassifierCascade m_classifier = new CvHaarClassifierCascade(
			cvLoad("/home/dietzc/devel/repos/knip-javacv/org.knime.knip.javacv/res/haarcascade_frontalface_default.xml"));

	private CellFactory m_cellFactory;

	@Override
	protected ColumnRearranger createColumnRearranger(DataTableSpec spec)
			throws InvalidSettingsException {
		ColumnRearranger columnRearranger = new ColumnRearranger(spec);
		columnRearranger.replace(createCellFactory(), 0);
		return columnRearranger;
	}

	@Override
	protected boolean isDistributable() {
		return false;
	}

	private CellFactory createCellFactory() {
		m_cellFactory = new CellFactory() {

			private CvMemStorage m_storage;

			@Override
			public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
					ExecutionMonitor exec) {
				// TODO Auto-generated method stub

			}

			@Override
			public DataColumnSpec[] getColumnSpecs() {
				return new DataColumnSpec[] { new DataColumnSpecCreator(
						"FaceWithRectangle", IplImageCell.TYPE).createSpec() };
			}

			@Override
			public DataCell[] getCells(DataRow row) {

				IplImage in = ((IplImageValue) row.getCell(0)).getIplImage();

				if (m_storage == null)
					m_storage = CvMemStorage.create();

				cvClearMemStorage(m_storage);

				CvSeq faces = cvHaarDetectObjects(in, m_classifier, m_storage,
						1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
				int total = faces.total();
				for (int i = 0; i < total; i++) {
					CvRect r = new CvRect(cvGetSeqElem(faces, i));
					int x = r.x(), y = r.y(), w = r.width(), h = r.height();
					cvRectangle(in, cvPoint(x, y), cvPoint(x + w, y + h),
							CvScalar.WHITE, 1, CV_AA, 0);

				}
				m_currentImg = in;
				stateChanged();

				return new DataCell[] { new IplImageCell(in) };

			}

		};

		return m_cellFactory;
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
}
