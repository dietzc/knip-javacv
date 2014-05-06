package org.knime.knip.javacv;

import java.io.IOException;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.container.BlobDataCell;

public class IplImageCell extends BlobDataCell implements IplImageValue {

	/**
	 * Convenience access member for
	 * <code>DataType.getType(StringCell.class)</code>.
	 * 
	 * @see DataType#getType(Class)
	 */
	public static final DataType TYPE = DataType.getType(IplImageCell.class);

	/**
	 * Returns the preferred value class of this cell implementation. This
	 * method is called per reflection to determine which is the preferred
	 * renderer, comparator, etc.
	 * 
	 * @return ImageValue.class;
	 */
	public static final Class<? extends DataValue> getPreferredValueClass() {
		return IplImageValue.class;
	}

	private static final JavaCV2DImgSerializer SERIALIZER = new JavaCV2DImgSerializer();

	public static final JavaCV2DImgSerializer getCellSerializer() {
		return SERIALIZER;
	}

	/** Factory for (de-)serializing a ImageRefCell. */
	private static class JavaCV2DImgSerializer implements
			DataCellSerializer<IplImageCell> {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void serialize(final IplImageCell cell,
				final DataCellDataOutput output) throws IOException {
			byte[] array = new byte[cell.m_img.width() * cell.m_img.height()];
			cell.m_img.getByteBuffer().get(array);
			output.writeInt(array.length);
			output.write(array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IplImageCell deserialize(final DataCellDataInput input)
				throws IOException {
			byte[] array = new byte[input.readInt()];
			input.readFully(array);
			IplImage img = new IplImage(new PointerPointer<>(array));
			return new IplImageCell(img);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IplImage m_img;

	public IplImageCell(IplImage img) {
		m_img = img;
	}

	@Override
	public String toString() {
		return "OpenCV IplImage";
	}

	@Override
	protected boolean equalsDataCell(DataCell dc) {
		return ((IplImageCell) dc).m_img == m_img;
	}

	@Override
	public int hashCode() {
		return m_img.hashCode();
	}

	@Override
	public IplImage getIplImage() {
		return m_img;
	}

}