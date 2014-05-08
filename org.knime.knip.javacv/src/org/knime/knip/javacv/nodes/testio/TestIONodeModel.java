/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on 11.03.2013 by dietyc
 */
package org.knime.knip.javacv.nodes.testio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
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
import org.knime.knip.base.data.img.ImgPlusCell;
import org.knime.knip.base.data.img.ImgPlusCellFactory;

/**
 * @author dietzc, University of Konstanz
 */
public class TestIONodeModel extends NodeModel {

	protected TestIONodeModel() {
		super(0, 1);
	}

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		return new DataTableSpec[] { createOutSpec() };
	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {

		final ImgPlusCellFactory factory = new ImgPlusCellFactory(exec);

		final BufferedDataContainer container = exec
				.createDataContainer(createOutSpec());

		// X,Y,Time

		// HIER: BefÃ¼lle Img mit daten aus orginal img die du via JavaCV
		// einliest.

		// String path = "C:\myfile\...".

		final String path = "C:\\CurrentImageData\\belgien_tracking\\2014-02-07-7dpf_ctrl_AB_c1_0001.mpeg";

		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(path);
		grabber.setFrameNumber(0);
		grabber.start();

		int dimensionZ = 195;
		int numberOfRows = grabber.getLengthInFrames() / dimensionZ;
		BufferedImage init = grabber.grab().getBufferedImage();
		for (int j = 0; j < numberOfRows; j++) {
			

			Img<UnsignedByteType> img = new ArrayImgFactory<UnsignedByteType>().create(
					new long[] { init.getWidth(), init.getHeight(), 1,
							dimensionZ },
					new UnsignedByteType());
			
			BufferedImage bufferedImage = new BufferedImage(init.getWidth(), init.getHeight(), init.getType());

			final RandomAccess<UnsignedByteType> access = img.randomAccess();
			for (int i = 0; i < img.dimension(3); i++) {
				grabber.setFrameNumber(i + j * numberOfRows);

				bufferedImage = grabber.grab().getBufferedImage();

				access.setPosition(i, 3);

				for (int x = 0; x < bufferedImage.getWidth(); x++) {
					access.setPosition(x, 0);
					for (int y = 0; y < bufferedImage.getHeight(); y++) {
						access.setPosition(y, 1);
						
						int argb = bufferedImage.getRGB(x, y);
						
						access.setPosition(0, 2);
						access.get().set(ARGBType.red(argb));
						
//						 access.setPosition(1, 2);
//						 access.get().set(ARGBType.green(argb));
//						
//						 access.setPosition(2, 2);
//						 access.get().set(ARGBType.blue(argb));
					}
				}
				bufferedImage.flush();
			}
			container.addRowToTable(new DefaultRow("Tims Row" + j, factory
					.createCell(new ImgPlus<UnsignedByteType>(img))));

		}

		int w = grabber.getLengthInFrames() - numberOfRows*dimensionZ;
		if (w > 0) {
			Img<UnsignedByteType> img = new ArrayImgFactory<UnsignedByteType>().create(
					new long[] { init.getWidth(), init.getHeight(), 1,
							w},
					new UnsignedByteType());

			final RandomAccess<UnsignedByteType> access = img.randomAccess();
			for (int i = 0; i < img.dimension(3); i++) {
				grabber.setFrameNumber(grabber.getLengthInFrames() - w + i);
				BufferedImage bufferedImage = grabber.grab().getBufferedImage();

				access.setPosition(i, 3);

				for (int x = 0; x < grabber.getImageWidth(); x++) {
					access.setPosition(x, 0);
					for (int y = 0; y < grabber.getImageHeight(); y++) {
						access.setPosition(y, 1);

						int argb = bufferedImage.getRGB(x, y);

						access.setPosition(0, 2);
						access.get().set(ARGBType.red(argb));

//						 access.setPosition(1, 2);
//						 access.get().set(ARGBType.green(argb));
//						
//						 access.setPosition(2, 2);
//						 access.get().set(ARGBType.blue(argb));
					}
				}
			}
			container.addRowToTable(new DefaultRow("Tims Row" + numberOfRows, factory
					.createCell(new ImgPlus<UnsignedByteType>(img))));
		}
		
		grabber.stop();

		container.close();

		return new BufferedDataTable[] { container.getTable() };
	}

	private DataTableSpec createOutSpec() {
		return new DataTableSpec(new DataColumnSpecCreator("Image",
				ImgPlusCell.TYPE).createSpec());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// Nothing to do since now
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// Nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// Nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}
}