package com.cg.mobinv.general.service.impl.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ODataConfiguration {


    public static final String SERVICE_URL = "/services/odata.svc/*";


    @Autowired
    private ODataCutomServlet servletInitOdata;

    @Bean
    ServletRegistrationBean ODataServlet() {
        return new ServletRegistrationBean(servletInitOdata,SERVICE_URL);
   }
}