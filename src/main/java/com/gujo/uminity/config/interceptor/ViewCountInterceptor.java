package com.gujo.uminity.config.interceptor;

import com.gujo.uminity.post.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ViewCountInterceptor implements HandlerInterceptor {

    private final PostService postService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // URI가 /api/posts/{id} 패턴일 때만 처리
        String uri = request.getRequestURI(); // ex) /api/posts/42
        if (uri.matches("^/api/posts/\\d+$")) {
            Long postId = Long.valueOf(uri.substring(uri.lastIndexOf('/') + 1));
            String cookieName = "VIEWED_POST_" + postId;

            boolean viewed = false;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (cookieName.equals(c.getName())) {
                        viewed = true;
                        break;
                    }
                }
            }

            if (!viewed) {
                postService.incrementViewCount(postId);
                Cookie viewCookie = new Cookie(cookieName, "true");
                viewCookie.setPath("/");
                viewCookie.setMaxAge(3 * 60); // 3분
                response.addCookie(viewCookie);
            }
        }
        return true;  // 다음 인터셉터 또는 컨트롤러로 진행
    }
}
