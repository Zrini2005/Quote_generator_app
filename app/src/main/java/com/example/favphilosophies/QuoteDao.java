package com.example.favphilosophies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quote quote);

    @Query("SELECT * FROM quotes WHERE quote = :quoteText")
    Quote getQuoteByText(String quoteText);

    @Query("SELECT * FROM quotes")
    List<Quote> getAllQuotes();

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    List<Quote> getFavoriteQuotesLimited();
}
