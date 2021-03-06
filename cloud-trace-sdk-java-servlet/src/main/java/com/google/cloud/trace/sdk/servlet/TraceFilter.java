// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk.servlet;

import com.google.cloud.trace.sdk.CloudTraceException;
import com.google.cloud.trace.sdk.LoggingTraceWriter;
import com.google.cloud.trace.sdk.TraceWriter;
import com.google.cloud.trace.sdk.ReflectionUtils;
import com.google.cloud.trace.sdk.TraceSpanData;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet filter that instantiates a {@link TraceWriter} and installs a {@link TraceSpanData}
 * in the request context and a corresponding ThreadLocal.
 */
public class TraceFilter implements Filter {
  private TraceWriter writer = new LoggingTraceWriter();
  private TraceRequestUtils requestUtils = new TraceRequestUtils();
  private TraceResponseUtils responseUtils = new TraceResponseUtils();

  @Override
  public void destroy() {}

  /**
   * Filter method to install a new {@link TraceSpanData} as a request attribute.
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;
    TraceSpanData spanData = requestUtils.createRequestSpanData(request);
    try {
      chain.doFilter(req, resp);
    } finally {
      try {
        responseUtils.closeResponseSpanData(spanData, writer, response);
      } catch (CloudTraceException e) {
        throw new ServletException(e);
      }
    }
  }

  /**
   * Initializes a {@link TraceWriter} if requested by the properties file init param.
   */
  @Override
  public void init(FilterConfig config) throws ServletException {
    String propertiesFileName = config.getInitParameter("trace-properties-file");
    if (propertiesFileName != null) {
      Properties props = new Properties();
      try {
        props.load(getClass().getClassLoader().getResourceAsStream(propertiesFileName));

        String traceWriterClassName = props.getProperty(getClass().getName() + ".traceWriter");
        if (traceWriterClassName != null && !traceWriterClassName.isEmpty()) {
          writer = (TraceWriter) ReflectionUtils.createFromProperties(traceWriterClassName, props);
        }

        requestUtils.initFromProperties(props);
      } catch (IOException e) {
        throw new ServletException(e);
      }
    }
  }
}
