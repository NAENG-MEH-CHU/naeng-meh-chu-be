package org.example.support;

import lombok.RequiredArgsConstructor;
import org.example.application.JwtAuthService;
import org.example.domain.entity.Member;
import org.example.exception.exceptions.BearerTokenNotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtLoginResolver implements HandlerMethodArgumentResolver {

    private final JwtAuthService jwtAuthService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtLogin.class) &&
                Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Member resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) {
        String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        JwtUtil.validateAuthorization(authorizationHeader);
        return jwtAuthService.findMemberByJwtPayload(JwtUtil.getJwtPayload(Objects.requireNonNull(authorizationHeader)));
    }
}