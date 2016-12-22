package com.example.bot.spring.echo.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;

import org.openqa.selenium.Dimension;
import org.springframework.stereotype.Service;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import lombok.SneakyThrows;

@Service
public class SnapShooter {
    static {
        Configuration.browser = "phantomjs";
    }

    @SneakyThrows
    BufferedImage snap(URI target) {
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(1040, 10));
        Selenide.open(target.toString());
        File file = Screenshots.takeScreenShotAsFile();
        return ImageIO.read(file);
    }
}
