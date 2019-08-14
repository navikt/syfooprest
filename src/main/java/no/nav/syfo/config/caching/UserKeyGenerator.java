package no.nav.syfo.config.caching;


import java.lang.reflect.Method;

import static java.lang.Integer.toHexString;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserKeyGenerator extends KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = toHexString(super.generate(target, method, params).hashCode());
        return "user: " + getUser() + "[" + cacheKey + "]";
    }

    private String getUser() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

}

