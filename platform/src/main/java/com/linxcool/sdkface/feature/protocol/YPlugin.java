package com.linxcool.sdkface.feature.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件注解
 * @author huchanghai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface YPlugin {

	/**
	 * 加载策略
	 * @author huchanghai
	 */
	public enum Policy {
		/** 默认使用上次配置，首次等待服务返回 */
		LAZY,

		/** 强制立即加载 */
		FORCE,

		/** 实时远程控制 */
		REMOTE,

		/** 触发器策略，当本地存在触发情况时加载 */
		TRIGGER,
	}

	/**
	 * 启动依赖入口（Application or Activity）
	 */
	public enum Entrance {
		/** 只要是Context类型均支持 */
		CONTEXT,

		/** 只支持Activity类型 */
		ACTIVITY,

		/** 只支持Application类型 */
		APPLICATION,
	}

	/**
	 * 加载策略
	 * @return
     */
	Policy strategy() default Policy.LAZY;

	/**
	 * 启动依赖关系，有的插件必须依赖Activity，有的则可以任意Context
	 * @return
     */
	Entrance entrance() default Entrance.CONTEXT;


}
