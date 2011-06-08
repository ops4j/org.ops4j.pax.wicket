package org.ops4j.pax.wicket.internal;

import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;

public interface OverwriteProxy extends MethodInterceptor {

    Map<String, String> getOverwrites();

}
