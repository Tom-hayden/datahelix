package com.scottlogic.deg.generator.generation.field_value_sources;

import com.scottlogic.deg.generator.restrictions.NumericLimit;
import com.scottlogic.deg.generator.utils.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerFieldValueSource implements IFieldValueSource {
    private final Integer inclusiveLower;
    private final Integer exclusiveUpper;
    private final Set<Integer> blacklist;

    public IntegerFieldValueSource(
        NumericLimit<BigDecimal> lowerLimit,
        NumericLimit<BigDecimal> upperLimit,
        Set<Object> blacklist) {

        this.inclusiveLower = getInclusiveLower(lowerLimit);
        this.exclusiveUpper = getExclusiveUpper(upperLimit);

        this.blacklist =
            blacklist.stream()
                .filter(v -> v instanceof Number)
                .map(v -> {
                    if (v instanceof BigDecimal) {
                        BigDecimal bd = (BigDecimal) v;
                        if (!NumberUtils.isInteger(bd))
                            return null;

                        return bd.intValue();
                    }
                    else if (v instanceof Integer) {
                        return (Integer)v;
                    }
                    else return null;
                })
                .filter(Objects::nonNull)
                .filter(i -> {
                    if (i < inclusiveLower)
                        return false;

                    if (i >= exclusiveUpper)
                        return false;

                    return true;
                })
                .collect(Collectors.toSet());
    }

    private static Integer getInclusiveLower(NumericLimit<BigDecimal> lowerLimit) {
        if (lowerLimit == null)
            return Integer.MIN_VALUE;

        NumericLimit<Integer> lowerLimitAsInteger = NumericLimit.bigDecimalToInteger(lowerLimit);

        return lowerLimitAsInteger.isInclusive()
            ? lowerLimitAsInteger.getLimit()
            : lowerLimitAsInteger.getLimit() + 1;
    }

    private static Integer getExclusiveUpper(NumericLimit<BigDecimal> upperLimit) {
        if (upperLimit == null)
            return Integer.MAX_VALUE;

        NumericLimit<Integer> upperLimitAsInteger = NumericLimit.bigDecimalToInteger(upperLimit);

        return upperLimitAsInteger.isInclusive()
            ? upperLimitAsInteger.getLimit() + 1
            : upperLimitAsInteger.getLimit();
    }

    @Override
    public boolean isFinite() {
        return true;
    }

    @Override
    public long getValueCount() {
        if (!isFinite())
            throw new IllegalStateException();

        return (long)exclusiveUpper - inclusiveLower - blacklist.size(); // eg, if 3 <= X < 5, there are two values: [3, 4]
    }

    @Override
    public Iterable<Object> generateAllValues() {
        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(
                IntStream
                    .range(
                        inclusiveLower,
                        exclusiveUpper)
                    .filter(i -> !blacklist.contains(i))
                    .iterator(),
                i -> !blacklist.contains(i)));
    }

    @Override
    public Iterable<Object> generateRandomValues(IRandomNumberGenerator randomNumberGenerator) {
        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(
                new SupplierBasedIterator<>(() ->
                    randomNumberGenerator.nextInt(
                        inclusiveLower,
                        exclusiveUpper)),
                i -> !blacklist.contains(i)));
    }
}
