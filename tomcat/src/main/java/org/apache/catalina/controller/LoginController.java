package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.HttpSessionManger;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";
    private static final HttpSessionManger HTTP_SESSION_MANGER = new HttpSessionManger();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        HttpCookie httpCookie = request.getCookie();

        if (httpCookie.isContains(JSESSIONID) && checkAlreadyLogin(httpCookie)) {
            response.setRedirect("/index.html");
            return;
        }

        ResourceReader.serveResource("/login.html", response);
    }

    private boolean checkAlreadyLogin(HttpCookie httpCookie) {
        String sessionId = httpCookie.findCookie(JSESSIONID);
        HttpSession session = HTTP_SESSION_MANGER.findSession(sessionId);
        return session.getAttribute(HttpSession.USER_ATTRIBUTE) != null;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String password = request.getBody("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> login(user, response, password),
                        () -> response.setRedirect("/401.html")
                );
    }

    private void login(User user, HttpResponse response, String password) {
            if (!user.checkPassword(password)) {
                response.setRedirect("/401.html");
                return;
            }

            String sessionId = saveSession(user);

            log.info("로그인 성공 :: account = {}", user.getAccount());
            response.setCookie(JSESSIONID, sessionId);
            response.setRedirect("/index.html");
    }

    private String saveSession(User user) {
        String sessionId = String.valueOf(UUID.randomUUID());

        HttpSession httpSession = new HttpSession(sessionId);
        httpSession.setAttribute(HttpSession.USER_ATTRIBUTE, user);
        HTTP_SESSION_MANGER.add(httpSession);

        return sessionId;
    }
}
