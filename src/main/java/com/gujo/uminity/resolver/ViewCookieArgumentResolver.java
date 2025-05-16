package com.gujo.uminity.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class ViewCookieArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String COOKIE_NAME = "VIEWED_POSTS";
    private static final int MAX_AGE_SECONDS = 1 * 60; // 3분

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ViewCookie.class)
                && boolean.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse resp = webRequest.getNativeResponse(HttpServletResponse.class);

        @SuppressWarnings("unchecked")
        Map<String, String> uriVars = (Map<String, String>)
                req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String annotatedName = parameter.getParameterAnnotation(ViewCookie.class).postIdParam();
        String postId = uriVars != null ? uriVars.get(annotatedName) : null;

        if (postId == null || !postId.matches("\\d+")) {
            return false;
        }

        String decoded = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName()) && c.getValue() != null) {
                    decoded = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        boolean isNew = Arrays.stream(decoded.split(","))
                .noneMatch(postId::equals);

        // 4) 새로운 ID면 쿠키에 추가 (인코딩)
        if (isNew) {
            String updated = decoded.isEmpty() ? postId : (decoded + "," + postId);
            String encoded = URLEncoder.encode(updated, StandardCharsets.UTF_8);

            Cookie newCookie = new Cookie(COOKIE_NAME, encoded);
            newCookie.setPath("/");
            newCookie.setMaxAge(MAX_AGE_SECONDS);
            resp.addCookie(newCookie);
        }

        return isNew;
    }
}
