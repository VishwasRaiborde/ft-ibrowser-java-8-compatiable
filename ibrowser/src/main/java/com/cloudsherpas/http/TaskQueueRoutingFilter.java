package com.cloudsherpas.http;

import com.cloudsherpas.GlobalConstants;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.litemvc.LiteMvcFilter;

@Singleton
public class TaskQueueRoutingFilter extends LiteMvcFilter {

  private Injector injector;

  @Inject
  public void setInjector(Injector injector) {
    this.injector = injector;
  }

  @Override
  public void configure() {
    map(GlobalConstants.TASK_QUEUE + "/group", GoogleGroupQueueServlet.class);
  }

  @Override
  public Object createObject(Class<?> clazz) throws Exception {
    return injector.getInstance(clazz);
  }

}
