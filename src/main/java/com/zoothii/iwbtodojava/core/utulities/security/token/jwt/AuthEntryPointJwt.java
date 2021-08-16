package com.zoothii.iwbtodojava.core.utulities.security.token.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.zoothii.iwbtodojava.core.utulities.results.ErrorResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    //private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    private final Gson gson = new Gson();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //logger.error("Unauthorized error: {}", authException.getMessage());

        // return custom error result instead of default sendError
        ErrorResult errorResult = new ErrorResult("Unauthorized");
        String errorResultString = this.gson.toJson(errorResult);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(errorResultString);
        out.flush();
    }
}
