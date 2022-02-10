package com.sap.sptutorial.validation.validator;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class SpELParameterValidator
        implements ConstraintValidator<ValidateParametersExpression, Object[]> {
    private ExpressionParser parser = new SpelExpressionParser();
    private Expression expression;

    @Override
    public void initialize(ValidateParametersExpression constraintAnnotation) {
        expression = parser.parseExpression(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Object[] params,
            ConstraintValidatorContext context) {
        StandardEvaluationContext evalCtxt = new StandardEvaluationContext();
        Map<String, Object> vars = IntStream.range(0, params.length).boxed()
                .collect(Collectors.toMap(i -> String.format("param%02d", i), i -> params[i]));
        evalCtxt.setVariables(vars);
        return (boolean) expression.getValue(evalCtxt);
    }

}
