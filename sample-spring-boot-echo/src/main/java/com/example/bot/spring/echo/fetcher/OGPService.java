package com.example.bot.spring.echo.fetcher;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.bot.spring.echo.Location;
import com.example.bot.spring.echo.TabelogApi;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.java8.Java8CallAdapterFactory;

@Slf4j
@Service
public class OGPService {
    private final TabelogApi tabelogService;

    public OGPService() {
        final Logger slf4jLogger = LoggerFactory.getLogger(TabelogApi.class);

        tabelogService = new Builder()
                .baseUrl("https://tabelog.com/")
                .client(new OkHttpClient.Builder()
                                .addInterceptor(new HttpLoggingInterceptor(slf4jLogger::info)
                                                        .setLevel(Level.HEADERS))
                                .build())
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .build()
                .create(TabelogApi.class);
    }

    public Location getLocationMessage(String path) {
        final String html = tabelogService.getSync(path);
        return extractLocationImpl(html);
    }

    Location extractLocationImpl(String html) {
        final Document document = Jsoup.parse(html);

        final Pattern compile = Pattern.compile("center=([-0-9\\.]+),([-0-9\\.]+)");
        final Matcher matcher = compile.matcher(html);
        final URI image = URI.create(document.getElementsByAttributeValue("property", "og:image")
                                             .attr("content"));

        matcher.find();

        double latitude = Double.valueOf(matcher.group(1));
        double longitude = Double.valueOf(matcher.group(2));

        log.info("groupCount: {}", matcher.groupCount());
        log.info("groupCount: {}", latitude);
        log.info("groupCount: {}", longitude);

        final Element elementById = document.getElementById("pr-comment-body");
        final String comment = elementById != null ? elementById.text() : null;

        return new Location(document.title(), comment, image, latitude, longitude);
    }
}
