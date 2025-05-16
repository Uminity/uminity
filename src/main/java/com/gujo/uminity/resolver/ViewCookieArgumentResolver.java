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

import java.util.Map;

public class ViewCookieArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String COOKIE_NAME = "VIEWED_POSTS";
    private static final int MAX_AGE = 3 * 60; // 3분

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
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        @SuppressWarnings("unchecked")
        Map<String, String> uriVars = (Map<String, String>)
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String postIdStr = uriVars.get("postId");

        // 쿠키값 읽어서
        String tmpCookie = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
                    tmpCookie = c.getValue();
                    break;
                }
            }
        }
        boolean isNew = !tmpCookie.contains(postIdStr);

        if (isNew) {
            String newVal = tmpCookie.isEmpty() ? postIdStr : tmpCookie + "," + postIdStr;
            Cookie newCookie = new Cookie(COOKIE_NAME, newVal);
            newCookie.setPath("/");
            newCookie.setMaxAge(MAX_AGE);
            response.addCookie(newCookie);
        }

        return isNew;
    }
}