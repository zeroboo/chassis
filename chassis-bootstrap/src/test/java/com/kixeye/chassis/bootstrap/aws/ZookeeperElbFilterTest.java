package com.kixeye.chassis.bootstrap.aws;

import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for ZookeeperElbFilter
 *
 * @author dturner@kixeye.com
 */
public class ZookeeperElbFilterTest {

    private static final String ENVIRONMENT = "unittest";

    private ZookeeperElbFilter filter = new ZookeeperElbFilter(ENVIRONMENT);

    @Test
    public void noMatchingEnvironment() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        loadBalancer.setLoadBalancerName("foo-Zookeeper-whatever");
        Assert.assertFalse(filter.accept(loadBalancer));
    }

    @Test
    public void matchingEnvironmentNoMatchingName() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        loadBalancer.setLoadBalancerName(ENVIRONMENT + "-Asgard-whatever");
        Assert.assertFalse(filter.accept(loadBalancer));
    }

    @Test
    public void foundCloudformationZookeeper() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        List<ListenerDescription> listenerDescriptions = new ArrayList<>();
        listenerDescriptions.add(new ListenerDescription());
        loadBalancer.setListenerDescriptions(listenerDescriptions);
        loadBalancer.setLoadBalancerName(ENVIRONMENT + "-Zookeeper-whatever");
        Assert.assertTrue(filter.accept(loadBalancer));
    }

    @Test
    public void foundInternalExhibitor() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        List<ListenerDescription> listenerDescriptions = new ArrayList<>();
        listenerDescriptions.add(new ListenerDescription());
        loadBalancer.setListenerDescriptions(listenerDescriptions);
        loadBalancer.setLoadBalancerName("exhibitor-" + ENVIRONMENT + "-internal");
        Assert.assertTrue(filter.accept(loadBalancer));
    }

    @Test
    public void externalExhibitor() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        List<ListenerDescription> listenerDescriptions = new ArrayList<>();
        listenerDescriptions.add(new ListenerDescription());
        loadBalancer.setListenerDescriptions(listenerDescriptions);
        loadBalancer.setLoadBalancerName("exhibitor-" + ENVIRONMENT);
        Assert.assertFalse(filter.accept(loadBalancer));
    }

    @Test
    public void randomUnmatchedELB() {
        LoadBalancerDescription loadBalancer = new LoadBalancerDescription();
        List<ListenerDescription> listenerDescriptions = new ArrayList<>();
        listenerDescriptions.add(new ListenerDescription());
        loadBalancer.setListenerDescriptions(listenerDescriptions);
        loadBalancer.setLoadBalancerName(RandomStringUtils.random(5,"abcd"));
        Assert.assertFalse(filter.accept(loadBalancer));
    }
}
