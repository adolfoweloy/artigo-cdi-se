package br.com.javamagazine.extension;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import br.com.javamagazine.annotation.Property;
import br.com.javamagazine.annotation.PropertyFile;

public class PropertyLoaderExtension implements Extension {

	private Map<Field, Object> fieldValues = new HashMap<>();

	public <T> void initializePropertyLoading(
			final @Observes ProcessInjectionTarget<T> target) {
		AnnotatedType<T> annotatedType = target.getAnnotatedType();

		if (annotatedType.isAnnotationPresent(PropertyFile.class)) {
			Properties properties = loadProperties(annotatedType);

			Set<AnnotatedField<? super T>> fields = annotatedType.getFields();

			for (AnnotatedField<? super T> field : fields) {
				if (field.isAnnotationPresent(Property.class)) {
					Property property = field.getAnnotation(Property.class);
					Object value = properties.get(property.value());
					fieldValues.put(field.getJavaMember(), value);
				}
			}

			final InjectionTarget<T> it = target.getInjectionTarget();
			InjectionTarget<T> wrapped = new InjectionTarget<T>() {

				@Override
				public void dispose(T instance) {
					it.dispose(instance);
				}

				@Override
				public Set<InjectionPoint> getInjectionPoints() {
					return it.getInjectionPoints();
				}

				@Override
				public T produce(CreationalContext<T> context) {
					return it.produce(context);
				}

				@Override
				public void inject(T instance, CreationalContext<T> context) {
					it.inject(instance, context);
					for (Map.Entry<Field, Object> property : fieldValues
							.entrySet()) {
						try {
							Field key = property.getKey();
							key.setAccessible(true);
							Class<?> baseType = key.getType();

							String value = property.getValue().toString();
							if (baseType == String.class) {
								key.set(instance, value);
							} else if (baseType == Integer.class) {
								key.set(instance, Integer.valueOf(value));
							} else {
								target.addDefinitionError(new InjectionException(
										"Type " + baseType + " of Field "
												+ key.getName()
												+ " not recognized yet!"));
							}

						} catch (Exception e) {
							target.addDefinitionError(new InjectionException());
						}

					}
				}

				@Override
				public void postConstruct(T instance) {
					it.postConstruct(instance);
				}

				@Override
				public void preDestroy(T instance) {
					it.preDestroy(instance);
				}
			};

			target.setInjectionTarget(wrapped);
		}
	}

	private <T> Properties loadProperties(AnnotatedType<T> annotatedType) {
		PropertyFile propertyFileAnnotation = annotatedType
				.getAnnotation(PropertyFile.class);
		String fileName = propertyFileAnnotation.value();
		InputStream inputStream = getClass()
				.getResourceAsStream("/" + fileName);
		Properties properties = new Properties();

		try {
			properties.load(inputStream);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Related property file[%s] could not be found", fileName));
		}
	}
}
