package com.alexshtf.interp.test;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(LinSpaceSupplier.class)
public @interface LinSpace
{
    double first();
    double last();
    int count() default 20;
}
