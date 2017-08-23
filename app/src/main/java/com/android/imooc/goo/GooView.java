package com.android.imooc.goo;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * @描述 粘性控件
 * @项目名称 App_imooc
 * @包名 com.android.imooc.goo
 * @类名 GooView
 * @author chenlin
 * @date 2015年6月2日 下午8:40:53
 * @version 1.0
 */

public class GooView extends View {
	private Paint mPaint;
	private float mStaicCircleCenter = 150f;
	private float mStaicRadius = 80f;// 直径
	private float mMoveCircleCenter = 70f;
	private float mMoveRadius = 80f;// 直径

	// 最大移动距离
	private float mMaxDistance = 70f;

	// 顶部状态栏的高度
	private int mBarHeight;
	private boolean isOutRange;//是否超出范围
	private boolean isDisappear;//是否消失
	private boolean drawLine;

	public GooView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GooView(Context context) {
		this(context, null);
	}

	public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.RED);
	}

	// 存储静态的两个点
	private PointF[] mStaticPointFs = new PointF[2];
	// 存储移动的两个点
	private PointF[] mMovewPointFs = new PointF[2];
	// 控制点
	private PointF mControlPointF = new PointF();
	// 静态点
	private PointF mStaticCenter = new PointF(mStaicCircleCenter, mStaicCircleCenter);
	// 移动点
	private PointF mMovewCenter = new PointF(mMoveCircleCenter, mMoveCircleCenter);
	

	@Override
	protected void onDraw(Canvas canvas) {

		// 5.获得两个圆心之间临时的距离
		float tempStaticRadius = getTempStickRadius();

		// 1、获得偏移量
		float yOffset = mStaticCenter.y - mMovewCenter.y;
		float xOffset = mStaticCenter.x - mMovewCenter.x;
		// 2、有了偏移量就可以求出两点斜率了
		Double lineK = 0.0;
		if (xOffset != 0f) {
			lineK = (double) (yOffset / xOffset);
		}
		// 3、通过工具求得两个点的集合
		mMovewPointFs = GeometryUtil.getIntersectionPoints(mMovewCenter, mMoveRadius, lineK);
		mStaticPointFs = GeometryUtil.getIntersectionPoints(mStaticCenter, tempStaticRadius, lineK);
		// 4、通过公式求得控制点
		mControlPointF = GeometryUtil.getMiddlePoint(mStaticCenter, mMovewCenter);

		// 保存画布状态
		canvas.save();
		
		if (!isDisappear) {
			// canvas.translate(0, -mBarHeight);
			if (!isOutRange) {
				// 画连接部分
				Path path = new Path();
				// 50, 250 p2
				path.moveTo(mStaticPointFs[0].x, mStaticPointFs[0].y);
				// 150f, 300f填充物的中间点
				// 50f, 250f p1
				path.quadTo(mControlPointF.x, mControlPointF.y, mMovewPointFs[0].x, mMovewPointFs[0].y);
				// p3
				path.lineTo(mMovewPointFs[1].x, mMovewPointFs[1].y);
				// p4
				path.quadTo(mControlPointF.x, mControlPointF.y, mStaticPointFs[1].x, mStaticPointFs[1].y);
				// 关闭后，会回到最开始的地方，形成封闭的图形
				path.close();

				canvas.drawPath(path, mPaint);

				canvas.drawCircle(mStaticCenter.x, mStaticCenter.y, tempStaticRadius, mPaint);
			}else{
				// 画连接部分
				Path path = new Path();
				// 50, 250 p2
				path.moveTo(mStaticPointFs[0].x, mStaticPointFs[0].y);
				// 150f, 300f填充物的中间点
				// 50f, 250f p1
				path.lineTo( mMovewPointFs[0].x, mMovewPointFs[0].y);
				// p3
				path.lineTo(mMovewPointFs[1].x, mMovewPointFs[1].y);
				// p4
				path.lineTo(mStaticPointFs[1].x, mStaticPointFs[1].y);
				// 关闭后，会回到最开始的地方，形成封闭的图形
				path.close();

				canvas.drawPath(path, mPaint);

				canvas.drawCircle(mStaticCenter.x, mStaticCenter.y, tempStaticRadius, mPaint);

			}

			// 画移动的大圆
			canvas.drawCircle(mMovewCenter.x, mMovewCenter.y, mMoveRadius, mPaint);
		}
		

		// 恢复上次的保存状态
		canvas.restore();
	}

	// 获取固定圆半径(根据两圆圆心距离)
	private float getTempStickRadius() {
		float distance = GeometryUtil.getDistanceBetween2Points(mMovewCenter, mStaticCenter);

		// if(distance> farestDistance){
		// distance = farestDistance;
		// }
		distance = Math.min(distance, mMaxDistance);

		// 0.0f -> 1.0f
		float percent = distance / mMaxDistance;

		// percent , 100% -> 20%
		return ValueUtil.evalute(percent, mStaicRadius, mStaicRadius * 0.2f);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBarHeight = Utils.getStatusBarHeight(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float downX = 0.0f;
		float downY = 0.0f;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isOutRange = false;
			isDisappear = false;
			// 得到按下的坐标
			// downX = event.getRawX();
			// downY = event.getRawY();
			downX = event.getX();
			downY = event.getY();
			// 更新移动的坐标
			updateMoveCenter(downX, downY);
			mStaticCenter.set(downX,downY);




			break;
		case MotionEvent.ACTION_MOVE:
			// 得到按下的坐标
			downX = event.getX();
			downY = event.getY();
			// 更新移动的坐标
			updateMoveCenter(downX, downY);

			// 当超过最大值时断开
			float distance = GeometryUtil.getDistanceBetween2Points(mMovewCenter, mStaticCenter);
			if (distance > mMaxDistance) {
				isOutRange = true;
				invalidate();
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (isOutRange) {
				//1）拖拽超出范围时，断开了，此时我们松手，图标消失
				// 当超过最大值时断开
			/*	distance = GeometryUtil.getDistanceBetween2Points(mMovewCenter, mStaticCenter);
				if (distance > mMaxDistance) {
					isDisappear = true;
					invalidate();
				}else {
					//2）拖拽超出范围时，断开了，此时我们把图标移动回去，图标恢复原样
					//就是把移动的圆圈设置到原来的静态圆圈里
					updateMoveCenter(mStaticCenter.x, mStaticCenter.y);
				}*/

				distance = GeometryUtil.getDistanceBetween2Points(mMovewCenter, mStaticCenter);
				if (distance > mMaxDistance) {
					drawLine = true;
				}else{
					updateMoveCenter(mStaticCenter.x, mStaticCenter.y);
					drawLine = false;
				}






				
			}else {
				//3）拖拽没有超出范围时，此时我们松手，图标弹回去
				
				//得到固定的点
				final PointF tempMovePointF = new PointF(mMovewCenter.x, mMovewCenter.y);
				ValueAnimator vAnim = ValueAnimator.ofFloat(1.0f);
				vAnim.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float percent = animation.getAnimatedFraction();
						//得到两点的
						PointF pointF = GeometryUtil.getPointByPercent(tempMovePointF, mStaticCenter, percent);
						updateMoveCenter(pointF.x, pointF.y);
					}
				});
				vAnim.setInterpolator(new OvershootInterpolator(4));
				vAnim.setDuration(500);
				vAnim.start();
			}
			
			break;

		}
		return true;
	}


	/**
	 * 更新移动的点
	 * 
	 * @param downX
	 * @param downY
	 */
	public void updateMoveCenter(float downX, float downY) {
		mMovewCenter.set(downX, downY);
		invalidate();
	}

}
