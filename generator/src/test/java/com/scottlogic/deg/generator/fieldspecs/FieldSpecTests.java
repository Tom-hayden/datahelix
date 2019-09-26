/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.fieldspecs.whitelist.DistributedSet;
import com.scottlogic.deg.generator.generation.string.generators.StringGenerator;
import com.scottlogic.deg.generator.restrictions.*;
import com.scottlogic.deg.generator.restrictions.linear.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static com.scottlogic.deg.common.profile.Types.*;
import static com.scottlogic.deg.generator.utils.Defaults.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

class FieldSpecTests {

    @Test
    void equals_objTypeIsNotFieldSpec_returnsFalse() {
        FieldSpec fieldSpec = FieldSpec.empty();

        boolean result = fieldSpec.equals("Test");

        assertFalse(
            "Expected that when the other object type is not FieldSpec a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_fieldSpecHasSetRestrictionsAndOtherObjectSetRestrictionsNull_returnsFalse() {
        FieldSpec fieldSpec = FieldSpec.fromSet(DistributedSet.singleton("whitelist"));

        boolean result = fieldSpec.equals(FieldSpec.empty());

        assertFalse(
            "Expected that when the field spec has set restrictions and the other object set restrictions are null a false value should be returned but was true",
            result
        );
    }

    @Test
    void equals_fieldSpecSetRestrictionsNullAndOtherObjectHasSetRestrictions_returnsFalse() {
        FieldSpec fieldSpec = FieldSpec.empty();

        boolean result = fieldSpec.equals(
            FieldSpec.fromSet(DistributedSet.singleton("whitelist")));

        assertFalse(
            "Expected that when the field spec does not have set restrictions and the other object has set restrictions a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_fieldSpecSetRestrictionsNotNullAndOtherObjectSetRestrictionsNotNullAndSetRestrictionsAreNotEqual_returnsFalse() {
        FieldSpec fieldSpec = FieldSpec.fromSet(DistributedSet.uniform(Arrays.asList(1, 2, 3)));

        boolean result = fieldSpec.equals(
            FieldSpec.fromSet(DistributedSet.uniform(Arrays.asList(1, 2, 3, 4))));

        assertFalse(
            "Expected that when the items in the set restrictions are not equal a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_fieldSpecNumericRestrictionsNotNullAndOtherObjectNumericRestrictionsNotNullAndNumericRestrictionsAreNotEqual_returnsFalse() {
        LinearRestrictions<BigDecimal> firstFieldSpecRestrictions = LinearRestrictionsFactory.createNumericRestrictions(
            new Limit<>(new BigDecimal(1), false),
            new Limit<>(new BigDecimal(20), false));
        FieldSpec fieldSpec = FieldSpec.empty().withRestrictions(firstFieldSpecRestrictions);

        LinearRestrictions<BigDecimal> secondFieldSpecRestrictions = LinearRestrictionsFactory.createNumericRestrictions(
            new Limit<>(new BigDecimal(5), false) ,
            new Limit<>(new BigDecimal(20), false));
        boolean result = fieldSpec.equals(
            FieldSpec.empty().withRestrictions(secondFieldSpecRestrictions)
        );

        assertFalse(
            "Expected that when the numeric restriction values differ a false value is returned but was true",
            result
        );
    }

    @Test
    public void shouldCreateNewInstanceWithNumericRestrictions() {
        FieldSpec original = FieldSpec.empty();
        LinearRestrictions<BigDecimal> restrictions = mock(LinearRestrictions.class);
        FieldSpec augmentedFieldSpec = original.withRestrictions(restrictions);

        Assert.assertNotSame(original, augmentedFieldSpec);
        Assert.assertSame(augmentedFieldSpec.getRestrictions(), restrictions);
    }

    @Test
    public void shouldCreateNewInstanceWithStringRestrictions() {
        FieldSpec original = FieldSpec.empty();
        StringRestrictions restrictions = mock(StringRestrictions.class);
        FieldSpec augmentedFieldSpec = original.withRestrictions(restrictions);

        Assert.assertNotSame(original, augmentedFieldSpec);
        Assert.assertSame(augmentedFieldSpec.getRestrictions(), restrictions);
    }

    @Test
    public void shouldCreateNewInstanceWithNullRestrictions() {
        FieldSpec original = FieldSpec.empty();
        FieldSpec augmentedFieldSpec = original.withNotNull();

        Assert.assertNotSame(original, augmentedFieldSpec);
    }

    @Test
    public void shouldCreateNewInstanceWithDateTimeRestrictions() {
        FieldSpec original = FieldSpec.empty();
        LinearRestrictions<OffsetDateTime> restrictions = mock(LinearRestrictions.class);
        FieldSpec augmentedFieldSpec = original.withRestrictions(restrictions);

        Assert.assertNotSame(original, augmentedFieldSpec);
        Assert.assertSame(augmentedFieldSpec.getRestrictions(), restrictions);
    }

    @Test
    public void emptyFieldSpecsShouldBeEqualAndHaveSameHashCode() {
        FieldSpec first = FieldSpec.empty();
        FieldSpec second = FieldSpec.empty();

        Assert.assertThat(first, equalTo(second));
        Assert.assertThat(first.hashCode(), equalTo(second.hashCode()));
    }

    @Test
    public void fieldSpecsWithEqualSetRestrictionsShouldBeEqual() {
        FieldSpec a = FieldSpec.fromSet(DistributedSet.singleton("same"));
        FieldSpec b = FieldSpec.fromSet(DistributedSet.singleton("same"));

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void fieldSpecsWithUnequalSetRestrictionsShouldBeUnequal() {
        FieldSpec a = FieldSpec.fromSet(DistributedSet.singleton("not same"));
        FieldSpec b = FieldSpec.fromSet(DistributedSet.singleton("different"));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void fieldSpecsWithEqualStringRestrictionsShouldBeEqual() {
        StringRestrictions aRestrictions = new MockStringRestrictions(true);
        StringRestrictions bRestrictions = new MockStringRestrictions(true);
        FieldSpec a = FieldSpec.empty().withRestrictions(aRestrictions);
        FieldSpec b = FieldSpec.empty().withRestrictions(bRestrictions);

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void fieldSpecsWithUnequalStringRestrictionsShouldBeUnequal() {
        StringRestrictions aRestrictions = new MockStringRestrictions(false);
        StringRestrictions bRestrictions = new MockStringRestrictions(false);
        FieldSpec a = FieldSpec.empty().withRestrictions(aRestrictions);
        FieldSpec b = FieldSpec.empty().withRestrictions(bRestrictions);

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void fieldSpecsWithEqualTypeRestrictionsShouldBeEqual() {
        FieldSpec a = FieldSpec.empty();
        FieldSpec b = FieldSpec.empty();

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void fieldSpecsWithEqualNullRestrictionsShouldBeEqual() {
        FieldSpec a = FieldSpec.empty().withNotNull();
        FieldSpec b = FieldSpec.empty().withNotNull();

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void fieldSpecsWithUnequalNullRestrictionsShouldBeUnequal() {
        FieldSpec a = FieldSpec.empty().withNotNull();
        FieldSpec b = FieldSpec.nullOnly();

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    void permitsRejectsInvalidNumeric() {
        LinearRestrictions<BigDecimal> numeric = LinearRestrictionsFactory.createNumericRestrictions(new Limit<>(BigDecimal.TEN, true), NUMERIC_MAX_LIMIT);
        FieldSpec spec = FieldSpec.empty().withRestrictions(numeric);

        assertFalse(spec.permits(BigDecimal.ONE));
    }

    @Test
    void permitsRejectsInvalidDateTime() {
        LinearRestrictions<OffsetDateTime> dateTime = LinearRestrictionsFactory.createDateTimeRestrictions(DATETIME_MIN_LIMIT, DATETIME_MAX_LIMIT);
        FieldSpec spec = FieldSpec.empty().withRestrictions(dateTime);

        OffsetDateTime time = OffsetDateTime.of(100, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC);

        assertFalse(spec.permits(time.plusNanos(1_000_000)));
    }

    @Test
    void permitsRejectsInvalidString() {
        StringRestrictions string = new StringRestrictions() {
            @Override
            public Optional<StringRestrictions> intersect(StringRestrictions other) {
                return null;
            }

            @Override
            public boolean match(String x) {
                return false;
            }

            @Override
            public StringGenerator createGenerator() {
                return null;
            }
        };
        FieldSpec spec = FieldSpec.empty().withRestrictions(string);

        assertFalse(spec.permits("Anything"));
    }

    private class MockStringRestrictions implements StringRestrictions {
        private final boolean isEqual;

        MockStringRestrictions(boolean isEqual) {
            this.isEqual = isEqual;
        }

        @Override
        public boolean equals(Object o) {
            return isEqual;
        }

        @Override
        public int hashCode() {
            return 1234;
        }

        @Override
        public Optional<StringRestrictions> intersect(StringRestrictions other) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean match(String x) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public StringGenerator createGenerator() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
