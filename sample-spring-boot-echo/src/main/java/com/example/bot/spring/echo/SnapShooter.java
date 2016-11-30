package com.example.bot.spring.echo;

import static com.codeborne.selenide.Selenide.$;

import java.awt.image.BufferedImage;
import java.net.URI;

import org.springframework.stereotype.Service;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.Selenide;

@Service
public class SnapShooter {
    static {
        Configuration.browser = "phantomjs";
    }

    BufferedImage snap(URI target) {
        Selenide.open(target.toString());
        return Screenshots.takeScreenShotAsImage($("html"));
    }
}
