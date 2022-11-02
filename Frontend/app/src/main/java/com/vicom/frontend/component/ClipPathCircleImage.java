package com.vicom.frontend.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ClipPathCircleImage extends ImageView {
    private int width;
    private int height;
    private Path path;
    public ClipPathCircleImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        path = new Path();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        path.reset();
        path.addCircle(width/2, height/2, width/2, Path.Direction.CCW);//CCW:逆时针,这里是一个简单的园,无影响
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
        //使用Path时，如果不与Paint进行共同操作，无法解决抗锯齿问题。
        //这时候只能使用Paint的PorterDuff.Mode替代Path实现所需要的效果
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    public ClipPathCircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public ClipPathCircleImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ClipPathCircleImage(Context context) {
        this(context, null);
    }
}