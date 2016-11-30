package com.example.bot.spring.echo;

import org.junit.Test;

import com.example.bot.spring.echo.TabelogService.Location;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabelogServiceTest {
    TabelogService tabelogService = new TabelogService();

    @Test
    public void test() {
        tabelogService.extractLocationImpl(
                "<img alt=\"マクドナルド 白山駅前店 - 地図\" class=\"js-map-lazyload\" data-original=\"https://maps.googleapis.com/maps/api/staticmap?client=gme-kakakucominc&amp;channel=tabelog.com&amp;sensor=false&amp;hl=ja&amp;center=35.722262838685516,139.75236648480916&amp;markers=color:red%7C35.722262838685516,139.75236648480916&amp;zoom=15&amp;size=510x150&amp;signature=FQxJHSVrhNvLDdU1jINxIlVPJdc=\" src=\"\" />");
    }

    @Test
    public void getLocationMessageTest() {
        Location locationMessage = tabelogService
                .getLocationMessage("/tokyo/A1323/A132305/13136331/");
        log.info("location : {}", locationMessage);
    }
}