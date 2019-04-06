package com.cg.mobinv.general.service.impl.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.servlet.ODataServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.cg.mobinv.mobinventory.service.impl.config.odata.AnnotationODataServiceFactory;

/**
 * @author mchudy
 *
 */
@Configuration
public class ODataCutomServlet extends ODataServlet {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AnnotationODataServiceFactory annotationServiceFactory;

	@Override
	protected ODataServiceFactory getServiceFactory(HttpServletRequest request) {
		return annotationServiceFactory;
	}
}
