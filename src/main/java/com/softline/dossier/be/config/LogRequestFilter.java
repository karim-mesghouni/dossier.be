package com.softline.dossier.be.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softline.dossier.be.Halpers.Functions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "RequestLogger")
public class LogRequestFilter extends BasicAuthenticationFilter {
    public LogRequestFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        CustomHttpRequestWrapper requestWrapper = new CustomHttpRequestWrapper(req);
        try {
            if (req.getMethod().equals("POST")) {
                if (req.getServletPath().equals("/graphql")) {
                    var json = new ObjectMapper().readValue(requestWrapper.getBodyInStringFormat().replaceAll("\\\\n", " ").replaceAll("\\.\\.\\.", "\"}"), Map.class);
                    String gql = Functions.tap(Pattern.compile("(.*?)([{(])").matcher(json.get("query").toString().replaceAll("(query|mutation).*?(?=\\{)", "").replaceFirst("\\{", "").trim()), Matcher::find).group().replaceAll("[}{)(]", "");
                    log.info("[GQL] {}({})", gql, json.get("variables"));
                } else {
                    log.info("[POST] {} Authorization:{}", req.getServletPath(), req.getHeader("Authorization").length() > 20);
                }
            } else {
                log.info("[GET] {}", req.getServletPath());
            }
        } catch (Throwable e) {
            log.warn("could not read request");
        } finally {
            chain.doFilter(requestWrapper, res);
        }
    }


    private static class CustomHttpRequestWrapper extends HttpServletRequestWrapper {
        private final String bodyInStringFormat;

        public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            bodyInStringFormat = readInputStreamInStringFormat(request.getInputStream(), Charset.forName(request.getCharacterEncoding()));
        }

        public String getBodyInStringFormat() {
            return bodyInStringFormat;
        }

        private String readInputStreamInStringFormat(InputStream stream, Charset charset) throws IOException {
            final int MAX_BODY_SIZE = 1024 * 1024 * 400;
            final StringBuilder bodyStringBuilder = new StringBuilder();
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream);
            }

            stream.mark(MAX_BODY_SIZE + 1);
            final byte[] entity = new byte[MAX_BODY_SIZE + 1];
            final int bytesRead = stream.read(entity);

            if (bytesRead != -1) {
                bodyStringBuilder.append(new String(entity, 0, Math.min(bytesRead, MAX_BODY_SIZE), charset));
                if (bytesRead > MAX_BODY_SIZE) {
                    bodyStringBuilder.append("...");
                }
            }
            stream.reset();

            return bodyStringBuilder.toString();
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyInStringFormat.getBytes());

            return new ServletInputStream() {
                private boolean finished = false;

                @Override
                public boolean isFinished() {
                    return finished;
                }

                @Override
                public int available() {
                    return byteArrayInputStream.available();
                }

                @Override
                public void close() throws IOException {
                    super.close();
                    byteArrayInputStream.close();
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                public int read() {
                    int data = byteArrayInputStream.read();
                    if (data == -1) {
                        finished = true;
                    }
                    return data;
                }
            };
        }
    }
}