/**
 * Copyright 2013 TouK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.httplogger.filters;

import grails.plugins.httplogger.HttpLogger;
import grails.plugins.httplogger.MultiReadHttpServletRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
public class LogRawRequestInfoFilter extends HttpLoggerFilter {

    private String[] headersToLog;

    @Override
    protected void logRequest(MultiReadHttpServletRequest requestWrapper) throws IOException, ServletException {
        addAttributes(requestWrapper);
        logRawRequestInfo(requestWrapper);
    }

    protected void addAttributes(HttpServletRequest servletRequest) {
        RequestData requestData = new RequestData(servletRequest);
        requestData.setStartTimeMillis(System.currentTimeMillis());
        requestData.setRequestNumber(HttpLogger.REQUEST_NUMBER_COUNTER.incrementAndGet());
    }

    protected void logRawRequestInfo(MultiReadHttpServletRequest requestWrapper) throws IOException {
        if (!logger.isInfoEnabled()) {
            return;
        }
        RequestData requestData = new RequestData(requestWrapper);

        Long requestNumber = requestData.getRequestNumber();
        String method = requestWrapper.getMethod();
        String urlWithQueryString = requestData.getUrlWithQueryString();
        String headers = requestData.getHeadersAsString(headersToLog);

        logger.info("<< #" + requestNumber + ' ' + method + ' ' + urlWithQueryString);
        logger.info("<< #" + requestNumber + ' ' + "headers " + headers);
        if ("POST".equalsIgnoreCase(method)) {
            logger.info("<< #" + requestNumber + ' ' + "body: '" + requestWrapper.getCopiedInput() + "'");
        }
    }

    public void setHeaders(String headers) {
        this.headersToLog = StringUtils.tokenizeToStringArray(headers, ",");
    }
}
