
package com.mouseaxisfix;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.PointerIcon;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends Activity {

    private TextView status, xValue, yValue, rawValue;
    private float totalX = 0, totalY = 0;
    private boolean captured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 36, 28, 28);
        root.setBackgroundColor(Color.rgb(17, 24, 39));

        TextView title = text("MOUSE AXIS FIX", 26);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        status = text("Mouse: waiting...", 16);
        status.setTextColor(Color.LTGRAY);
        root.addView(status);

        xValue = text("X: 0.00", 24);
        xValue.setTextColor(Color.rgb(80, 200, 255));
        root.addView(xValue);

        yValue = text("Y: 0.00", 24);
        yValue.setTextColor(Color.rgb(255, 190, 80));
        root.addView(yValue);

        rawValue = text("Move your mouse inside this area.", 14);
        rawValue.setTextColor(Color.LTGRAY);
        root.addView(rawValue);

        Button capture = new Button(this);
        capture.setText("CAPTURE MOUSE");
        root.addView(capture);

        Button reset = new Button(this);
        reset.setText("RESET VALUES");
        root.addView(reset);

        View mouseArea = new View(this);
        mouseArea.setBackgroundColor(Color.rgb(31, 41, 55));

        LinearLayout.LayoutParams areaParams =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
            );
        areaParams.topMargin = 24;
        root.addView(mouseArea, areaParams);

        setContentView(root);

        capture.setOnClickListener(v -> {
            mouseArea.requestFocus();
            mouseArea.requestPointerCapture();
        });

        reset.setOnClickListener(v -> {
            totalX = 0;
            totalY = 0;
            updateValues(0, 0);
        });

        mouseArea.setFocusable(true);

        mouseArea.setOnCapturedPointerListener((view, event) -> {
            processMouse(event);
            return true;
        });

        mouseArea.setOnGenericMotionListener((view, event) -> {
            if (event.getSource() == android.view.InputDevice.SOURCE_MOUSE) {
                processMouse(event);
                return true;
            }
            return false;
        });
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setPadding(0, 12, 0, 12);
        return t;
    }

    private void processMouse(MotionEvent event) {
        float x = event.getAxisValue(MotionEvent.AXIS_RELATIVE_X);
        float y = event.getAxisValue(MotionEvent.AXIS_RELATIVE_Y);

        totalX += x;
        totalY += y;

        captured = true;
        status.setText("Mouse: captured");
        updateValues(x, y);

        rawValue.setText(String.format(
            Locale.US,
            "Last movement: X %.2f | Y %.2f",
            x, y
        ));
    }

    private void updateValues(float x, float y) {
        xValue.setText(String.format(
            Locale.US, "X: %.2f  | Total: %.2f", x, totalX
        ));

        yValue.setText(String.format(
            Locale.US, "Y: %.2f  | Total: %.2f", y, totalY
        ));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        captured = hasCapture;
        status.setText(hasCapture
            ? "Mouse: captured"
            : "Mouse: not captured");
    }
}
