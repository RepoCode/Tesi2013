package tesi.bticino.scenariodue;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
 
// Una semplice SurfaceView del quale impostiamo altezza e larghezza
public class GStreamerSurfaceView extends SurfaceView {
    public int media_width = 320;
    public int media_height = 240;
 
    // Costruttore
    public GStreamerSurfaceView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public GStreamerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public GStreamerSurfaceView (Context context) {
        super(context);
    }
 
    //Chiamato dal gestore di layout per scoprire la nostra dimensione.
    //Cercheremo di massimizzare la nostra dimensione, e mantiene le proporzioni dei media se possibile
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;
        int wmode = View.MeasureSpec.getMode(widthMeasureSpec);
        int hmode = View.MeasureSpec.getMode(heightMeasureSpec);
        int wsize = View.MeasureSpec.getSize(widthMeasureSpec);
        int hsize = View.MeasureSpec.getSize(heightMeasureSpec);
 
        Log.i ("GStreamer", "onMeasure called with " + media_width + "x" + media_height);
        // Obey width rules
        switch (wmode) {
        case View.MeasureSpec.AT_MOST:
            if (hmode == View.MeasureSpec.EXACTLY) {
                width = Math.min(hsize * media_width / media_height, wsize);
                break;
            }
        case View.MeasureSpec.EXACTLY:
            width = wsize;
            break;
        case View.MeasureSpec.UNSPECIFIED:
            width = media_width;
        }
 
        // Rispettiamo le regole di altezza
        switch (hmode) {
        case View.MeasureSpec.AT_MOST:
            if (wmode == View.MeasureSpec.EXACTLY) {
                height = Math.min(wsize * media_height / media_width, hsize);
                break;
            }
        case View.MeasureSpec.EXACTLY:
            height = hsize;
            break;
        case View.MeasureSpec.UNSPECIFIED:
            height = media_height;
        }
 
        // Calcoliamo le dimensioni migliori
        if (hmode == View.MeasureSpec.AT_MOST && wmode == View.MeasureSpec.AT_MOST) {
            int correct_height = width * media_height / media_width;
            int correct_width = height * media_width / media_height;
 
            if (correct_height < height)
                height = correct_height;
            else
                width = correct_width;
        }
 
        // Risoettiamo la dimensione minima
        width = Math.max (getSuggestedMinimumWidth(), width);
        height = Math.max (getSuggestedMinimumHeight(), height);
        setMeasuredDimension(width, height);
    }
 
}