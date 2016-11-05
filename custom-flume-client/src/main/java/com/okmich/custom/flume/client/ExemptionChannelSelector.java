/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.custom.flume.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.channel.AbstractChannelSelector;

/**
 *
 * @author hadmin
 */
public class ExemptionChannelSelector extends AbstractChannelSelector {

    private static final String CURR_KEY = "currInt";
    private static final String DEFAULT_CHANNEL_KEY = "default";

    private static final Logger LOG = Logger.getLogger(ExemptionChannelSelector.class.getName());
    List<String> currencyList = null;

    private List<Channel> hdfsOnlyChannel;

    public ExemptionChannelSelector() {
    }

    @Override
    public List<Channel> getRequiredChannels(Event event) {
        //20160103 170058493,1.066030,1.067620,0,AUDNZD,hostname
        String curPair = new String(event.getBody()).split(",")[4];

        if (doesPairIncludeOneOf(curPair, currencyList)) {
            return getAllChannels();
        } else {
            return hdfsOnlyChannel;
        }
    }

    @Override
    public List<Channel> getOptionalChannels(Event event) {
        return new ArrayList<>();
    }

    @Override
    public void configure(Context cntxt) {
        String currencies = cntxt.getString(CURR_KEY);
        LOG.log(Level.INFO, "CURRENCT OF INTEREST ARE {0}", currencies);
        if (currencies == null) {
            throw new IllegalArgumentException("no set values for interested currency");
        }
        currencyList = Arrays.asList(currencies.split(","));

        LOG.log(Level.INFO, "currency pairs of interest: {0}", currencyList);
        //channel setting
        this.hdfsOnlyChannel = new ArrayList<>();
        String defaultChannel = cntxt.getString(DEFAULT_CHANNEL_KEY);
        this.hdfsOnlyChannel.add(getChannelNameMap().get(defaultChannel));
    }

    /**
     *
     * @param currencyPair
     * @param currentInt
     * @return
     */
    private boolean doesPairIncludeOneOf(String currencyPair, List<String> currentInt) {
        
        for (String s : currentInt) {
            if (currencyPair.contains(s)) {
                return true;
            }
        }
        return false;
    }
    
    
//    public static void main(String[] args) {
//        System.out.println(new ExemptionChannelSelector().doesPairIncludeOneOf("AUDNZD", Arrays.asList("USD", "ZAR")));
//    }
}
