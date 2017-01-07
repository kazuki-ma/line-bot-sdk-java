package com.example.bot.spring.echo.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.net.HttpHeaders;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class HtmlMessageConposer {
    SnapShooter snapShooter;

    @RequestMapping(path = "/test/**")
    public ModelAndView modelAndView() {
        return new ModelAndView("test");
    }

    @GetMapping(path = "/image")
    public void image(
            final HttpServletResponse response,
            @RequestParam("path") final URI path) {
        BufferedImage snap = snapShooter.snap(path);
        writeImageToResponse(snap, response);
    }

    static void writeImageToResponse(
            final BufferedImage result,
            final HttpServletResponse httpServletResponse) {
        final ImageWriter writer;
        final ImageWriteParam jpgWriteParam;
        boolean png = true;

        if (png) {
            writer = ImageIO.getImageWritersByFormatName("png").next();
            jpgWriteParam = null;
        } else {
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            jpgWriteParam = writer.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.3f);
        }

        final BufferedImage newBufferedImage =
                new BufferedImage(result.getWidth(), result.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(result, 0, 0, Color.WHITE, null);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(byteArrayOutputStream)) {

            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(newBufferedImage, null, null), jpgWriteParam);

            imageOutputStream.flush();

            httpServletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
            httpServletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, byteArrayOutputStream.size());

            httpServletResponse.getOutputStream().write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
