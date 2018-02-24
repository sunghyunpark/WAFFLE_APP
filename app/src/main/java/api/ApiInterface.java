package api;

import api.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SungHyun on 2018-02-24.
 */

public interface ApiInterface {

    /**
     * Register API
     * @param tag -> register
     * @param uid -> user's uid from firebase
     * @param name
     * @param phone_num
     * @return
     */
    @FormUrlEncoded
    @POST("users/login.php")
    Call<LoginResponse> registerApi(@Field("tag") String tag, @Field("uid") String uid,
                                      @Field("name") String name, @Field("phone_num") String phone_num);

    /**
     * Login API
     * @param tag -> login
     * @param uid -> user's uid from firebase
     * @return
     */
    @GET("users/login.php")
    Call<LoginResponse> loginApi(@Query("tag") String tag, @Query("uid") String uid);
}
