package es.tfg.game;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GameUserInterface {
    @GET("api/users/games")
    Call<String> STRING_CALL(
            @Query("page") int page,
            @Query("limit") int limit,
            @Header("auth-token") String token
    );
}
