package org.knime.knip.javacv.nodes.io.webcam;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.streamable.InputPortRole;
import org.knime.core.node.streamable.OutputPortRole;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.streamable.PortInput;
import org.knime.core.node.streamable.PortOutput;
import org.knime.core.node.streamable.RowInput;
import org.knime.core.node.streamable.RowOutput;
import org.knime.core.node.streamable.StreamableOperator;
import org.knime.knip.javacv.IplImageCell;

import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class WebCamIONodeModel extends NodeModel implements
		SimpleStreamableNodeModel {

	private DataColumnSpec m_spec;
	public IplImage m_currentImg = null;

	protected WebCamIONodeModel() {
		super(1, 1);

	}

	/** {@inheritDoc} */
	@Override
	public OutputPortRole[] getOutputPortRoles() {
		return new OutputPortRole[] { OutputPortRole.NONDISTRIBUTED };
	}

	public org.knime.core.node.streamable.InputPortRole[] getInputPortRoles() {
		return new InputPortRole[] { InputPortRole.NONDISTRIBUTED_STREAMABLE };
	};

	@Override
	public StreamableOperator createStreamableOperator(
			final PartitionInfo partitionInfo, final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		return new StreamableOperator() {

			private ImageProducer m_producer;

			@Override
			public void runFinal(final PortInput[] inputs,
					final PortOutput[] outputs, final ExecutionContext ctx)
					throws Exception,
					com.googlecode.javacv.FrameRecorder.Exception {

				try {
					((RowInput) inputs[0]).poll();

					if (m_producer == null) {
						m_producer = new ImageProducer();
					}

					m_producer.start();
				} catch (Exception e) {
					// do nothing
				} catch (InterruptedException e) {
					// do nothing
				}
				RowOutput out = (RowOutput) outputs[0];
				try {
					int i = 0;
					int k = 0;
					long init = System.currentTimeMillis();
					IplImage img;
					while ((img = m_producer.get()) != null) {
						// Thread.sleep(100);
						out.push(new DefaultRow(new RowKey("Row" + i),
								new IplImageCell(img)));

						stateChanged();

						k++;

						if (k == 50) {
							long res = System.currentTimeMillis() - init;
							System.out.println("Frame-Rate: " + k
									/ (res / 1000.0d));

							k = 0;
							init = System.currentTimeMillis();
						}
						ctx.checkCanceled();

					}
				} catch (Exception e) {
					e.printStackTrace();

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (CanceledExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					((RowInput) inputs[0]).close();
					out.close();
					m_producer.stop();
				}
			}
		};
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception, InterruptedException {

		// TODO this is super ugly
		final BufferedDataContainer container = exec
				.createDataContainer(new DataTableSpec(m_spec));

		StreamableOperator op;
		try {
			op = createStreamableOperator(new PartitionInfo(0, 1), null);
		} catch (InvalidSettingsException e1) {
			e1.printStackTrace();
		}

		container.close();
		return new BufferedDataTable[] { container.getTable() };
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
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		m_spec = new DataColumnSpecCreator("GrabbedImgs", IplImageCell.TYPE)
				.createSpec();

		return new DataTableSpec[] { new DataTableSpec(m_spec) };
	}

	private class ImageProducer {
		private VideoInputFrameGrabber m_grabber;

		public ImageProducer()
				throws com.googlecode.javacv.FrameRecorder.Exception,
				com.googlecode.javacv.FrameGrabber.Exception {
			m_grabber = null;
			try {
				m_grabber = new VideoInputFrameGrabber(1);
			} finally {
				m_grabber.stop();
			}
		}

		public void stop() throws com.googlecode.javacv.FrameGrabber.Exception {
			m_grabber.stop();
		}

		public void start()
				throws com.googlecode.javacv.FrameGrabber.Exception,
				InterruptedException {

			m_grabber.start();
			Thread.sleep(1000);
			m_currentImg = m_grabber.grab();
			m_grabber.setFrameRate(20.0); 
		}

		public final IplImage get()
				throws com.googlecode.javacv.FrameGrabber.Exception {
			return m_currentImg = m_grabber.grab();
		}
	}

	@Override
	public IplImage getFrame() {
		return m_currentImg;
	}
}