package es.tfg.game;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GameInterface {
    @GET("api/guest/games")
    Call<String> STRING_CALL(
            @Query("page") int page,
            @Query("limit") int limit
    );
}
