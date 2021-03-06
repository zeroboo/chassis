package com.kixeye.chassis.transport.http;

/*
 * #%L
 * Chassis Transport Core
 * %%
 * Copyright (C) 2014 KIXEYE, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.kixeye.chassis.transport.ExceptionServiceErrorMapper;
import com.kixeye.chassis.transport.dto.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles exception from the APIs.
 * 
 * @author ebahtijaragic
 */
@ControllerAdvice
public class HttpExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(HttpExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
    public ServiceError defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
		ServiceError error = ExceptionServiceErrorMapper.mapException(ex);
		
		switch (error.code) {
			case ExceptionServiceErrorMapper.UNKNOWN_ERROR_CODE:
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				
				logger.error("Unexpected error", ex);
				break;
            case ExceptionServiceErrorMapper.VALIDATION_ERROR_CODE:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if (logger.isDebugEnabled()) {
                    logger.debug("Validation exception", ex);
                }
                break;
            case ExceptionServiceErrorMapper.SECURITY_ERROR_CODE:
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                if (logger.isDebugEnabled()) {
                    logger.debug("Security exception", ex);
                }
                break;
			default:
				if (ex instanceof HttpServiceException) {
		        	HttpServiceException httpEx = (HttpServiceException)ex;
		        	
					response.setStatus(httpEx.httpResponseCode);
				}

				logger.warn("Service exception", ex);
				break;
		}
        
        return error;
    }
}
