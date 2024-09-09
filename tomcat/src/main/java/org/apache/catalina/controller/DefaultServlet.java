package org.apache.catalina.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class DefaultServlet implements Controller {

    private static final String RESOURCE_PATH = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        serveResource(request, response);
    }

    public void serveResource(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getRequestUrl().split("\\?")[0];
        Path resourcePath = getResourcePath(path);
        String body = Files.readString(resourcePath);
        String contentType = Files.probeContentType(resourcePath);

        response.setBody(body, contentType);
        response.setStatus(HttpStatus.OK);
    }

    private Path getResourcePath(String path) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resourceUrl = classLoader.getResource(RESOURCE_PATH + path);
        if (resourceUrl == null) {
            resourceUrl = classLoader.getResource(RESOURCE_PATH + "/404.html");
        }

        return Path.of(resourceUrl.getPath());
    }
}
