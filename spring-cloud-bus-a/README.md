# 细聊Spring Cloud Bus

## Spring 事件驱动模型

因为Spring Cloud Bus的运行机制也是Spring事件驱动模型所以需要先了解相关知识点：
![Alt text](http://static.bluersw.com/images/spring-cloud-bus/spring-cloud-bus-03.png)  
上面图中是Spring事件驱动模型的实现示意图，其中抽象类abstract class AbstractApplicationEventMulticaster中根据事件和事件类型获取所有观察者的方法是：

```java
	protected Collection<ApplicationListener<?>> getApplicationListeners(
			ApplicationEvent event, ResolvableType eventType)  
```

该方法内具体检索监听器（观察者的方法）是：

```java
private Collection<ApplicationListener<?>> retrieveApplicationListeners(
            ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever)
            
            .....
        // Add programmatically registered listeners, including ones coming
		// from ApplicationListenerDetector (singleton beans and inner beans).
		for (ApplicationListener<?> listener : listeners) {
			if (supportsEvent(listener, eventType, sourceType)) {
				if (retriever != null) {
					retriever.applicationListeners.add(listener);
				}
				allListeners.add(listener);
			}
		}
            .....
```

此方法内根据参数事件对象遍历所有监听者的时候有个很重要的方法boolean supportsEvent，此方法判断是否是传入事件对象的监听器：

```java
	protected boolean supportsEvent(
			ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {

		GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
				(GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
		return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
	}
```

其中接口GenericApplicationListener和GenericApplicationListenerAdapter类都是为了定义supportsEventType和supportsSourceType方法，通过这两个方法确定是否是事件的监听器（观察者）。

```java
public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * Determine whether this listener actually supports the given event type.
	 * @param eventType the event type (never {@code null})
	 */
	boolean supportsEventType(ResolvableType eventType);

	/**
	 * Determine whether this listener actually supports the given source type.
	 * <p>The default implementation always returns {@code true}.
	 * @param sourceType the source type, or {@code null} if no source
	 */
	default boolean supportsSourceType(@Nullable Class<?> sourceType) {
		return true;
	}

	/**
	 * Determine this listener's order in a set of listeners for the same event.
	 * <p>The default implementation returns {@link #LOWEST_PRECEDENCE}.
	 */
	@Override
	default int getOrder() {
		return LOWEST_PRECEDENCE;
	}

}
```

其中判断发布事件的来源对象supportsSourceType方法默认就返回true，意味着如果不重写这个接口方法，监听器不判断事件来源，只根据事件类型进行筛选,比如GenericApplicationListenerAdapter类包装的supportsSourceType方法实现：

```java
public boolean supportsSourceType(@Nullable Class<?> sourceType) {
		return !(this.delegate instanceof SmartApplicationListener) ||
				((SmartApplicationListener) this.delegate).supportsSourceType(sourceType);
	}
```
