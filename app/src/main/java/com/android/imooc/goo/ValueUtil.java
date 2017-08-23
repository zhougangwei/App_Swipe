package com.android.imooc.goo;

/**
 * @描述 估值器工具类
 * @项目名称 App_imooc
 * @包名 com.android.imooc.paralla
 * @类名 ValueUtil
 * @author chenlin
 * @date 2015年5月29日 下午12:29:12
 * @version 1.0
 */

public class ValueUtil {

	/**
	 * 估值器，得到移动后的位置
	 * 
	 * @param percent
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public static Float evalute(float percent, Number startValue, Number endValue) {
		float start = startValue.floatValue();
		return start + percent * (endValue.floatValue() - start);
	}

	/**
	 * 颜色变化过度
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public static Object evaluateColor(float fraction, Object startValue, Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}
}
