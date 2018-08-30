package com.scottlogic.deg.generator.generation.field_value_sources;

import com.scottlogic.deg.generator.restrictions.NumericLimit;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;

class IntegerFieldValueSourceTests {
    @Test
    void withSameInclusiveLowerAndUpperBounds() {
        givenLowerBound(3, true);
        givenUpperBound(3, true);

        expectAllValues(3);
    }

    @Test
    void whenBlacklistHasNoValuesInRange() {
        givenLowerBound(3, true);
        givenUpperBound(5, true);

        givenBlacklist(1);

        expectAllValues(3, 4, 5);
    }

    @Test
    void exclusiveRangesShouldExcludeTheirOwnValue() {
        givenLowerBound(3, false);
        givenUpperBound(5, false);

        expectAllValues(4);
    }

    @Test
    void whenBlacklistContainsNonIntegralValues() {
        givenLowerBound(3, true);
        givenUpperBound(5, true);

        givenBlacklist("hello", 4, new BigDecimal(5));

        expectAllValues(3);
    }

    @Test
    void whenUpperBoundNotSpecified() {
        givenLowerBound(0, true);

        expectFinite();
        expectValueCount(Integer.MAX_VALUE);
    }

    @Test
    void whenBlacklistContainsAllValuesInRange() {
        givenLowerBound(3, true);
        givenUpperBound(5, true);

        givenBlacklist(3, 4, 5);

        expectNoValues();
    }



    private NumericLimit<BigDecimal> upperLimit;
    private NumericLimit<BigDecimal> lowerLimit;
    private Set<Object> blacklist;
    private IntegerFieldValueSource objectUnderTest;

    // these take string values to avoid representing decimals as floating-points
    private void givenLowerBound(String value, boolean isInclusive) {
        lowerLimit = new NumericLimit<>(new BigDecimal(value), isInclusive);
    }

    private void givenLowerBound(int value, boolean isInclusive) {
        lowerLimit = new NumericLimit<>(new BigDecimal(value), isInclusive);
    }

    // these take string values to avoid representing decimals as floating-points
    private void givenUpperBound(String value, boolean isInclusive) {
        upperLimit = new NumericLimit<>(new BigDecimal(value), isInclusive);
    }

    private void givenUpperBound(int value, boolean isInclusive) {
        upperLimit = new NumericLimit<>(new BigDecimal(value), isInclusive);
    }

    private void givenBlacklist(Object... values) {
        blacklist = new HashSet<>(Arrays.asList(values));
    }

    private void expectAllValues(Integer... expectedValuesArray) {
        List<Integer> expectedValues = Arrays.asList(expectedValuesArray);
        List<Integer> actualValues = new ArrayList<>();

        getObjectUnderTest()
            .generateAllValues()
            .forEach(v -> actualValues.add((Integer)v));

        // ASSERT
        expectValueCount(expectedValuesArray.length);
        expectFinite();

        Assert.assertThat(actualValues, equalTo(expectedValues));
    }

    private void expectNoValues() {
        expectAllValues();
    }

    private IFieldValueSource getObjectUnderTest() {
        if (objectUnderTest == null) {
            objectUnderTest = new IntegerFieldValueSource(lowerLimit, upperLimit, blacklist);
        }

        return objectUnderTest;
    }

    private void expectFinite() {
        Assert.assertTrue(getObjectUnderTest().isFinite());
    }

    private void expectValueCount(int expectedCount) {
        Assert.assertThat(
            getObjectUnderTest().getValueCount(),
            equalTo((long)expectedCount));
    }

    @BeforeEach
    void beforeEach() {
        this.upperLimit = null;
        this.lowerLimit = null;
        this.blacklist = Collections.emptySet();
        this.objectUnderTest = null;
    }
}