/*
 * Copyright 2019 Blake Zheng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blakezheng.widget.cordiformview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by BlakeZheng on 2019/5/31.
 */
@SuppressWarnings("UnusedDeclaration")
public class CordiformView extends View {
    private final static String TAG = "CardiProgressBar";
    private final static float SQRT_TWO = (float) Math.sqrt(2);
    private final static float DRAWABLE_RATIO = (float) ((4 + 2 * SQRT_TWO)/ (2 + 3 * SQRT_TWO));
    private final static float TEXT_HEIGHT_SCALE = 1.171875f;
    private final static int MAX_PROGRESS = 101;
    private final static int DEFAULT_SHADOW_COLOR = Color.GRAY;
    private final static int DEFAULT_INNER_TEXT_COLOR = Color.BLACK;

    private boolean ready = false;

    private int leftArcColor;
    private int rightArcColor;
    private int bottomLineColor;

    private int leftProgress;
    private int rightProgress;
    private int bottomProgress;

    private int strokeWidth;
    private int actualStrokeWidth;

    private int shadowColor;
    private int shadowRadius;
    private int shadowDx = 0;
    private int shadowDy = 0;

    private int sideLength = -1;
    private int drawableHeight;
    private int drawableWidth;

    private int leftLineStartX;
    private int leftLineStartY;
    private int leftLineEndX;
    private int leftLineEndY;

    private int rightLineStartX;
    private int rightLineStartY;
    private int rightLineEndX;
    private int rightLineEndY;

    private int lineLength;
    private int maxProgressToRightLine;
    private int maxProgressToBottomArc;

    private int xOffset;
    private int yOffset;

    private int bottomArcRadius = 70;
    private int bottomArcCx;
    private int bottomArcCy;

    private String leftInnerText;
    private String rightInnerText;
    private String bottomInnerText;
    private String leftOuterText;
    private String rightOuterText;
    private String bottomOuterText;

    private int innerTextColor;
    private float innerTextSize;
    private float outerTextSize;
    private int outerTextOffset;

    private float outerTextOffsetWhileDrawing;

    private RectF leftArcRect;
    private RectF rightArcRect;
    private RectF bottomRect;

    private Paint leftArcPaint;
    private Paint leftArcBgPaint;
    private Paint rightArcPaint;
    private Paint rightArcBgPaint;
    private Paint linePaint;
    private Paint bottomArcPaint;
    private Paint bottomArcBgPaint;
    private Paint lineBgPaint;
    private Paint coverPaint;

    private Paint shadowPaint;
    private Path shadowPath;

    private Path leftOuterTextPath;
    private Path rightOuterTextPath;
    private Path bottomOuterTextPath;
    private Paint outerTextPaint;

    private Path leftInnerTextPath;
    private Path rightInnerTextPath;
    private Path bottomInnerTextPath;
    private Paint innerTextPaint;

    public CordiformView(Context context) {
        super(context);

        leftArcColor = ColorUtils.getColor(context, R.color.default_left_arc_color);
        rightArcColor = ColorUtils.getColor(context, R.color.default_right_arc_color);
        bottomLineColor = ColorUtils.getColor(context, R.color.default_bottom_line_color);
        shadowColor = DEFAULT_SHADOW_COLOR;
        innerTextColor = DEFAULT_INNER_TEXT_COLOR;

        init();
    }

    public CordiformView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CordiformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CordiformView, defStyleAttr, 0);

        leftArcColor = a.getColor(R.styleable.CordiformView_leftArcColor, ColorUtils.getColor(context, R.color.default_left_arc_color));
        rightArcColor = a.getColor(R.styleable.CordiformView_rightArcColor, ColorUtils.getColor(context, R.color.default_right_arc_color));
        bottomLineColor = a.getColor(R.styleable.CordiformView_bottomLineColor, ColorUtils.getColor(context, R.color.default_bottom_line_color));
        leftProgress = a.getInt(R.styleable.CordiformView_leftProgress, 0) % MAX_PROGRESS;
        rightProgress = a.getInt(R.styleable.CordiformView_rightProgress, 0) % MAX_PROGRESS;
        bottomProgress = a.getInt(R.styleable.CordiformView_bottomProgress, 0) % MAX_PROGRESS;
        strokeWidth = a.getDimensionPixelSize(R.styleable.CordiformView_strokeWidth, 0);
        shadowColor = a.getColor(R.styleable.CordiformView_shadowColor, DEFAULT_SHADOW_COLOR);
        shadowRadius = a.getDimensionPixelSize(R.styleable.CordiformView_shadowRadius, 0);
        shadowDx = a.getDimensionPixelSize(R.styleable.CordiformView_shadowDx, 0);
        shadowDy = a.getDimensionPixelSize(R.styleable.CordiformView_shadowDy, 0);

        leftInnerText = getString(a, R.styleable.CordiformView_leftInnerText);
        rightInnerText = getString(a, R.styleable.CordiformView_rightInnerText);
        bottomInnerText = getString(a, R.styleable.CordiformView_bottomInnerText);
        leftOuterText = getString(a, R.styleable.CordiformView_leftOuterText);
        rightOuterText = getString(a, R.styleable.CordiformView_rightOuterText);
        bottomOuterText = getString(a, R.styleable.CordiformView_bottomOuterText);

        innerTextSize = a.getDimensionPixelSize(R.styleable.CordiformView_innerTextSize, 0);
        outerTextSize = a.getDimensionPixelSize(R.styleable.CordiformView_outerTextSize, 0);
        outerTextOffset = a.getDimensionPixelSize(R.styleable.CordiformView_outerTextOffset, 0);

        innerTextColor = a.getColor(R.styleable.CordiformView_innerTextColor, DEFAULT_INNER_TEXT_COLOR);

        a.recycle();
        init();
    }

    private void init(){

        ready = true;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        actualStrokeWidth = strokeWidth;

        leftArcRect = new RectF();
        rightArcRect = new RectF();
        leftArcPaint = new Paint();
        leftArcPaint.setAntiAlias(true);
        leftArcPaint.setDither(true);
        leftArcPaint.setStyle(Paint.Style.STROKE);

        leftArcPaint.setStrokeCap(Paint.Cap.ROUND);
        leftArcPaint.setColor(leftArcColor);
        leftArcBgPaint = new Paint(leftArcPaint);
        leftArcBgPaint.setColor(parseToDarkColor(leftArcColor));

        rightArcPaint = new Paint();
        rightArcPaint.set(leftArcPaint);
        rightArcPaint.setColor(rightArcColor);
        rightArcBgPaint = new Paint(rightArcPaint);
        rightArcBgPaint.setColor(parseToDarkColor(rightArcColor));

        linePaint = new Paint();
        linePaint.set(rightArcPaint);
        linePaint.setColor(bottomLineColor);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        lineBgPaint = new Paint(linePaint);
        lineBgPaint.setColor(parseToDarkColor(bottomLineColor));
        bottomArcPaint = new Paint(linePaint);
        bottomArcPaint.setAntiAlias(false);
        bottomArcBgPaint = new Paint(lineBgPaint);
        bottomArcBgPaint.setAntiAlias(false);

        shadowPath = new Path();
        bottomRect = new RectF();

        shadowPaint = new Paint();
        shadowPaint.setStrokeJoin(Paint.Join.ROUND);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setColor(Color.GRAY);
        shadowPaint.setAntiAlias(true);
        if(shadowRadius > 0){
            shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
        }

        coverPaint = new Paint();
        coverPaint.setColor(bottomLineColor);
        coverPaint.setStyle(Paint.Style.FILL);

        outerTextPaint = new TextPaint();
        outerTextPaint.setAntiAlias(true);
        outerTextPaint.setColor(Color.BLACK);
        outerTextPaint.setTextAlign(Paint.Align.CENTER);
        outerTextPaint.setTextSize(outerTextSize);
        if(outerTextSize <= 0
                && (!TextUtils.isEmpty(leftOuterText)
                || !TextUtils.isEmpty(rightOuterText)
                || !TextUtils.isEmpty(bottomOuterText))){
            outerTextSize = getContext().getResources().getDisplayMetrics().density * 10;
        }
        outerTextPaint.setTextSize(outerTextSize);
        innerTextPaint = new TextPaint();
        innerTextPaint.setColor(innerTextColor);
        innerTextPaint.setAntiAlias(true);
        innerTextPaint.setTextAlign(Paint.Align.RIGHT);

        leftInnerTextPath = new Path();
        rightInnerTextPath = new Path();
        bottomInnerTextPath = new Path();
        leftOuterTextPath = new Path();
        rightOuterTextPath = new Path();
        bottomOuterTextPath = new Path();

        if(!TextUtils.isEmpty(leftOuterText) || !TextUtils.isEmpty(rightOuterText) || !TextUtils.isEmpty(bottomInnerText)){
            outerTextOffsetWhileDrawing = outerTextSize * TEXT_HEIGHT_SCALE + outerTextOffset;
        }
        doMath();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        doMath();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);


        int actualPaddingHorizontal = calActualPaddingHorizontal();
        int actualPaddingVertical = calActualPaddingVertical();

        if(widthSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.AT_MOST){
            if(widthSpecSize > heightSpecSize){
                widthSpecSize = (int) ((heightSpecSize - actualPaddingVertical) * DRAWABLE_RATIO + actualPaddingHorizontal);
            }else{
                heightSpecSize = (int) ((widthSpecSize - actualPaddingHorizontal) / DRAWABLE_RATIO + actualPaddingVertical);
            }

            setMeasuredDimension(widthSpecSize, heightSpecSize);

        }
    }

    private void setupStrokeWidth(){
        if(sideLength < 0){
            return;
        }

        if(actualStrokeWidth > sideLength / 3){
            actualStrokeWidth = sideLength / 3;
        }else if(actualStrokeWidth <= 0){
            actualStrokeWidth = sideLength / 6;
        }

        leftArcPaint.setStrokeWidth(actualStrokeWidth);
        leftArcBgPaint.setStrokeWidth(actualStrokeWidth);
        rightArcPaint.setStrokeWidth(actualStrokeWidth);
        rightArcBgPaint.setStrokeWidth(actualStrokeWidth);
        linePaint.setStrokeWidth(actualStrokeWidth);
        lineBgPaint.setStrokeWidth(actualStrokeWidth);
        bottomArcPaint.setStrokeWidth(actualStrokeWidth);
        bottomArcBgPaint.setStrokeWidth(actualStrokeWidth);
        shadowPaint.setStrokeWidth(actualStrokeWidth);
    }

    private void doMath(){
        if(!ready){
            return;
        }

        if(getHeight() == 0 && getWidth() == 0){
            Log.d(TAG, "no size yet");
            return;
        }

        actualStrokeWidth = strokeWidth;
        xOffset = strokeWidth / 2 + getPaddingStart() + (int)Math.max(shadowRadius - shadowDx, (outerTextSize * TEXT_HEIGHT_SCALE) + outerTextOffset);
        yOffset = strokeWidth / 2 + getPaddingTop() + (int)Math.max(shadowRadius - shadowDy, (outerTextSize * TEXT_HEIGHT_SCALE) + outerTextOffset) ;
        float ratio = (float) getWidthWithoutPadding() / getHeightWithoutPadding();
        if(ratio > DRAWABLE_RATIO){
            drawableHeight = getHeightWithoutPadding();
            drawableWidth = (int) (drawableHeight * DRAWABLE_RATIO);
            xOffset += (getWidthWithoutPadding() - drawableWidth) / 2;
        }else{
            drawableWidth = getWidthWithoutPadding();
            drawableHeight =(int) (drawableWidth / DRAWABLE_RATIO);
            yOffset += (getHeightWithoutPadding() - drawableHeight) / 2;
        }

        if(drawableWidth <= 0 || drawableHeight <=0){
            return;
        }

        sideLength = (int) (2 * drawableWidth / (2 + SQRT_TWO));
        setupStrokeWidth();

        leftArcRect.set(xOffset, yOffset, sideLength + xOffset, sideLength + yOffset);
        rightArcRect.set(drawableWidth - sideLength + xOffset, yOffset, drawableWidth + xOffset, sideLength + yOffset);

        bottomArcRadius = (int) (0.25 * sideLength);
        lineLength = (int) (2 * (sideLength - bottomArcRadius) + Math.PI * bottomArcRadius / 2);
        maxProgressToRightLine = (sideLength - bottomArcRadius) * 100 / lineLength;
        maxProgressToBottomArc = 100 - (sideLength - bottomArcRadius) * 100 / lineLength;

        leftLineEndX = (int) (( 2 - SQRT_TWO) * sideLength / 4) + xOffset;
        leftLineEndY = (int) (( 2 + SQRT_TWO) * sideLength / 4) + yOffset;
        rightLineStartX = drawableWidth - leftLineEndX + 2*xOffset;
        rightLineStartY = leftLineEndY;
        leftLineStartX = rightLineEndX = xOffset + drawableWidth / 2;
        leftLineStartY = rightLineEndY = yOffset + drawableHeight;

        int offset = (int) (bottomArcRadius/SQRT_TWO);
        rightLineEndX  += offset;
        rightLineEndY -= offset;
        leftLineStartX -= offset;
        leftLineStartY -= offset;

        bottomArcCx = xOffset + drawableWidth / 2;
        bottomArcCy = yOffset + (int) (drawableHeight - SQRT_TWO * bottomArcRadius);
        bottomRect.set(bottomArcCx  - bottomArcRadius, bottomArcCy - bottomArcRadius,
                bottomArcCx + bottomArcRadius, bottomArcCy + bottomArcRadius);

        shadowPath.reset();
        shadowPath.moveTo(rightLineStartX, rightLineStartY);
        shadowPath.lineTo(rightLineEndX, rightLineEndY);
        shadowPath.arcTo(bottomRect, 45, 90);
        shadowPath.lineTo(leftLineEndX, leftLineEndY);
        shadowPath.arcTo(leftArcRect, 135, 180);
        shadowPath.arcTo(rightArcRect, -135, 180);

        leftInnerTextPath.reset();
        leftInnerTextPath.moveTo(leftLineEndX, leftLineEndY);
        leftInnerTextPath.arcTo(leftArcRect, 135, 180);

        rightInnerTextPath.reset();
//        rightInnerTextPath.moveTo(drawableWidth / 2 + xOffset, drawableHeight - SQRT_TWO * sideLength + yOffset);
        rightInnerTextPath.moveTo(rightLineStartX, rightLineStartY);
        rightInnerTextPath.arcTo(rightArcRect, 45, -180);

        bottomInnerTextPath.reset();
        bottomInnerTextPath.moveTo(leftLineEndX, leftLineEndY);
        bottomInnerTextPath.lineTo(leftLineStartX, leftLineStartY);
        bottomInnerTextPath.arcTo(bottomRect, 135, -90);
        bottomInnerTextPath.lineTo(rightLineStartX, rightLineStartY);

        leftOuterTextPath.reset();
        leftOuterTextPath.moveTo(leftLineEndX - (actualStrokeWidth + outerTextOffsetWhileDrawing)/2 ,
                leftLineEndY - (actualStrokeWidth + outerTextOffsetWhileDrawing)/2);
        RectF leftTextRect = new RectF(leftArcRect);
        leftTextRect.inset(-(actualStrokeWidth + outerTextOffsetWhileDrawing)/2, -(actualStrokeWidth + outerTextOffsetWhileDrawing)/2);
        leftOuterTextPath.arcTo(leftTextRect, 135, 180);

        rightOuterTextPath.reset();
        rightOuterTextPath.moveTo(drawableWidth / 2 - (actualStrokeWidth + outerTextOffsetWhileDrawing)/2  + xOffset,
                drawableHeight - SQRT_TWO * sideLength  - (actualStrokeWidth + outerTextOffsetWhileDrawing)/2 + yOffset);
        RectF rightTextRect = new RectF(rightArcRect);
        rightTextRect.inset(-(actualStrokeWidth + outerTextOffsetWhileDrawing)/2, -(actualStrokeWidth + outerTextOffsetWhileDrawing)/2);
        rightOuterTextPath.arcTo(rightTextRect, -135, 180);

        float halfLengthDiagonalLine = SQRT_TWO * sideLength / 2;
        int xMidPointOfRound = (int) (sideLength / 2 + SQRT_TWO * sideLength / 4 + xOffset);
        int yMidPointOfRound = (int) (sideLength / 2 + SQRT_TWO * sideLength / 4 + yOffset);
        bottomOuterTextPath.reset();
        bottomOuterTextPath.moveTo(xMidPointOfRound - halfLengthDiagonalLine - actualStrokeWidth /2, yMidPointOfRound);
        RectF bottomTextArcRect = new RectF();
        bottomTextArcRect.set(xMidPointOfRound - halfLengthDiagonalLine - actualStrokeWidth /2, yMidPointOfRound - halfLengthDiagonalLine - actualStrokeWidth /2,
                xMidPointOfRound + halfLengthDiagonalLine + actualStrokeWidth /2, yMidPointOfRound + halfLengthDiagonalLine + actualStrokeWidth /2);
        bottomOuterTextPath.arcTo(bottomTextArcRect, 180, -180, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        if(drawableWidth <= 0 || drawableHeight <=0){
            return;
        }
        //draw shadow
        canvas.drawPath(shadowPath, shadowPaint);

        //draw right part of the bottom line
        canvas.drawLine(rightLineStartX, rightLineStartY, rightLineEndX, rightLineEndY, lineBgPaint);
        float rightLineProgress = Math.min(maxProgressToRightLine, bottomProgress);
        float rightLineRatio = rightLineProgress  / maxProgressToRightLine;
        canvas.drawLine(rightLineStartX, rightLineStartY,
                rightLineStartX + ((rightLineEndX - rightLineStartX) * rightLineRatio),
                rightLineStartY + ((rightLineEndY - rightLineStartY) * rightLineRatio), linePaint);


        //draw right arc background
        canvas.drawArc(rightArcRect, -135, 180, false, rightArcBgPaint);
        //draw right arc progress
        canvas.drawArc(rightArcRect, -135, 180 * ((float) rightProgress / 100), false, rightArcPaint);

        //draw left arc background
        canvas.drawArc(leftArcRect, 135, 180, false, leftArcBgPaint);
        //draw left arc progress
        canvas.drawArc(leftArcRect, 135, 180 * ((float) leftProgress / 100), false, leftArcPaint);


        //draw left part of the bottom line
        canvas.drawLine(leftLineStartX, leftLineStartY, leftLineEndX, leftLineEndY, lineBgPaint);
        float leftLineProgress = bottomProgress - maxProgressToBottomArc;
        if(leftLineProgress > 0){
            float leftLineRatio = leftLineProgress / (100 - maxProgressToBottomArc);
            canvas.drawLine(leftLineStartX, leftLineStartY,
                    leftLineStartX + ((leftLineEndX - leftLineStartX) * leftLineRatio),
                    leftLineStartY + ((leftLineEndY - leftLineStartY) * leftLineRatio), linePaint);
        }

        //draw round corner of the bottom line
        canvas.drawArc(bottomRect, 45, 90, false, bottomArcBgPaint);
        float bottomArcProgress = Math.min(bottomProgress - maxProgressToRightLine, maxProgressToBottomArc - maxProgressToRightLine) + 0.5f;
        if(bottomArcProgress >= 0){
            float bottomArcRatio = bottomArcProgress / (maxProgressToBottomArc - maxProgressToRightLine);
            canvas.drawArc(bottomRect, 45, 90 * bottomArcRatio, false, bottomArcPaint);
        }

        //!This is the trick to make bottom lines seem to be one
        if(rightLineProgress  > 0.5 * maxProgressToRightLine){
            canvas.drawCircle(rightLineStartX + ((rightLineEndX - rightLineStartX) * rightLineRatio),
                    rightLineStartY + ((rightLineEndY - rightLineStartY) * rightLineRatio), actualStrokeWidth /2, coverPaint);
        }

        //height of innerText must smaller than stroke width
        float actualInnerTextSize = innerTextSize;
        if(innerTextSize > actualStrokeWidth / TEXT_HEIGHT_SCALE){
            innerTextSize = actualStrokeWidth / TEXT_HEIGHT_SCALE;
        }else if(innerTextSize <= 0){
            innerTextSize = actualStrokeWidth / TEXT_HEIGHT_SCALE / 1.5f;
        }
        innerTextPaint.setTextSize(innerTextSize);

        Paint.FontMetrics fontMetrics = innerTextPaint.getFontMetrics();
        float innerTextVOffset=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;

        innerTextPaint.setTextAlign(Paint.Align.RIGHT);
        if(!TextUtils.isEmpty(leftInnerText)){
            canvas.drawTextOnPath(leftInnerText, leftInnerTextPath, 0, innerTextVOffset, innerTextPaint);
        }

        //for text to be more readable
        innerTextPaint.setTextAlign(Paint.Align.LEFT);
        if(!TextUtils.isEmpty(rightInnerText)){
            canvas.drawTextOnPath(rightInnerText, rightInnerTextPath, 1, innerTextVOffset, innerTextPaint);
        }

        if(!TextUtils.isEmpty(bottomInnerText)){
            canvas.drawTextOnPath(bottomInnerText, bottomInnerTextPath, 0, innerTextVOffset, innerTextPaint);
        }

        fontMetrics = outerTextPaint.getFontMetrics();
        float outerTextVOffset=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        if(!TextUtils.isEmpty(bottomOuterText)){
            outerTextPaint.setColor(bottomLineColor);
            canvas.drawTextOnPath(bottomOuterText, bottomOuterTextPath, 0, outerTextVOffset, outerTextPaint);
        }

        if(!TextUtils.isEmpty(rightOuterText)){
            outerTextPaint.setColor(rightArcColor);
            canvas.drawTextOnPath(rightOuterText, rightOuterTextPath, 0, outerTextVOffset, outerTextPaint);
        }

        if(!TextUtils.isEmpty(leftOuterText)){
            outerTextPaint.setColor(leftArcColor);
            canvas.drawTextOnPath(leftOuterText, leftOuterTextPath, 0, outerTextVOffset, outerTextPaint);
        }

    }


    private int getWidthWithoutPadding(){
        int widthWithoutPadding = getWidth() - calActualPaddingHorizontal();
        return widthWithoutPadding >= 0 ? widthWithoutPadding : 0;
    }

    private int getHeightWithoutPadding(){
        int heightWithoutPadding = getHeight() - calActualPaddingVertical();
        return heightWithoutPadding >= 0 ? heightWithoutPadding : 0;
    }

    public int getActualStrokeWidth(){
        return actualStrokeWidth;
    }

    public void setProgress(int left, int right, int bottom){
        this.leftProgress = left % MAX_PROGRESS;
        this.rightProgress = right % MAX_PROGRESS;
        this.bottomProgress = bottom % MAX_PROGRESS;
        invalidate();
    }

    public int getLeftArcColor() {
        return leftArcColor;
    }

    public void setLeftArcColor(int leftArcColor) {
        this.leftArcColor = leftArcColor;
        leftArcPaint.setColor(leftArcColor);
        leftArcBgPaint.setColor(parseToDarkColor(leftArcColor));
        invalidate();
    }

    public int getRightArcColor() {
        return rightArcColor;
    }

    public void setRightArcColor(int rightArcColor) {
        this.rightArcColor = rightArcColor;
        rightArcPaint.setColor(leftArcColor);
        rightArcBgPaint.setColor(parseToDarkColor(leftArcColor));
        invalidate();
    }

    public int getBottomLineColor() {
        return bottomLineColor;
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        linePaint.setColor(bottomLineColor);
        lineBgPaint.setColor(parseToDarkColor(bottomLineColor));
        bottomArcPaint.setColor(bottomLineColor);
        bottomArcBgPaint.setColor(parseToDarkColor(bottomLineColor));
        coverPaint.setColor(bottomLineColor);
        invalidate();
    }

    public int getLeftProgress() {
        return leftProgress;
    }

    public void setLeftProgress(int leftProgress) {
        this.leftProgress = leftProgress % MAX_PROGRESS;
        invalidate();
    }

    public int getRightProgress() {
        return rightProgress;
    }

    public void setRightProgress(int rightProgress) {
        this.rightProgress = rightProgress % MAX_PROGRESS;
        invalidate();
    }

    public int getBottomProgress() {
        return bottomProgress;
    }

    public void setBottomProgress(int bottomProgress) {
        this.bottomProgress = bottomProgress % MAX_PROGRESS;
        invalidate();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        actualStrokeWidth = this.strokeWidth = strokeWidth;
        setupStrokeWidth();
        invalidate();
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        if(shadowRadius > 0){
            shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
        }
        invalidate();
    }

    public int getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        if(this.shadowRadius != shadowRadius){
            this.shadowRadius = shadowRadius;
            if(shadowRadius > 0){
                shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
                doMath();
                invalidate();
            }
        }
    }

    public int getShadowDx() {
        return shadowDx;
    }

    public void setShadowDx(int shadowDx) {
        if(this.shadowDx != shadowDx){
            this.shadowDx = shadowDx;
            if(shadowRadius > 0){
                shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
                doMath();
                invalidate();
            }
        }
    }

    public int getShadowDy() {
        return shadowDy;
    }

    public void setShadowDy(int shadowDy) {
        if(this.shadowDy != shadowDy){
            this.shadowDy = shadowDy;
            if(shadowRadius > 0){
                shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
                doMath();
                invalidate();
            }
        }
    }

    public String getLeftInnerText() {
        return leftInnerText;
    }

    public void setLeftInnerText(String leftInnerText) {
        this.leftInnerText = leftInnerText;
        invalidate();
    }

    public void setLeftInnerText(int leftInnerTextRes) {
        this.leftInnerText = getResources().getString(leftInnerTextRes);
        invalidate();
    }

    public String getRightInnerText() {
        return rightInnerText;
    }

    public void setRightInnerText(String rightInnerText) {
        this.rightInnerText = rightInnerText;
        invalidate();
    }

    public void setRightInnerText(int rightInnerTextRes) {
        this.rightInnerText = getResources().getString(rightInnerTextRes);
        invalidate();
    }

    public String getBottomInnerText() {
        return bottomInnerText;
    }

    public void setBottomInnerText(String bottomInnerText) {
        this.bottomInnerText = bottomInnerText;
        invalidate();
    }

    public void setBottomInnerText(int bottomInnerTextRes) {
        this.bottomInnerText = getResources().getString(bottomInnerTextRes);
        invalidate();
    }

    public String getLeftOuterText() {
        return leftOuterText;
    }


    public void setLeftOuterText(String leftOuterText) {
        setLeftOuterText(leftOuterText, false);
    }

    public void setLeftOuterText(int leftOuterTextRes) {
        setLeftOuterText(leftOuterTextRes, false);
    }

    public void setLeftOuterText(int leftOuterTextRes, boolean relayout) {
        this.leftOuterText = getResources().getString(leftOuterTextRes);
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public void setLeftOuterText(String leftOuterText, boolean relayout) {
        this.leftOuterText = leftOuterText;
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public String getRightOuterText() {
        return rightOuterText;
    }

    public void setRightOuterText(String rightOuterText) {
        setRightOuterText(rightOuterText, false);
    }

    public void setRightOuterText(String rightOuterText, boolean relayout) {
        this.rightOuterText = rightOuterText;
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public void setRightOuterText(int rightOuterTextRes) {
        setRightOuterText(rightOuterTextRes, false);
    }

    public void setRightOuterText(int rightOuterTextRes, boolean relayout) {
        this.rightOuterText = getResources().getString(rightOuterTextRes);
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public String getBottomOuterText() {
        return bottomOuterText;
    }

    public void setBottomOuterText(String bottomOuterText) {
        setBottomOuterText(bottomOuterText, false);
    }

    public void setBottomOuterText(String bottomOuterText, boolean relayout) {
        this.bottomOuterText = bottomOuterText;
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public void setBottomOuterText(int bottomOuterTextRes) {
        setBottomOuterText(bottomOuterTextRes, false);
    }

    public void setBottomOuterText(int bottomOuterTextRes, boolean relayout) {
        this.bottomOuterText = getResources().getString(bottomOuterTextRes);
        if(relayout){
            requestLayout();
        }else{
            invalidate();
        }
    }

    public float getInnerTextSize() {
        return innerTextSize;
    }

    public void setInnerTextSize(float innerTextSize) {
        if(this.innerTextSize != innerTextSize){
            this.innerTextSize = innerTextSize;
            doMath();
            invalidate();
        }
    }

    public float getOuterTextSize() {
        return outerTextSize;
    }


    public void setOuterTextSize(float outerTextSize) {
        if(this.outerTextSize != outerTextSize){
            this.outerTextSize = outerTextSize;
            outerTextPaint.setTextSize(outerTextSize);
            requestLayout();
        }
    }

    public int getOuterTextOffset() {
        return outerTextOffset;
    }

    public void setOuterTextOffset(int outerTextOffset) {
        if(this.outerTextOffset != outerTextOffset){
            this.outerTextOffset = outerTextOffset;
            doMath();
            invalidate();
        }
    }

    public int getInnerTextColor() {
        return innerTextColor;
    }

    public void setInnerTextColor(int innerTextColor) {
        this.innerTextColor = innerTextColor;
        innerTextPaint.setColor(innerTextColor);
        invalidate();
    }

    private int parseToDarkColor(int color){
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(Color.alpha(color), (int)(r * 0.3), (int)(g * 0.3), (int)(b * 0.3));
    }

    private String getString(TypedArray a, int attr){
        int res = a.getResourceId(attr, 0);
        if(res != 0){
            return getResources().getString(res);
        }else{
            return a.getString(attr);
        }
    }

    private int calActualPaddingVertical(){
        return getPaddingTop() + getPaddingBottom()  + strokeWidth + 2 * (int)Math.max((float)shadowRadius, (outerTextSize * TEXT_HEIGHT_SCALE) + outerTextOffset);
    }

    private int calActualPaddingHorizontal(){
        return getPaddingStart() + getPaddingEnd() + strokeWidth + 2 * (int)Math.max((float) shadowRadius, (outerTextSize * TEXT_HEIGHT_SCALE) + outerTextOffset);
    }

}
