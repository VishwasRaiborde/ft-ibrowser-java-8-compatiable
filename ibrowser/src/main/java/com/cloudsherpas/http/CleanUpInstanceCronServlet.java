package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.labs.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class CleanUpInstanceCronServlet extends HttpServlet {
  @Override
  protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
      throws ServletException, IOException {
    Queue queue = QueueFactory.getQueue("AppQueue");
    TaskOptions taskOptions = TaskOptions.Builder.withUrl("/clean-up-instances")
        .header("Host",
            ModulesServiceFactory.getModulesService().getModuleHostname("ibrowser-backend", "2"))
        .method(Method.GET);
//		                            .header("Host", BackendServiceFactory.getBackendService().getBackendAddress("report-instance-backend"))
//		                            .method(Method.GET);
    queue.add(taskOptions);
  }

}
