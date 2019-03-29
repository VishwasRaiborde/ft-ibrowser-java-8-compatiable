package com.cloudsherpas.http.app;

import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cloudsherpas.GlobalConstants;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.litemvc.LiteMvcFilter;

@Singleton
public class AppMvcFilter extends LiteMvcFilter {

	private Injector injector;

	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void configure() {
		map(GlobalConstants.APP_ROOT, HomeHandler.class).templateResult("OK", "app/app.html");
        map(GlobalConstants.APP_ROOT + "/", HomeHandler.class).templateResult("OK", "app/app.html");
        map(GlobalConstants.APP_ROOT + "/heading/"+GlobalConstants.KEY_REGEX+"", ReportHandler.class).templateResult("OK", "app/app.html");
        map(GlobalConstants.APP_ROOT + "/reportDates", ReportDatesHandler.class).templateResult("OK", "app/app.html");
        map(GlobalConstants.APP_ROOT + "/favouriteReport", FavouriteReportHandler.class).templateResult("OK", "app/app.html");
        map(GlobalConstants.APP_ROOT + "/search", SearchReportHandler.class).templateResult("OK", "app/app.html");      
        map(GlobalConstants.APP_ROOT+ "/user", UserHandler.class).templateResult("OK", "app/app.html");        
        map(GlobalConstants.APP_ROOT + "/view-report/"+GlobalConstants.KEY_REGEX, ReportViewHandler.class);
        map(GlobalConstants.APP_ROOT + "/reportview/(\\d*)", ReportDatesHandler.class);
   }

	@Override
	public Object createObject(Class<?> clazz) throws Exception {
		return injector.getInstance(clazz);
	}

	@Override
	public void processTemplate(HttpServletRequest request, HttpServletResponse response, String templateName,
			Object handler) {

		VelocityContext context = new VelocityContext();
		context.put("request", request);
		context.put("response", response);
		context.put("handler", handler);

		Template template = null;
		try {
			template = Velocity.getTemplate(templateName);

			template.merge(context, response.getWriter());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		super.init(config);

		try {
			Properties props = new Properties();
			props.setProperty("resource.loader", "class");
			props.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			Velocity.init(props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
}
