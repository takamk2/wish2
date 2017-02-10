package takamk2.local.wish2.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by takamk2 on 17/01/19.
 * <p>
 * The Edit Fragment of Base Class.
 */

public class DisableTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }
}
