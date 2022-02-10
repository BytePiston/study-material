package com.sap.sptutorial.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELClassValidator implements ConstraintValidator<ValidateClassExpression, Object>{
    private ExpressionParser parser = new SpelExpressionParser();
    private Expression expression;

    @Override
    public void initialize(ValidateClassExpression constraintAnnotation) {
        expression = parser.parseExpression(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        StandardEvaluationContext evalCtxt = new StandardEvaluationContext(object);
        return (boolean) expression.getValue(evalCtxt);
    }

}
