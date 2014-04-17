package org.knime.knip.javacv;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;
import org.knime.core.data.StringValueComparator;
import org.knime.core.data.renderer.DataValueRendererFamily;
import org.knime.core.data.renderer.DefaultDataValueRendererFamily;
import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.renderer.ThumbnailRenderer;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface IplImageValue extends DataValue {

	/**
	 * Static singleton for meta description.
	 * 
	 * @see DataValue#UTILITY
	 */
	public static final UtilityFactory UTILITY = new ImageUtilityFactory();

	/** Gathers meta information to this type. */
	public static final class ImageUtilityFactory extends UtilityFactory {

		/** Limits scope of constructor, does nothing. */
		protected ImageUtilityFactory() {
			//
		}

		/*
		 * Specialized icon for ImageCell
		 */
		private static final Icon ICON;

		private static final StringValueComparator COMPARATOR = new StringValueComparator();

		/*
		 * try loading this icon, if fails we use null in the probably silly
		 * assumption everyone can deal with that
		 */
		static {
			ImageIcon icon;
			try {
				final ClassLoader loader = ImgPlusValue.class.getClassLoader();
				final String path = ImgPlusValue.class.getPackage().getName()
						.replace('.', '/');
				icon = new ImageIcon(loader.getResource(path
						+ "/icon/imageicon.png"));
			} catch (final Exception e) {
				icon = null;
			}
			ICON = icon;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Icon getIcon() {
			return ICON;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected DataValueComparator getComparator() {
			return COMPARATOR;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected DataValueRendererFamily getRendererFamily(
				final DataColumnSpec spec) {
			return new DefaultDataValueRendererFamily(
					ThumbnailRenderer.THUMBNAIL_RENDERER);
		}
	}

	/**
	 * Returns IplImage
	 * 
	 * @return
	 */
	public IplImage getIplImage();
}
