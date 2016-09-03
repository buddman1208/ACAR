package kr.edcan.acar.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import kr.edcan.acar.utils.NetworkHelper;
import kr.edcan.acar.utils.NetworkInterface;
import kr.edcan.acar.utils.DataManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by JunseokOh on 2016. 9. 3..
 */
public class MessageInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "GameRank";
    NetworkInterface networkInterface;
    DataManager manager;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        manager = new DataManager();
        manager.initializeManager(getApplicationContext());
        if(manager.getActiveUser().first) {
            networkInterface = NetworkHelper.getNetworkInstance();
            Call<ResponseBody> sendToken = networkInterface.pushToken(refreshedToken, manager.getActiveUser().second.getId());
            sendToken.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    switch (response.code()){
                        case 200:

                            break;
                        case 401:
                            break;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("asdf", t.getMessage());
                }
            });

        }
    }

}
