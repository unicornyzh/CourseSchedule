package example.yzhhzq.courseschedule;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hasee on 2016/12/2.
 */
public class ServiceID extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        /// sendRegistrationToServer(refreshedToken);
    }
}