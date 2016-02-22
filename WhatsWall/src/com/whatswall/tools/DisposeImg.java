package com.whatswall.tools;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.whatswall.base.C;

public class DisposeImg {

	/**
	 * 将Bitmap转成Png字节
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] getPngBitmapBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 将Bitmap转成Png字节
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] getJpgBitmapBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
		return baos.toByteArray();
	}

	/**
	 * 转换图片宽高，通过Width
	 * 
	 * @param is
	 *            [0] width [1] height
	 * @return [0] width [1] height
	 */
	public static int[] convertByWidth(int[] is) {
		int maxWidth = -1;
		float width = is[0];
		float height = is[1];
		float n = 1;
		if (width <= height)
			maxWidth = (C.screenWidth * 2) / 5;
		else
			maxWidth = C.screenWidth / 2;
		if (width > maxWidth) {
			n = width / maxWidth;
			width = maxWidth;
			height = height / n;
		}

		if (width < maxWidth - 10) {
			n = maxWidth / width;
			width = maxWidth;
			height = height * n;
		}
		return new int[] { (int) width, (int) height };
	}

	/**
	 * 计算实际采样率
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {
		// Raw height and width of image
//		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > reqWidth) {

			inSampleSize = Math.round((float) width / (float) reqWidth);

		}
		return inSampleSize;
	}

	/**
	 * 优得到的采样率对图片进行解析
	 * 
	 * @param filename
	 * @param reqWidth
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}
	

}
