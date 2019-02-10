package com.tti.unilagmba.Remote;

import com.tti.unilagmba.Model.DataMessage;
import com.tti.unilagmba.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Scarecrow on 4/1/2018.
 */

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA9Okv57c:APA91bESA2q1-Y8I0eOnRHBWn6ZDFXyOwAIpMDdKu9i6QCj5qNIRB0F9EBoiCdLN7IzGNPg5-aaWeW9Hhx-ImLGeCwq6dtmPTEOjfL1VeqSnYte6-gGEXd_UnKw5iQTn_pIq_NVZXaEynD-rO0qIPOLi0tRH_6JnoQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);

}
