package com.cruzer.cursor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class CursorOverlayService extends Service {
    private WindowManager windowManager;
    private ImageView cursorView;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        cursorView = new ImageView(this);
        cursorView.setImageResource(R.drawable.cursor_icon); // Ensure cursor_icon.png exists in res/drawable

        params = new WindowManager.LayoutParams(
            50, 50, // Cursor size (adjust as needed)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 500; // Initial X position
        params.y = 500; // Initial Y position

        windowManager.addView(cursorView, params);

        // Detect mouse movement
        cursorView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
				@Override
				public boolean onGenericMotion(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
						updateCursorPosition(event.getRawX(), event.getRawY());
						return true;
					}
					return false;
				}
			});
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursorView != null) {
            windowManager.removeView(cursorView);
        }
    }

    // Move cursor like a real mouse
    private void updateCursorPosition(float x, float y) {
        params.x = (int) x;
        params.y = (int) y;
        windowManager.updateViewLayout(cursorView, params);
    }
}
