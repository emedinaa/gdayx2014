package com.gdglima.multimedia.appmultimedia.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class ImageUtils {

	public static Bitmap rotateImage(Bitmap myImg, String path) {
		int orientation = 0;
		ExifInterface exif;
		float angle = 0;
		try {
			exif = new ExifInterface(path);
			Log.i("ImageUtils","exif "+ exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i("ImageUtils", "rotateImage orientation " + orientation);
		if (orientation == 6) {
			angle = 90;
		} else if (orientation == 3) {
			angle = 180;
		} else if (orientation == 8) {
			angle = 270;
		} else if (orientation == 0) {
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
				matrix, true);

		return rotated;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
			int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

}
