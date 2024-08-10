package com.example.favphilosophies;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView, authorTextView;
    private Quote currentQuote;
    private QuoteApiService quoteApiService;
    private QuoteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);
        Button fetchQuoteButton = findViewById(R.id.fetchQuoteButton);
        Button markFavoriteButton = findViewById(R.id.markFavoriteButton);
        Button viewAllQuotesButton = findViewById(R.id.viewAllQuotesButton);
        Button viewFavoriteQuotesButton = findViewById(R.id.viewFavoriteQuotesButton);


        Retrofit retrofit = ApiClient.getClient();
        quoteApiService = retrofit.create(QuoteApiService.class);
        database = QuoteDatabase.getDatabase(this);


        fetchQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markFavoriteButton.setVisibility(View.VISIBLE);
                fetchQuote();
            }
        });

        markFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsFavorite();
                markFavoriteButton.setVisibility(View.GONE);
            }
        });

        viewAllQuotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllQuotes();
            }
        });

        viewFavoriteQuotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFavoriteQuotes();
            }
        });
    }

    private void fetchQuote() {
        Call<List<QuoteResponse>> call = quoteApiService.getQuotes("happiness");
        call.enqueue(new Callback<List<QuoteResponse>>() {
            @Override
            public void onResponse(Call<List<QuoteResponse>> call, Response<List<QuoteResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    QuoteResponse quoteResponse = response.body().get(0);
                    quoteTextView.setText(quoteResponse.getQuote());
                    authorTextView.setText(quoteResponse.getAuthor());

                    currentQuote = new Quote();
                    currentQuote.setQuote(quoteResponse.getQuote());
                    currentQuote.setAuthor(quoteResponse.getAuthor());
                    currentQuote.setCategory("happiness");
                    currentQuote.setFavorite(false);
                    new Thread(() -> {

                        Quote existingQuote = database.quoteDao().getQuoteByText(currentQuote.getQuote());
                        if (existingQuote == null) {

                            database.quoteDao().insert(currentQuote);
                        }
                    }).start();
                } else {
                    quoteTextView.setText("No quote found");
                    authorTextView.setText("");
                }
            }

            @Override
            public void onFailure(Call<List<QuoteResponse>> call, Throwable t) {
                quoteTextView.setText("Failed to fetch quote");
                Log.d("Homepage", "API call failed: " + t.getMessage());
                authorTextView.setText("");
            }
        });
    }

    private void markAsFavorite() {
        if (currentQuote != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Quote existingQuote = database.quoteDao().getQuoteByText(currentQuote.getQuote());
                    if (existingQuote != null) {

                        existingQuote.setFavorite(true);
                        database.quoteDao().insert(existingQuote);
                    }

                }
            }).start();
        }
    }

    private void viewAllQuotes() {
        new Thread(() -> {
            List<Quote> quotes = database.quoteDao().getAllQuotes();
            final StringBuilder quotesText = new StringBuilder();

            if (quotes != null && !quotes.isEmpty()) {
                for (Quote quote : quotes) {

                    quotesText.append(quote.getQuote()).append("\n— ").append(quote.getAuthor()).append("\n\n");
                }
            } else {
                quotesText.append("No quotes found");
            }

            runOnUiThread(() -> {
                SpannableString spannableString = new SpannableString(quotesText.toString());
                int startIndex = 0;
                for (Quote quote : quotes) {
                    int quoteIndex = quotesText.indexOf(quote.getQuote(), startIndex);
                    int authorIndex = quotesText.indexOf("— " + quote.getAuthor(), startIndex);

                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), quoteIndex, quoteIndex + quote.getQuote().length(), 0);
                    spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), authorIndex, authorIndex + "— ".length() + quote.getAuthor().length(), 0);

                    startIndex = authorIndex + "— ".length() + quote.getAuthor().length();
                }

                quoteTextView.setText(spannableString);
                authorTextView.setText("");
            });
        }).start();
    }


    private void viewFavoriteQuotes( ) {
        new Thread(() -> {
            List<Quote> favoriteQuotes = database.quoteDao().getFavoriteQuotesLimited( );
            final StringBuilder quotesText = new StringBuilder();

            if (favoriteQuotes != null && !favoriteQuotes.isEmpty()) {
                for (Quote quote : favoriteQuotes) {
                    quotesText.append(quote.getQuote()).append("\n— ").append(quote.getAuthor()).append("\n\n");
                }
            } else {
                quotesText.append("No favorite quotes found");
            }

            runOnUiThread(() -> {
                SpannableString spannableString = new SpannableString(quotesText.toString());
                int startIndex = 0;
                assert favoriteQuotes != null;
                for (Quote quote : favoriteQuotes) {
                    int quoteIndex = quotesText.indexOf(quote.getQuote(), startIndex);
                    int authorIndex = quotesText.indexOf("— " + quote.getAuthor(), startIndex);

                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), quoteIndex, quoteIndex + quote.getQuote().length(), 0);
                    spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), authorIndex, authorIndex + "— ".length() + quote.getAuthor().length(), 0);

                    startIndex = authorIndex + "— ".length() + quote.getAuthor().length();
                }

                quoteTextView.setText(spannableString);
                authorTextView.setText("");
            });
        }).start();
    }



}
