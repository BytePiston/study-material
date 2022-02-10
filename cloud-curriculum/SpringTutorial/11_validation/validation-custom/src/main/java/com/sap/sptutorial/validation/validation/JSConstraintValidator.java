package com.sap.sptutorial.validation.validation;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.script.CompiledScript;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@SuppressWarnings("restriction")
public class JSConstraintValidator implements ConstraintValidator<JSValidated, Object> {

	private String function;
	private ScriptObjectMirror bindings;
	private String scriptFileName;

	@Autowired
	private Scripts scripts;

	@Override
	public void initialize(JSValidated constraintAnnotation) {
		function = constraintAnnotation.function();
		scriptFileName = constraintAnnotation.script();
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		CompiledScript script = scripts.getValidationScript(scriptFileName);
		try {
			bindings = (ScriptObjectMirror) script.getEngine().createBindings();
			script.eval(bindings);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		HibernateConstraintValidatorContext hibernateContext = context
				.unwrap(HibernateConstraintValidatorContext.class);

		boolean objectEvalResult = true;
		boolean fieldEvalResult = true;
		if (function.length() > 0) {
			ScriptObjectMirror functionObject = (ScriptObjectMirror) bindings.getMember(function);
			if (functionObject.isFunction()) {
				objectEvalResult = objectEvalResult && (boolean) functionObject.call(null, object);
			}
		}
		if (objectEvalResult) {
			hibernateContext.disableDefaultConstraintViolation();
		}

		Field[] fields = object.getClass().getDeclaredFields();

		fieldEvalResult = Arrays.stream(fields).map(field -> {
			if (field.isAnnotationPresent(JSValidatedField.class)) {
				JSValidatedField annotation = field.getAnnotation(JSValidatedField.class);
				try {
					ScriptObjectMirror functionObject = (ScriptObjectMirror) bindings.getMember(annotation.check());
					field.setAccessible(true);
					boolean fieldResult = (boolean) functionObject.call(null, field.get(object));
					hibernateContext.addExpressionVariable("check", annotation.check())
							.buildConstraintViolationWithTemplate(annotation.message()).addPropertyNode(field.getName())
							.addConstraintViolation();
					return fieldResult;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else {
				return true;
			}
		}).reduce((val1, val2) -> val1 && val2).get();

		return objectEvalResult && fieldEvalResult;
	}

}
