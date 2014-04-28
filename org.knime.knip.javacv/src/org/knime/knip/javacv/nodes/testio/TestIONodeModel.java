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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.real.FloatType;

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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.avcodec.AVCodec;
import com.googlecode.javacv.cpp.avcodec.AVCodecContext;
import com.googlecode.javacv.cpp.avcodec.AVFrame;
import com.googlecode.javacv.cpp.avcodec.AVPacket;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

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

		Loader.load(opencv_core.class);
		Loader.load(AVPacket.class);
		Loader.load(avcodec.class);

		final ImgPlusCellFactory factory = new ImgPlusCellFactory(exec);

		final BufferedDataContainer container = exec
				.createDataContainer(createOutSpec());

		// X,Y,Time
		final Img<FloatType> img;

		// HIER: BefÃ¼lle Img mit daten aus orginal img die du via JavaCV
		// einliest.

		// String path = "C:\myfile\...".

		final String path = "/home/tibuch/workspace_knime/DELTA.MPG";
		final AVCodec codec;
		final AVCodecContext c;
		int frame, len;
		int[] got_picture = new int[1];
		final AVFrame picture;
		IplImage iplImg;
		ByteBuffer inbufBB = ByteBuffer
				.allocateDirect(4096 + avcodec.FF_INPUT_BUFFER_PADDING_SIZE);

		BytePointer inbuf = new BytePointer(inbufBB);

		AVPacket avpkt = new AVPacket();

		avcodec.av_init_packet(avpkt);

		codec = avcodec.avcodec_find_decoder(avcodec.AV_CODEC_ID_MPEG1VIDEO);
		if (codec == null)
			System.out.println("Codec not found");

		c = avcodec.avcodec_alloc_context3(codec);
		if ((codec.capabilities() & avcodec.CODEC_CAP_TRUNCATED) != 0)
			c.flags(c.flags() | avcodec.CODEC_FLAG_TRUNCATED);

		FileChannel fin = new FileInputStream(path).getChannel();

		picture = avcodec.avcodec_alloc_frame();
		if (picture == null)
			System.out.println("Could not allocate video frame.");

		frame = 0;

		img = new ArrayImgFactory<FloatType>().create(
				new long[] { c.width(), c.height(), avpkt.size() },
				new FloatType());

		while (true) {
			inbufBB.position(0).limit(4096);
			avpkt.size(fin.read(inbufBB));
			if (avpkt.size() <= 0)
				break;

			avpkt.data(inbuf);
			while (avpkt.size() > 0) {
				len = avcodec.avcodec_decode_video2(c, picture, got_picture,
						avpkt);
				if (len < 0) {
					System.err.printf("Error while decoding frame %d\n", frame);
				}
				if (got_picture[0] != 0) {
					System.out.printf("Saving frame %3d\n", frame);
					String buf = String.format("output", frame);
					iplImg = IplImage.create(picture.width(), picture.height(),
							avpkt.size(), 1);

				}

			}

		}

		final ImgPlus<FloatType> wrappedImg = new ImgPlus<FloatType>(img);

		container.addRowToTable(new DefaultRow("Tims Row", factory
				.createCell(wrappedImg)));

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