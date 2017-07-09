package com.vengage.booklistingapp;

import android.text.Editable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Vengage on 7/4/2017.
 * <p>
 * Class that contains useful methods and data
 */

class Utils {

    /**
     * Lock this class from being instantiated by using a private constructor
     */
    private Utils() {
    }

    /**
     * Get the books data from URL
     *
     * @param requestURL The request URL
     * @return Return a list of books
     */
    static List<Book> fetchBooksData(String requestURL) {
        // Create the URL object
        URL url = createUrl(requestURL);

        // Get he JSON response from HTTP request to the URL
        String jsonResonse = "";
        try {
            jsonResonse = makeHttpRequest(url);
            if (jsonResonse.equalsIgnoreCase("")) {
                throw new IOException("Problem making the HTTP request.");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        // Get the data from the response
        // Return the data object
        return Utils.extractBooksFromJson(jsonResonse);
    }

    /**
     * Function which extracts the list of books from a JSONString
     *
     * @param bookJSON The String containing the JSON data
     * @return Return a list of books
     */
    private static List<Book> extractBooksFromJson(String bookJSON) {
        // Check if the JSON string is not empty
        if (bookJSON.isEmpty()) {
            return null;
        }

        // Create an empty ArrayList in which will add books
        List<Book> books = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(bookJSON);

            // Check if we received a good JSON response if not we received an error
            if (jsonObject.has("totalItems") && jsonObject.getInt("totalItems") > 0) {
                // We do not need to check if the jsonObject has the "items" array
                // because "totalItems" must be greater than 0
                JSONArray itemsJsonArray = jsonObject.getJSONArray("items");

                // Declare the variables used
                String title;
                String subtitle;
                String authors;
                String description;
                String infoLink;
                String searchInfo;
                int pageCount;
                int ratingsCount;
                double averageRating;

                JSONObject itemObject;
                JSONObject volumeInfoObject;
                JSONObject searchInfoObject;
                JSONArray authorsArray;
                for (int i = 0; i < itemsJsonArray.length(); i++) {
                    // For each element we intialize the variables
                    title = "";
                    subtitle = "";
                    authors = "";
                    description = "";
                    infoLink = "";
                    searchInfo = "";
                    pageCount = 0;
                    ratingsCount = 0;
                    averageRating = 0D;

                    itemObject = itemsJsonArray.getJSONObject(i);
                    volumeInfoObject = itemObject.getJSONObject("volumeInfo");
                    searchInfoObject = null;
                    authorsArray = null;

                    // Setting the corresponding values
                    if (itemObject.has("searchInfo")) {
                        searchInfoObject = itemObject.getJSONObject("searchInfo");
                    }
                    if (volumeInfoObject.has("authors")) {
                        authorsArray = volumeInfoObject.getJSONArray("authors");
                    }
                    if (volumeInfoObject.has("title")) {
                        title = volumeInfoObject.getString("title");
                    }
                    if (authorsArray != null && authorsArray.length() > 0) {
                        authors = readStringFromJsonArray(authorsArray);
                    }
                    if (volumeInfoObject.has("subtitle")) {
                        subtitle = volumeInfoObject.getString("subtitle");
                    }
                    if (volumeInfoObject.has("description")) {
                        description = volumeInfoObject.getString("description");
                    }
                    if (volumeInfoObject.has("pageCount")) {
                        pageCount = volumeInfoObject.getInt("pageCount");
                    }
                    if (volumeInfoObject.has("averageRating")) {
                        averageRating = volumeInfoObject.getDouble("averageRating");
                    }
                    if (volumeInfoObject.has("ratingsCount")) {
                        ratingsCount = volumeInfoObject.getInt("ratingsCount");
                    }
                    if (volumeInfoObject.has("infoLink")) {
                        infoLink = volumeInfoObject.getString("infoLink");
                    }
                    if (searchInfoObject != null && searchInfoObject.has("textSnippet")) {
                        searchInfo = searchInfoObject.getString("textSnippet");
                    }

                    // We only add the books which have at least title and authors
                    if (!title.isEmpty() && !authors.isEmpty())
                        books.add(new Book(title, subtitle, authors, description,
                                infoLink, searchInfo, pageCount, ratingsCount, averageRating));
                }
            } else if (jsonObject.has("totalItems") && jsonObject.getInt("totalItems") == 0) {
                // No books are found for the corresponding query string
                // And we have an error
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the books JSON response.", e);
        }

        return books;
    }

    /**
     * Function which converts a JSONArray of String into one String
     *
     * @param jsonArray The JSONArray of String
     * @return A String made of by the String elements separated by a space
     */
    private static String readStringFromJsonArray(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        if (jsonArray != null) {
            int arrayLength = jsonArray.length();
            for (int i = 0; i < arrayLength; i++) {
                String string = "";
                try {
                    string = jsonArray.getString(i);
                    if (string.equalsIgnoreCase("")) continue;
                } catch (JSONException e) {
                    Log.e(TAG, "Problem parsing the authors JSON response.", e);
                }
                stringBuilder.append(string);
                if (i < (arrayLength - 1)) stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String mUrl) {
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            int responseCode = urlConnection.getResponseCode();
            // OK response
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            // Too many request generate a forbidden acces
            else if(responseCode == 403) {
                Log.e(TAG, "Forbidden access.Error response code: " + urlConnection.getResponseCode());
            }
            // Empty query
            else if(responseCode == 400){
                Log.e(TAG, "Empty query. Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return the string from an editable
     *
     * @param editable The editable return from an EditText
     * @return The string resulted by the concatenation of the characters from the editable
     */
    public static String getStringFromEditable(Editable editable) {
        char[] chars = new char[60];
        editable.getChars(0, editable.length(), chars, 0);
        return String.copyValueOf(chars, 0, editable.length());
    }
}
