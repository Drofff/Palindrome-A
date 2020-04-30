package com.drofff.palindrome.context;

import android.app.Activity;
import android.util.Log;

import com.drofff.palindrome.annotation.StringResource;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.drofff.palindrome.R.array.bean_classes;
import static com.drofff.palindrome.utils.ReflectionUtils.constructInstance;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class BeanManager {

    private static final String LOG_TAG = BeanManager.class.getName();

    private final Activity contextActivity;

    public BeanManager(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    public void registerBeans() {
        Set<Class<?>> beanClasses = getBeanClasses();
        registerBeans(beanClasses);
    }

    private Set<Class<?>> getBeanClasses() {
        return getBeanClassNames().stream()
                .map(ReflectionUtils::getClassByName)
                .collect(toSet());
    }

    private List<String> getBeanClassNames() {
        String[] beanClasses = contextActivity.getResources()
                .getStringArray(bean_classes);
        return asList(beanClasses);
    }

    private void registerBeans(Set<Class<?>> beanClasses) {
        for(int i = 0; i < beanClasses.size(); i++) {
            registerBeansWithResolvableDependencies(beanClasses);
            if(resolvedAllBeans(beanClasses)) {
                break;
            }
        }
        validateResolvedAllBeans(beanClasses);
    }

    private void registerBeansWithResolvableDependencies(Set<Class<?>> beanClasses) {
        beanClasses.stream()
                .filter(this::isNotRegistered)
                .filter(this::hasResolvableDependencies)
                .forEach(this::registerBeanOfClass);
    }

    private boolean isNotRegistered(Class<?> beanClass) {
        return !BeanContext.hasBeanOfClass(beanClass);
    }

    private <T> void registerBeanOfClass(Class<T> clazz) {
        Log.d(LOG_TAG, "Registering a bean of class " + clazz.getName());
        Constructor<?> constructor = getClassConstructor(clazz);
        Object[] params = resolveConstructorParams(constructor);
        Object instance = constructInstance(constructor, params);
        registerObjectBeanAsClass(instance, clazz);
    }

    private Object[] resolveConstructorParams(Constructor<?> constructor) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Annotation[][] paramsAnnotations = constructor.getParameterAnnotations();
        return IntStream.range(0, paramTypes.length)
                .mapToObj(paramPosition -> resolveConstructorParam(paramTypes[paramPosition], paramsAnnotations[paramPosition]))
                .collect(toList())
                .toArray(new Object[] {});
    }

    private Object resolveConstructorParam(Class<?> paramType, Annotation[] annotations) {
        if(containsStringResourceAnnotation(annotations)) {
            return resolveStringResource(annotations);
        } else if(isContextActivity(paramType)) {
            return contextActivity;
        }
        return BeanContext.getBeanOfClass(paramType);
    }

    private String resolveStringResource(Annotation[] annotations) {
        int resourceId = getStringResourceId(annotations);
        return contextActivity.getResources().getString(resourceId);
    }

    private int getStringResourceId(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(this::isStringResourceAnnotation)
                .map(annotation -> (StringResource) annotation)
                .mapToInt(StringResource::id)
                .findFirst()
                .orElseThrow(() -> new PalindromeException("Missing string resource id"));
    }

    private <T> void registerObjectBeanAsClass(Object object, Class<T> clazz) {
        T bean = clazz.cast(object);
        BeanContext.registerBean(bean);
    }

    private void validateResolvedAllBeans(Set<Class<?>> beanClasses) {
        if(notResolvedAllBeans(beanClasses)) {
            Class<?> notResolvedBeanClass = getFirstNotResolvableBean(beanClasses);
            throw new PalindromeException("Can not resolve dependencies of bean " + notResolvedBeanClass.getName());
        }
    }

    private boolean notResolvedAllBeans(Set<Class<?>> beanClasses) {
        return !resolvedAllBeans(beanClasses);
    }

    private boolean resolvedAllBeans(Set<Class<?>> beanClasses) {
        return beanClasses.stream()
                .allMatch(BeanContext::hasBeanOfClass);
    }

    private Class<?> getFirstNotResolvableBean(Set<Class<?>> beanClasses) {
        return beanClasses.stream()
                .filter(this::hasNotResolvableDependencies)
                .findFirst()
                .orElseThrow(() -> new PalindromeException("Inconsistent state: all beans are resolvable"));
    }

    private boolean hasNotResolvableDependencies(Class<?> clazz) {
        return !hasResolvableDependencies(clazz);
    }

    private boolean hasResolvableDependencies(Class<?> clazz) {
        Constructor<?> constructor = getClassConstructor(clazz);
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
        return IntStream.range(0, paramTypes.length)
                .allMatch(paramPosition -> isResolvableParam(paramTypes[paramPosition], paramAnnotations[paramPosition]));
    }

    private Constructor<?> getClassConstructor(Class<?> clazz) {
        validateHasAvailableConstructor(clazz);
        return clazz.getConstructors()[0];
    }

    private void validateHasAvailableConstructor(Class<?> clazz) {
        if(isEmptyArray(clazz.getConstructors())) {
            throw new PalindromeException("Could not find any available constructors for bean " + clazz.getName());
        }
    }

    private boolean isEmptyArray(Object[] array) {
        return array.length == 0;
    }

    private boolean isResolvableParam(Class<?> paramType, Annotation[] annotations) {
        return containsStringResourceAnnotation(annotations) || isContextActivity(paramType) ||
                BeanContext.hasBeanOfClass(paramType);
    }

    private boolean containsStringResourceAnnotation(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .anyMatch(this::isStringResourceAnnotation);
    }

    private boolean isStringResourceAnnotation(Annotation annotation) {
        return annotation instanceof StringResource;
    }

    private boolean isContextActivity(Class<?> clazz) {
        return Activity.class.isAssignableFrom(clazz);
    }

}
