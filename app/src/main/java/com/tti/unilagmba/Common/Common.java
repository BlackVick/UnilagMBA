package com.tti.unilagmba.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tti.unilagmba.Model.Todo;
import com.tti.unilagmba.Model.User;
import com.tti.unilagmba.Remote.APIService;
import com.tti.unilagmba.Remote.RetrofitClient;

/**
 * Created by Scarecrow on 3/9/2018.
 */

public class Common {
    public static User currentUser;
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static final String USER_NAME = "Username";
    public static final String MATRIC = "Matric";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final int PICK_DOCUMENT_REQUEST = 72;

    public static String topicName = "News";

    public static final String DELETE = "Delete";
    public static final String DELETE_SINGLE = "Delete For You";
    public static final String DELETE_BOTH = "Delete For Everyone";

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static boolean isConnectedToInternet(Context context)    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i<info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

}
