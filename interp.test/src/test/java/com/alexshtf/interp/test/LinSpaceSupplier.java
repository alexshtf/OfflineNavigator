package com.alexshtf.interp.test;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

import java.util.ArrayList;
import java.util.List;

public class LinSpaceSupplier extends ParameterSupplier
{
    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) throws Throwable {
        LinSpace linSpace = (LinSpace) sig.getAnnotation(LinSpace.class);

        ArrayList list = new ArrayList();
        for(double i = 0; i < linSpace.count(); i += 1) {

            double diff = linSpace.last() - linSpace.first();
            double t = i / (linSpace.count() - 1);
            double val = linSpace.first() + diff * t;

            list.add(PotentialAssignment.forValue("nums", val));
        }

        return list;
    }
}
