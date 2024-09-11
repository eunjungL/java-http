package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class WelcomeController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new IllegalArgumentException("POST는 지원하지 않습니다.");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setBody("Hello world!", "text/html");
        response.setStatus(HttpStatus.OK);
    }
}
