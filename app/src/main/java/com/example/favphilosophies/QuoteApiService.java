package com.example.favphilosophies;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;


public interface QuoteApiService {
    @GET("v1/quotes")
    Call<List<QuoteResponse>> getQuotes(@Query("category") String category);
}

