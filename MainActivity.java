package takamk2.local.wish2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import takamk2.local.wish2.reward.DisplayRewardFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        long id = 1; // TODO: temporary
        transaction.replace(R.id.fragment_container1, DisplayRewardFragment.newInstance(id));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
