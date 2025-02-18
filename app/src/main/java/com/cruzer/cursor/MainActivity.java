package com.cruzer.cursor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.InputDevice;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;

    Button checkMouseBtn, startCursorBtn, stopCursorBtn;
    TextView mouseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Ensure your layout file is correct

        checkMouseBtn = findViewById(R.id.checkMouseBtn);
        startCursorBtn = findViewById(R.id.startCursorBtn);
        stopCursorBtn = findViewById(R.id.stopCursorBtn);
        mouseStatus = findViewById(R.id.mouseStatus);

        // Check Mouse Connection Button
        checkMouseBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isMouseConnected()) {
						mouseStatus.setText("Mouse Status: Connected");
					} else {
						mouseStatus.setText("Mouse Status: Not Connected");
					}
				}
			});

        // Check and Request SYSTEM_ALERT_WINDOW permission
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        }

        // Start Cursor Overlay Button
        startCursorBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Settings.canDrawOverlays(MainActivity.this)) {
						startCursorOverlay();
					} else {
						Toast.makeText(MainActivity.this, "Overlay permission not granted!", Toast.LENGTH_SHORT).show();
						requestOverlayPermission();
					}
				}
			});

        // Stop Cursor Overlay Button
        stopCursorBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					stopCursorOverlay();
				}
			});
    }

    private boolean isMouseConnected() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int id : deviceIds) {
            InputDevice device = InputDevice.getDevice(id);
            if (device != null && (device.getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
                return true;
            }
        }
        return false;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
								   Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Overlay permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startCursorOverlay() {
        Intent intent = new Intent(MainActivity.this, CursorOverlayService.class);
        startService(intent);
    }

    public void stopCursorOverlay() {
        Intent intent = new Intent(MainActivity.this, CursorOverlayService.class);
        stopService(intent);
    }
}
