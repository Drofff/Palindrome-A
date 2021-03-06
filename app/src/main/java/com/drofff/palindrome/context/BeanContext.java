package com.drofff.palindrome.context;

import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.Bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class BeanContext {

    private static final Set<Bean> BEANS = new HashSet<>();

    private BeanContext() {}

    static <T> void registerBean(T object) {
        validateNotNull(object, "Bean should not be null");
        validateHaveNoBeanOfClass(object.getClass());
        Bean bean = new Bean(object, object.getClass());
        BEANS.add(bean);
    }

    private static void validateHaveNoBeanOfClass(Class<?> clazz) {
        if(hasBeanOfClass(clazz)) {
            throw new PalindromeException("Bean of such class is already present in the context");
        }
    }

    static boolean hasBeanOfClass(Class<?> beanClass) {
        return getBeansOfClass(beanClass).count() > 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBeanOfClass(Class<T> beanClass) {
        List<Bean> beansOfClass = getBeansOfClass(beanClass).collect(Collectors.toList());
        validateResolvedBeansOfClass(beansOfClass, beanClass);
        Bean bean = beansOfClass.get(0);
        return (T) bean.getInstance();
    }

    private static void validateResolvedBeansOfClass(List<Bean> beans, Class<?> clazz) {
        if(beans.isEmpty()) {
            throw new PalindromeException("Can not find any bean of class " + clazz.getName());
        }
        if(isNotSignleton(beans)) {
            throw new PalindromeException("More than one bean of class " + clazz.getName() + " are present in the context");
        }
    }

    private static boolean isNotSignleton(List<?> list) {
        return list.size() > 1;
    }

    private static Stream<Bean> getBeansOfClass(Class<?> clazz) {
        return BEANS.stream()
                .filter(bean -> isBeanOfClass(bean, clazz));
    }

    private static boolean isBeanOfClass(Bean bean, Class<?> clazz) {
        Class<?> beanClazz = bean.getClazz();
        return clazz.isAssignableFrom(beanClazz);
    }

}
