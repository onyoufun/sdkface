package com.linxcool.sdkface.feature.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法注解
 * @author huchanghai
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface YFunction {

	/**
	 * 方法名，若不是以插件名开始，则自动组装成：插件名_方法名
	 * @return
	 */
	String name();

	/**
	 * 方法别名
	 * @return
	 */
	String alias() default "";

}
