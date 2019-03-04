package com.my.blog.website.consummer.interceptor;


import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.getAndClearSavedRequest(request);
        String successUrl = "/admin/index";

        WebUtils.redirectToSavedRequest(request,response,successUrl);
//        WebUtils.issueRedirect(request,response,successUrl);
        return false;
    }
//    @Override
//    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
//        WebUtils.issueRedirect(request, response, this.getSuccessUrl(), null, true);
//    }

}

