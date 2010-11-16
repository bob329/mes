/**
 * ********************************************************************
 * Code developed by amazing QCADOO developers team.
 * Copyright (c) Qcadoo Limited sp. z o.o. (2010)
 * ********************************************************************
 */

package com.qcadoo.mes.security.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public final class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authResult) throws IOException, ServletException {

        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(response);

        super.successfulAuthentication(request, redirectResponseWrapper, authResult);

        response.getOutputStream().println("loginSuccessfull");

    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException failed) throws IOException, ServletException {

        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(response);

        super.unsuccessfulAuthentication(request, redirectResponseWrapper, failed);

        if (failed.getExtraInformation() == null) {
            response.getOutputStream().println("loginUnsuccessfull:login");
        } else {
            response.getOutputStream().println("loginUnsuccessfull:password");
        }

    }

    private static final class RedirectResponseWrapper extends HttpServletResponseWrapper {

        public RedirectResponseWrapper(final HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
        }

        @Override
        public void sendRedirect(final String string) throws IOException {
            // this method should be empty to prevent setting redirect by parent
        }

    }
}
