package com.arnab.ambucyclerider.Remote;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAPI {

    //Rememner enable billing for this project to use this API
    @GET("maps/api/directions/json")
    Observable<String> getDirections(
            @Query("mode") String mode,
            @Query("transit_rounting_preference") String transit_routing,
            @Query("origin") String from,
            @Query("destination") String to,
            @Query("key") String key
    );

}

