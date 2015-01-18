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
 * --------------------------------------------------------------------- *
 *
 */
package org.knime.knip.javacv;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael
 *         Zinsmaier</a>
 */
public class JavaCVNodeActivator implements BundleActivator {

	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(JavaCVNodeActivator.class);

	private static boolean JavaCVLoaded = false;

	//
	// "jniopencv_legacy", "jniopencv_ml",
	// "jniopencv_nonfree", "jniopencv_objdetect", "jniopencv_photo",
	// "jniopencv_stitching", "jniopencv_video", "jniopencv_videostab",
	// "jnipostproc", "jniswresample", "jniswscale",
	//
	// , "jniavcodec", "jniavdevice", "jniavfilter",
	// "jniavformat", "jniavutil", "jnifreenect", "jniopencv_calib3d",
	// "jniopencv_contrib", "jniopencv_features2d", "jniopencv_flann",
	// "jniopencv_highgui", "jniopencv_imgproc"
	/**
	 * This method trys to load all libs that are passed to it.<br>
	 * 
	 * To ensure the libs are actually all loaded, the programmer has to make
	 * sure that the libs are in correct order.
	 * 
	 * @param libs
	 *            the system specific libs to load
	 * 
	 * @throws UnsatisfiedLinkError
	 *             if one or more libs could not be loaded at all
	 */
	private void loadLibs(final String[] libs) {

		if (libs != null) {
			for (final String s : libs) {
				System.loadLibrary(s);
			}
		}
	}

	@Override
	public final void start(final BundleContext context) throws Exception {

		LOGGER.debug("Trying to load JavaCV libs");

		// final String os = System.getProperty("os.name");

		try {

			// loadLibs(WINDOWS);

			// if (os.contains("Windows")) {
			// loadLibs(windows);
			// } else if (os.equals("Linux")) {
			// loadLibs(linux);
			// } else if (os.equals("Mac OS X")) {
			// loadLibs(osx);
			// } else {
			// LOGGER.error("VTK not loaded, could not determine System: "
			// + os);
			// }

			Loader.load(opencv_core.class);
			//
			// LOGGER.debug("JavaCV successfully loaded");
			// JavaCVLoaded = true;

		} catch (final UnsatisfiedLinkError e) {
			LOGGER.error("Could not load JavaCV");
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public final void stop(final BundleContext context) throws Exception {
		// unused
	}

	public static final boolean JavaCVLoaded() {
		return JavaCVLoaded;
	}
}
