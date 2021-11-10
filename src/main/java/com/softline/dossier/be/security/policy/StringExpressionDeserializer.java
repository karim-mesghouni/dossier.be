package com.softline.dossier.be.security.policy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;

/**
 * converts String expression into {@link Expression} Objects
 */
public class StringExpressionDeserializer extends StdDeserializer<Expression> {
    private static final long serialVersionUID = -3756824333350261220L;

    ExpressionParser expressionParser = new SpelExpressionParser();

    public StringExpressionDeserializer() {
        this(null);
    }

    protected StringExpressionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Expression deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        String expresionString = jp.getCodec().readValue(jp, String.class);
        return expressionParser.parseExpression(expresionString);
    }
}
