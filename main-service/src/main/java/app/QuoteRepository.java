package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuoteRepository {

    private final List<Quote> quotes = Collections.synchronizedList(new ArrayList<>());
    private final Random random = new Random();

    private Quote quoteOfTheDay; // na početku null

    public void addQuote(Quote quote) {
        synchronized (quotes) {
            quotes.add(quote);
            quoteOfTheDay = quotes.get(random.nextInt(quotes.size()));
        }
    }

    public List<Quote> getAllQuotes() {
        synchronized (quotes) {
            return new ArrayList<>(quotes);
        }
    }

    public Quote getQuoteOfTheDay() {
        synchronized (quotes) {
            return quoteOfTheDay;
        }
    }
}