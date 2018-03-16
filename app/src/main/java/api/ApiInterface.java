package api;

import api.response.CafeEtcInfoResponse;
import api.response.CafeResponse;
import api.response.CommonResponse;
import api.response.LoginResponse;
import api.response.MyCommentResponse;
import api.response.MyFavoriteCntResponse;
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

    /**
     * Load Cafe List API
     * @param tag -> cafe_list_from_user_location
     * @param uid
     * @param user_latitude
     * @param user_longitude
     * @param last_cafe_id
     * @return
     */
    @GET("cafe/cafe_info.php")
    Call<CafeResponse> GetCafeListFromMyLocation(@Query("tag") String tag, @Query("uid") String uid, @Query("user_latitude") Double user_latitude,
                                                 @Query("user_longitude") Double user_longitude, @Query("last_cafe_id") String last_cafe_id);

    /**
     * Get Cafe Info With CafeId API
     * @param tag -> about_cafe_info_with_cafe_id
     * @param cafe_id
     * @return
     */
    @GET("cafe/cafe_info.php")
    Call<CafeResponse> GetAboutCafeInfo(@Query("tag") String tag, @Query("cafe_id") String cafe_id);

    /**
     * About Cafe Etc Info 불러오기 API
     * @param tag -> cafe_etc_info
     * @param cafe_id
     * @param all_flag -> Y/N
     * @return
     */
    @GET("cafe/cafe_info.php")
    Call<CafeEtcInfoResponse> GetCafeEtcInfo(@Query("tag") String tag, @Query("cafe_id") String cafe_id, @Query("uid") String uid, @Query("all_flag") String all_flag);

    /**
     * Cafe Like API
     * @param tag -> like_cafe
     * @param uid
     * @param cafe_id
     * @param state -> like state Y/N
     * @return
     */
    @FormUrlEncoded
    @POST("cafe/cafe_info.php")
    Call<CommonResponse> LikeCafe(@Field("tag") String tag, @Field("uid") String uid, @Field("cafe_id") String cafe_id, @Field("like_state") String state);

    /**
     * Write Cafe Comment API
     * @param tag -> write_cafe_comment
     * @param uid
     * @param cafe_id
     * @param comment_text
     * @return
     */
    @FormUrlEncoded
    @POST("cafe/cafe_info.php")
    Call<CommonResponse> WriteCafeComment(@Field("tag") String tag, @Field("uid") String uid, @Field("cafe_id") String cafe_id, @Field("comment_text") String comment_text);

    /**
     * Like Cafe Count API
     * @param tag -> my_favorite_cafe_cnt
     * @param uid
     * @return
     */
    @GET("cafe/my_favorite.php")
    Call<MyFavoriteCntResponse> GetMyFavoriteCnt(@Query("tag") String tag, @Query("uid") String uid);

    /**
     * Get My Comment Cafe List API
     * @param tag -> my_comment_cafe
     * @param uid
     * @return
     */
    @GET("cafe/my_favorite.php")
    Call<MyCommentResponse> GetMyCommentCafeList(@Query("tag") String tag, @Query("uid") String uid);

    /**
     * Get My Favorite Cafe List API
     * @param tag -> my_favorite_cafe
     * @param uid
     * @return
     */
    @GET("cafe/my_favorite.php")
    Call<CafeResponse> GetMyFavoriteCafeList(@Query("tag") String tag, @Query("uid") String uid);

    /**
     * Get Recommend Cafe List API
     * @param tag -> recommend_cafe
     * @return
     */
    @GET("cafe/cafe_info.php")
    Call<CafeResponse> GetRecommendCafeList(@Query("tag") String tag);



}
