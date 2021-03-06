/* Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright 2005-2012 janux.org */

package org.janux.ui.web.catalina;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.Cookie;

import org.apache.catalina.connector.Response;

import org.janux.bus.processor.ObjectFormatterGeneric;
import org.janux.ui.web.FreemarkerObjectFormatter;
import org.janux.ui.web.CookieFormatter;

/**
 ***************************************************************************************************
 * Used to serialize into a String the contents of a catalina Response object
 * 
 * @author  <a href="mailto:philippe.paravicini@janux.org">Philippe Paravicini</a>
 * @since 0.1 - 2011-03-28
 ***************************************************************************************************
 */
public class HttpResponseFormatter extends FreemarkerObjectFormatter<org.apache.catalina.connector.Response>
{
	public static final String KEY_RESPONSE = "response";
	public static final String KEY_COOKIES  = "cookies";
	public static final String DEFAULT_TEMPLATE_NAME = "response_catalina.ftl";

	private ObjectFormatterGeneric<Cookie[]> cookieFormatter;

	public ObjectFormatterGeneric<Cookie[]> getCookieFormatter() 
	{ 
		if (this.cookieFormatter == null && this.getFreemarkerConfig() != null) {
			this.cookieFormatter = new CookieFormatter(this.getFreemarkerConfig());
		}
		return cookieFormatter; 
	}

	public void setCookieFormatter(ObjectFormatterGeneric<Cookie[]> o) { cookieFormatter = o; }

	public HttpResponseFormatter() {
		super();
		this.setTemplateName(DEFAULT_TEMPLATE_NAME);
	}

	public HttpResponseFormatter(Configuration config) { 
		super(config); 
		this.setTemplateName(DEFAULT_TEMPLATE_NAME);
	}


	public Map<String,Object> buildModel(Response response)
	{
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(KEY_RESPONSE, response);

		if (this.getCookieFormatter() != null) 
		{
			model.put(KEY_COOKIES, this.getCookieFormatter().format(response.getCookies()));
		} else {
			String msg = "CookieFormatter is null";
			model.put(KEY_COOKIES, msg);
		}
		return model;
	}

} // end HttpResponseFormatter class


