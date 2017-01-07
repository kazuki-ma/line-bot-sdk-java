package com.example.bot.spring.echo.googlemaps;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.example.bot.spring.echo.googlemaps.GoogleMapsApi.NearBySearchResponse;
import com.example.bot.spring.echo.googlemaps.GoogleMapsApi.PlaceResponse.Result;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
@Service
public class GoogleMapsService {

    public static final Pattern URL_LOCATION_PATTERN = Pattern.compile("/@([-0-9.]+,[-0-9.]+)");
    private final GoogleMapsApi googleMapsApi;

    public GoogleMapsService() {
        googleMapsApi = getGoogleMapsApi("AIzaSyAGWBuI3nBvEm0aUzw8KCF3UhlBJWjNPRA");
    }

    static GoogleMapsApi getGoogleMapsApi(final String key) {
        final Logger slf4jLogger = LoggerFactory.getLogger(GoogleMapsApi.class);

        final ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return new Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(new OkHttpClient.Builder()
                                .addInterceptor(new HttpLoggingInterceptor(slf4jLogger::info)
                                                        .setLevel(Level.HEADERS))
                                .addInterceptor(chain -> {
                                    final URI uri;
                                    try {
                                        uri = new URIBuilder(chain.request().url().uri())
                                                .addParameter("key", key)
                                                .addParameter("language", "ja")
                                                .build();
                                    } catch (final URISyntaxException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return chain.proceed(chain.request().newBuilder()
                                                              .url(uri.toString())
                                                              .build());
                                })
                                .build())
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
                .create(GoogleMapsApi.class);
    }

    @SneakyThrows
    public TitleLocationPair getLocationFromShortUri(
            String title,
            URI shortenUri) {
        Selenide.open(shortenUri.toString());
        final URI longUrl = ((Supplier<URI>) () -> {
            for (int i = 0; i < 10; i++) {
                String currentUrl = WebDriverRunner.url();

                if (currentUrl.contains("maps/place")) {
                    return URI.create(currentUrl);
                }
            }
            throw new RuntimeException();
        }).get();

        return getLocationFromLongUri(title, longUrl);
    }

    @SneakyThrows
    public TitleLocationPair getLocationFromLongUri(String title, URI longUrl) {
        final String titleFromUri = getTitleStringFromUri(longUrl);
        final String location = getLocationStringFromUri(longUrl);
        final TitleLocationPair titleLocationPair = new TitleLocationPair()
                .setTitle(title)
                .setLocation(location);

        final NearBySearchResponse nearBySearchResponse =
                googleMapsApi.nearBySearch(titleFromUri, titleLocationPair.getLocation()).get();

        final Result placeDetail =
                googleMapsApi.placeDetail(nearBySearchResponse.getResults().get(0).getPlaceId())
                             .get().getResult();

        titleLocationPair.setTitle(placeDetail.getName());
        titleLocationPair.setIcon(placeDetail.getIcon());

        return titleLocationPair;
    }

    @SneakyThrows
    static String getTitleStringFromUri(URI longUrl) {
        final Matcher matcher = Pattern.compile("maps/place/([^/]+)").matcher(longUrl.toString());
        matcher.find();

        return UriUtils.decode(matcher.group(1), StandardCharsets.UTF_8.name());
    }

    static String getLocationStringFromUri(URI currentUrl) {
        final Matcher matcher = URL_LOCATION_PATTERN.matcher(currentUrl.toString());
        matcher.find();
        return matcher.group(1);
    }

    @Data
    @Accessors(chain = true)
    public static class TitleLocationPair {
        String title;
        String location;
        String description;
        URI icon;
    }
}
