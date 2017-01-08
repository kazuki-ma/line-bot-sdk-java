package com.example.bot.spring.echo.googlemaps;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.example.bot.spring.echo.Location;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleMapsServiceTest {
    @Test
    public void getTitleStringFromUri() throws Exception {
        final String titleStringFromUri = GoogleMapsService
                .getTitleStringFromUri(URI.create(
                        "https://www.google.co.jp/maps/place/%E6%B8%8B%E8%B0%B7%E9%A7%85/@35.6581003,139.699553,17z/data=!3m1!4b1!4m5!3m4!1s0x60188b563b00109f:0x337328def1e2ab26!8m2!3d35.6581003!4d139.7017417"));

        assertThat(titleStringFromUri)
                .isEqualTo("渋谷駅");
    }

    GoogleMapsService target;

    @Before
    public void setUp() {
        target = new GoogleMapsService();
    }

    @Test
    public void getLocationStringFromUri() throws Exception {
        final String locationStringFromUri = GoogleMapsService.getLocationStringFromUri(URI.create(
                "https://www.google.com/maps/place/%E6%B8%8B%E8%B0%B7%E9%A7%85/@35.6581003,139.699553,17z/data=!3m1!4b1!4m5!3m4!1s0x0:0x337328def1e2ab26!8m2!3d35.6581003!4d139.7017417?hl=ja"));

        assertThat(locationStringFromUri)
                .isEqualTo("35.6581003,139.699553");
    }

    @Test
    public void getLocationFromShortUriTest() {
        final Location locationFromShortUri =
                target.getLocationFromShortUri("渋谷駅",
                                               URI.create("https://goo.gl/maps/NnTwuDEhhqS2"));

        log.info("{}", locationFromShortUri);
    }

}