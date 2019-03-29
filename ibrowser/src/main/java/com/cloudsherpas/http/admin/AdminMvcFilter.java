package com.cloudsherpas.http.admin;

import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.http.NoAccessPageHandler;
import com.cloudsherpas.http.app.UserHandler;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.litemvc.LiteMvcFilter;

@Singleton
public class AdminMvcFilter extends LiteMvcFilter {

	private Injector injector;

	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void configure() {
        map(GlobalConstants.ADMIN_ROOT, AdminHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/", AdminHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/noaccess", NoAccessPageHandler.class).templateResult("OK", "noAccess.html");
        map(GlobalConstants.ADMIN_ROOT + "/user", UserHandler.class).templateResult("OK", "admin/container.html");        
        
        map(GlobalConstants.ADMIN_ROOT + "/delete", EntityDeleteHandler.class);
        
        map(GlobalConstants.ADMIN_ROOT + "/heading", HeadingHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/heading/"+GlobalConstants.KEY_REGEX+"/action", HeadingActionHandler.class).templateResult("OK", "admin/headingForm.html").templateResult("SUCCESS", "close_popup.html");
        
        map(GlobalConstants.ADMIN_ROOT + "/type", TypeHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/viewAllType", ViewAllTypeHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/type/"+GlobalConstants.KEY_REGEX+"/action", TypeActionHandler.class).templateResult("OK", "admin/typeForm.html").templateResult("SUCCESS", "close_popup.html");
        
        map(GlobalConstants.ADMIN_ROOT + "/report", ReportHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/viewAllReport", ViewAllReportHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/report/"+GlobalConstants.KEY_REGEX+"/action", ReportActionHandler.class).templateResult("OK", "admin/reportForm.html").templateResult("SUCCESS", "close_popup.html");
	    
        map(GlobalConstants.ADMIN_ROOT + "/tradingYear", TradingYearHandler.class).templateResult("OK", "admin/container.html");
        map(GlobalConstants.ADMIN_ROOT + "/tradingYear/"+GlobalConstants.KEY_REGEX+"/action", TradingYearActionHandler.class).templateResult("OK", "admin/tradingYearForm.html").templateResult("SUCCESS", "close_popup.html");
        
        map(GlobalConstants.ADMIN_ROOT + "/news", NewsHandler.class).templateResult("OK", "admin/container.html");	
        map(GlobalConstants.ADMIN_ROOT + "/news/"+GlobalConstants.KEY_REGEX+"/action", NewsActionHandler.class).templateResult("OK", "admin/newsForm.html").templateResult("SUCCESS", "close_popup.html");		    
        
        map(GlobalConstants.ADMIN_ROOT + "/reportValidation", ReportValidationHandler.class).templateResult("OK", "admin/reportForm.html");
        map(GlobalConstants.ADMIN_ROOT + "/typeValidation", TypeValidationHandler.class).templateResult("OK", "admin/typeForm.html");
        
        map(GlobalConstants.ADMIN_ROOT + "/upload", FileUploadHandler.class);

 
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
