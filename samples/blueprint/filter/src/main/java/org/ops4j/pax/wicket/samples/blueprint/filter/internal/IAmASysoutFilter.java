package org.ops4j.pax.wicket.samples.blueprint.filter.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class IAmASysoutFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init filter");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {
        System.out.println("filter...filter...fileter....");
    }

    public void destroy() {
        System.out.println("Destroy filter");
    }

}
