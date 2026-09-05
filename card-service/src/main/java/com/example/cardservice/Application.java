package com.example.cardservice;

import com.example.cardservice.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class Application {

    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        // Создаём Tomcat Context
        Context tomcatContext =
                tomcat.addContext("", new File(".").getAbsolutePath());

        // Создаём Spring WebApplicationContext
        AnnotationConfigWebApplicationContext springContext =
                new AnnotationConfigWebApplicationContext();

        springContext.setServletContext(
                tomcatContext.getServletContext()
        );

        springContext.register(AppConfig.class);

        // DispatcherServlet
        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(springContext);

        Wrapper wrapper =
                tomcat.addServlet("", "dispatcher", dispatcherServlet);

        wrapper.setLoadOnStartup(1);
        wrapper.addMapping("/");

        // Spring Security
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("springSecurityFilterChain");
        filterDef.setFilter(
                new DelegatingFilterProxy(
                        "springSecurityFilterChain",
                        springContext
                )
        );

        tomcatContext.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("springSecurityFilterChain");
        filterMap.addURLPattern("/*");

        tomcatContext.addFilterMap(filterMap);

        // Запуск
        tomcat.start();
        tomcat.getServer().await();
    }
}