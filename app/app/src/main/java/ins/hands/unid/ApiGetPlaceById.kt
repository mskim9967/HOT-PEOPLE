package ins.hands.unid

import android.util.Log
import com.google.gson.annotations.SerializedName
import ins.hands.unid.data.MatchingData
import ins.hands.unid.data.MatchingStatusData
import ins.hands.unid.data.PlaceData
import ins.hands.unid.data.UserData
import retrofit2.http.*
import java.lang.Exception
import javax.net.ssl.SSLEngineResult

interface RemoteDateSourcePlace{
    suspend fun getPlace(id : String) : PlaceResponse
    suspend fun getHotPlace() : HotPlaceResponse
    suspend fun searchPlace(str : String) : HotPlaceResponse
    suspend fun getUserData(token : String) : UserResponse
    suspend fun getMyMatching() : GetMatchingListResponse
    suspend fun getMySuccessMatching() : MatchingListResponse
    suspend fun getMyCancelMatching() : MatchingListResponse
    suspend fun joinMatching(body : MatchingIdClass) : JoinResponse
    suspend fun getMatchingById(id : Int) : GetMatchingResponse
    suspend fun getMatchingSearch(status : String) : GetMatchingListResponse
    suspend fun cancelMatching(id : Int) : StatusResponse
    suspend fun getMatchingSearchId(id:String) : GetMatchingListResponse
    suspend fun createMatching(body : CreateMatchBody) : CreateMatchResponse
    suspend fun getMatchingByGuestId(id : Int, status : String) : MatchingListResponse
    suspend fun makeupMatching(id : Int, joinId : Int) : StatusResponse
    suspend fun closeMatching(id:Int) : StatusResponse
    suspend fun getPlaceType(type : String) : HotPlaceResponse
    suspend fun getSuccessMatching() : GetMatchingListResponse
}
class RemoteDataSourcePlaceImpl(private val service : GetPlaceService) : RemoteDateSourcePlace{
    override suspend fun getPlace(id : String) : PlaceResponse = service.getPlace(id)
    override suspend fun getHotPlace() : HotPlaceResponse {
        lateinit var response: HotPlaceResponse
        Log.d("RemoteDataSource","Get Hotplace")
         try{
             response =service.getHotPlace()
         }catch(e:Exception)
         {
             e.printStackTrace()
             response = HotPlaceResponse("Failer", mutableListOf())
         }
        return response
    }
    override suspend fun searchPlace(str: String): HotPlaceResponse =service.searchPlace(str)
    override suspend fun getUserData(token: String): UserResponse {

        return service.getUserData(token)
    }

    override suspend fun getMyMatching(): GetMatchingListResponse =service.getMyMatching(TOKEN)
    override suspend fun getMySuccessMatching(): MatchingListResponse =service.getMySuccessMatching()
    override suspend fun getMyCancelMatching(): MatchingListResponse =service.getMyCancelMatching()
    override suspend fun joinMatching(body: MatchingIdClass): JoinResponse = service.joinMatcing(TOKEN,body)
    override suspend fun cancelMatching(id: Int): StatusResponse =service.cancelMatching(id,TOKEN)
    override suspend fun getMatchingById(id: Int): GetMatchingResponse =service.getMatchingById(id)
    override suspend fun getMatchingSearch(status: String): GetMatchingListResponse =service.getMatchingSearch(status)
    override suspend fun getMatchingSearchId(id: String): GetMatchingListResponse =service.getMatchingSearchId(id)
    override suspend fun createMatching(body : CreateMatchBody) : CreateMatchResponse = service.createMatching(TOKEN,body)
    override suspend fun getMatchingByGuestId(id : Int, status : String) : MatchingListResponse = service.getMatchingByGuestId(id,status)
    override suspend fun makeupMatching(id: Int, joinId: Int): StatusResponse = service.makeupMatching(id,TOKEN,joinId)
    override suspend fun closeMatching(id: Int): StatusResponse = service.closeMatching(id,TOKEN)
    override suspend fun getPlaceType(type: String): HotPlaceResponse =service.searchPlaceType(type)
    override suspend fun getSuccessMatching(): GetMatchingListResponse {
        try {
            return service.successMatching(TOKEN)
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }
        return GetMatchingListResponse("no", mutableListOf())
    }
}

interface GetPlaceService {
    @GET("/api/hot_place")
    suspend fun getHotPlace() : HotPlaceResponse
    @GET("/api/place/{id}")
    suspend fun getPlace(@Path("id") id : String) : PlaceResponse
    @GET("/api/place/search")
    suspend fun searchPlace(@Query("name") name : String) : HotPlaceResponse
    @GET("/api/hot_place/type/{type}")
    suspend fun searchPlaceType(@Path("type") type : String) : HotPlaceResponse

    @GET("/auth/kakao/token")
    suspend fun getUserData(@Query("access_token") access_token : String) : UserResponse
    @GET("/api/my_matching/wait")
    suspend fun getMyMatching(@Query("access_token") token : String) : GetMatchingListResponse
    @GET("/api/my_matching/success")
    suspend fun getMySuccessMatching() : MatchingListResponse
    @GET("/api/my_matching/cancel")
    suspend fun getMyCancelMatching() : MatchingListResponse
    @POST("/api/matching_join")
    suspend fun joinMatcing(@Query("access_token") token : String, @Body body : MatchingIdClass) : JoinResponse
    @GET("/api/matching/{id}")
    suspend fun getMatchingById(@Path("id") id : Int) : GetMatchingResponse
    @GET("/api/matching/search")
    suspend fun getMatchingSearch(@Query("status") status : String) : GetMatchingListResponse
    @DELETE("/api/matching_join/{id}")
    suspend fun cancelMatching(@Path("id") id:Int, @Query("access_token") token:String) : StatusResponse
    @GET("/api/matching/search")
    suspend fun getMatchingSearchId(@Query("place_id") id : String) : GetMatchingListResponse
    @POST("/api/matching")
    suspend fun createMatching(@Query("access_token") token :String, @Body body: CreateMatchBody) : CreateMatchResponse
    @GET("/api/matching_join/search")
    suspend fun getMatchingByGuestId(@Query("guest_id") id : Int, @Query("status") status : String) : MatchingListResponse
    @GET("/api/matching/{id}/makeup")
    suspend fun makeupMatching(@Path("id") id : Int, @Query("access_token") token : String, @Query("matching_join_id") joinId : Int) : StatusResponse
    @GET("/api/matching/{id}/cancel")
    suspend fun closeMatching(@Path("id") id : Int, @Query("access_token") token : String) : StatusResponse
    @GET("/api/my_matching_join/makeup")
    suspend fun successMatching(@Query("access_token") token:String) : GetMatchingListResponse


}

data class CreateMatchBody(@SerializedName("place_id")val place_id : String, @SerializedName("matching_datetime")val matching_datetime : String,@SerializedName("description") val description : String)
data class CreateMatchResponse(val status : String, val insertId : Int, val data : CreateMatchBody)
data class MatchingListResponse(val status : String, val data : MutableList<MatchingData>)
data class UserResponse(val status : String, val data : UserData)
data class PlaceResponse(val status : String, val data : PlaceData)
data class HotPlaceResponse(val status : String, val data:MutableList<PlaceData>)
data class JoinResponse(val status : String, val insertId : Int, val data : MatchingIdClass)
data class MatchingIdClass(val matching_id : Int)
data class GetMatchingResponse(val status : String, val data : MatchingStatusData)
data class GetMatchingListResponse(val status : String, val data : MutableList<MatchingStatusData>)
data class StatusResponse(val status : String)

