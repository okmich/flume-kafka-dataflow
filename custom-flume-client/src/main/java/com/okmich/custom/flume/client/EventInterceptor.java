/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.custom.flume.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

/**
 *
 * @author hadmin
 */
public class EventInterceptor implements Interceptor {

    public static final String KEY = "CURR_PAIRS";
    public static final String HOST_KEY = "REMOTE_CLIENT";
    private static final Logger LOG = Logger.getLogger(EventInterceptor.class.getName());

    private EventInterceptor() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        if (!event.getHeaders().containsKey(KEY)) {
            throw new RuntimeException();
        }
        String value = event.getHeaders().get(KEY);
        String hostAdd = event.getHeaders().get(HOST_KEY);
        
        String newEvent = new String(event.getBody()) + "," + value + "," + (hostAdd == null ? "" : hostAdd);
        LOG.info(newEvent);
        event.setBody(newEvent.getBytes());
        event.getHeaders().clear();

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        for (Event event : list) {
            intercept(event);
        }
        return list;
    }

    @Override
    public void close() {
    }

    public static class Builder implements Interceptor.Builder {

        public Builder() {
        }

        @Override
        public Interceptor build() {
            return new EventInterceptor();
        }

        @Override
        public void configure(Context cntxt) {
        }
    }
}
