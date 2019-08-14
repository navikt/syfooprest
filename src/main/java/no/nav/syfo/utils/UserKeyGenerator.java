package no.nav.syfo.utils;

import no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.Integer.toHexString;
import static java.lang.reflect.Proxy.getInvocationHandler;
import static java.lang.reflect.Proxy.isProxyClass;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.springframework.aop.framework.AopProxyUtils.proxiedUserInterfaces;

public class UserKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = toHexString(super.generate(target, method, params).hashCode());
        return "user: " + getUser() + " cachekey: " + getTargetClassName(target) + "." + method.getName() + "[" + cacheKey + "]";
    }

    private String getUser() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

    private String getTargetClassName(Object target) {
        if (isProxyClass(target.getClass())) {
            InvocationHandler invocationHandler = getInvocationHandler(target);
            if (invocationHandler instanceof InstanceSwitcher) {
                return ((InstanceSwitcher) invocationHandler).getTargetClassName();
            } else {
                return proxiedUserInterfaces(target)[0].getName();
            }
        }
        return target.getClass().getName();
    }
}
