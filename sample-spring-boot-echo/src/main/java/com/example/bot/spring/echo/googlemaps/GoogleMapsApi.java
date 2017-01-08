package com.example.bot.spring.echo.googlemaps;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.Data;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {
    @GET("maps/api/place/nearbysearch/json?rankby=distance")
    CompletableFuture<NearBySearchResponse> nearBySearch(
            @Query("name") String name,
            @Query("location") String location);

    @Data
    class NearBySearchResponse {
        List<String> htmlAttributions;
        List<Result> results;

        @Data
        static class Result {
            @Data
            static class Geometry {
                @Data
                static class Location {
                    double lat;
                    double lng;
                }

                Location location;
            }

            Geometry geometry;
            URI icon;
            String id;
            String name;
            String placeId;
            String reference;
            List<String> types;
            String vicinity;
        }
    }

    @GET("maps/api/place/details/json")
    CompletableFuture<PlaceResponse> placeDetail(
            @Query("placeid") String placeid);

    @Data
    class PlaceResponse {
        @Data
        static class Result {
            @Data
            static class Photo {
                String photoReference;
                int height;
                int width;
            }

            URI icon;
            String name;
            List<Photo> photos;
        }

        Result result;
    }

    @GET("maps/api/place/photo?maxwidth=640")
    CompletableFuture<PlacePhotoResponse> placePhoto(
            String photoReference);

    URI palcePhotoUrl(
            String photoReference);

    @Data
    class PlacePhotoResponse {

    }
}
