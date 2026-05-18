package $PKG_NAME;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(getResources().getIdentifier("activity_main", "layout", getPackageName()));
    }
}
