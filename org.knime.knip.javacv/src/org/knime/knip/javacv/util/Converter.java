package org.knime.knip.javacv.util;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_16S;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_16U;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_1U;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_32F;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_32S;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_64F;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8S;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBDoubleType;
import net.imglib2.type.numeric.NativeARGBDoubleType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.bytedeco.javacpp.opencv_core.IplImage;

@SuppressWarnings("unchecked")
public class Converter {

	/**
	 * Tries to create an {@link IplImage} from a generic {@link Img}.
	 * 
	 * @param input
	 *            the {@link Img} which should be converted
	 * @return An {@link IplImage} or an {@link UnsupportedOperationException}
	 *         if the {@link ImgFactory} or the {@link Type} isn't currently
	 *         supported
	 */
	public static <T extends RealType<T> & NativeType<T>> IplImage createIplImage(
			Img<T> input) throws UnsupportedOperationException {

		// first check if the image type is supported
		if (!isSupportedImageType(input)) {
			throw new UnsupportedOperationException(input.firstElement()
					.getClass().getSimpleName()
					+ " is currently not supported");
		}

		// call the right conversion method
		if (input instanceof ArrayImg<?, ?>) {
			return arrayImgToIplImage(input);
		} else if (input instanceof PlanarImg<?, ?>) {
			throw new UnsupportedOperationException(
					"not yet implemented for planar img");
		} else if (input instanceof CellImg<?, ?, ?>) {
			throw new UnsupportedOperationException(
					"not yet implemented for cell img");
		}

		return null;
	}

	private static <T extends RealType<T> & NativeType<T>> boolean isSupportedImageType(
			Img<T> img) {
		T val = img.firstElement();

		// check for unsupported integer types
		if (val instanceof Unsigned12BitType || val instanceof UnsignedIntType) {
			return false;
		}

		// complex types aren't supported atm
		if (val instanceof ComplexFloatType || val instanceof ComplexDoubleType) {
			return false;
		}

		// ARGB types also aren't supported at the moment
		if (val instanceof ARGBDoubleType
				|| val instanceof NativeARGBDoubleType) {
			return false;
		}

		return true;
	}

	private static <T extends RealType<T> & NativeType<T>> IplImage arrayImgToIplImage(
			Img<T> input) throws UnsupportedOperationException {

		T type = input.firstElement();

		int width = (int) input.dimension(0);
		int height = (int) input.dimension(1);
		int channels = (int) ((input.numDimensions() == 3) ? input.dimension(2)
				: 1);
		int depth = getDepth(type, channels);

		IplImage ii = IplImage.create(width, height, depth, channels);

		if (type instanceof BitType) {
			ArrayImg<BitType, BitArray> img = (ArrayImg<BitType, BitArray>) input;
			int[] data = img.update(null).getCurrentStorageArray();
			ii.getIntBuffer().put(data);
		} else if (type instanceof ByteType || type instanceof UnsignedByteType) {
			ArrayImg<?, ByteArray> img = (ArrayImg<?, ByteArray>) input;
			byte[] data = img.update(null).getCurrentStorageArray();
			ii.getByteBuffer().put(data);
		} else if (type instanceof ShortType
				|| type instanceof UnsignedShortType) {
			ArrayImg<?, ShortArray> img = (ArrayImg<?, ShortArray>) input;
			short[] data = img.update(null).getCurrentStorageArray();
			ii.getShortBuffer().put(data);
		} else if (type instanceof IntType) {
			ArrayImg<?, IntArray> img = (ArrayImg<?, IntArray>) input;
			int[] data = img.update(null).getCurrentStorageArray();
			ii.getIntBuffer().put(data);
		} else if (type instanceof FloatType) {
			ArrayImg<?, FloatArray> img = (ArrayImg<?, FloatArray>) input;
			float[] data = img.update(null).getCurrentStorageArray();
			ii.getFloatBuffer().put(data);
		} else if (type instanceof DoubleType) {
			ArrayImg<?, DoubleArray> img = (ArrayImg<?, DoubleArray>) input;
			double[] data = img.update(null).getCurrentStorageArray();
			ii.getDoubleBuffer().put(data);
		}

		return ii;
	}

	private static <T extends RealType<T> & NativeType<T>> int getDepth(T type,
			int channels) throws UnsupportedOperationException {

		if (type instanceof BitType) {
			return IPL_DEPTH_1U;
		} else if (type instanceof ByteType) {
			return IPL_DEPTH_8S;
		} else if (type instanceof UnsignedByteType) {
			return IPL_DEPTH_8U;
		} else if (type instanceof ShortType) {
			return IPL_DEPTH_16S;
		} else if (type instanceof UnsignedShortType) {
			return IPL_DEPTH_16U;
		} else if (type instanceof IntType) {
			return IPL_DEPTH_32S;
		} else if (type instanceof FloatType) {
			return IPL_DEPTH_32F;
		} else if (type instanceof DoubleType) {
			return IPL_DEPTH_64F;
		}

		throw new UnsupportedOperationException(type.getClass().getSimpleName()
				+ " is not supported");
	}
}
