package com.kixeye.chassis.transport.swagger;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StreamUtils;

/**
 * A servlet to load resources.
 * 
 * @author ebahtijaragic
 */
public class SwaggerServlet extends HttpServlet {
	private static final long serialVersionUID = -691019542966084867L;

	private static final String SWAGGER_DIRECTORY = "classpath:com/kixeye/chassis/transport/swagger";
	
	private static final String WELCOME_PAGE = "index.html";
	
	private DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private String rootContextPath;
	
	public SwaggerServlet(String contextPath) {
		this.rootContextPath = contextPath;
		
		while (rootContextPath.endsWith("*") || rootContextPath.endsWith("/")) {
			rootContextPath = StringUtils.removeEnd(rootContextPath, "*");
			rootContextPath = StringUtils.removeEnd(rootContextPath, "/");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// figure out the real path
		String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
		
		while (pathInfo.endsWith("/")) {
			pathInfo = StringUtils.removeEnd(pathInfo, "/");
		}
		
		while (pathInfo.startsWith("/")) {
			pathInfo = StringUtils.removeStart(pathInfo, "/");
		}
		
		if (StringUtils.isBlank(pathInfo)) {
			resp.sendRedirect(rootContextPath + "/" + WELCOME_PAGE);
		} else {
			// get the resource
			AbstractFileResolvingResource resource = (AbstractFileResolvingResource)resourceLoader.getResource(SWAGGER_DIRECTORY + "/" + pathInfo);
			
			// send it to the response
			if (resource.exists()) {
				StreamUtils.copy(resource.getInputStream(), resp.getOutputStream());
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.flushBuffer();
			} else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}
}
