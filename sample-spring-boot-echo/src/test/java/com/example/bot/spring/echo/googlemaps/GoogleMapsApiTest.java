package com.example.bot.spring.echo.googlemaps;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.example.bot.spring.echo.googlemaps.GoogleMapsApi.NearBySearchResponse;
import com.example.bot.spring.echo.googlemaps.GoogleMapsApi.PlaceResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleMapsApiTest {
    GoogleMapsApi googleMapsApi = GoogleMapsService.getGoogleMapsApi("AIzaSyAGWBuI3nBvEm0aUzw8KCF3UhlBJWjNPRA");

    @Test
    public void getTest() throws Exception {
        final CompletableFuture<NearBySearchResponse> completableFuture =
                googleMapsApi.nearBySearch("東京国立博物館", "35.709636,139.48838");

        log.info("{}", completableFuture.get());
        log.info("{}", completableFuture.get().getResults().get(0).getPlaceId());
    }

    @Test
    public void getPlaceDetailTest() throws Exception {
        final PlaceResponse placeResponse =
                googleMapsApi.placeDetail("ChIJEX3XFIOOGGAR3XdJvRjWLyM").get();

        log.info(placeResponse.toString());
    }
}