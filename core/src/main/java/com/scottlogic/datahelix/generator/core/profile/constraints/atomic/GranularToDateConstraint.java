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

package com.scottlogic.datahelix.generator.core.profile.constraints.atomic;

import com.scottlogic.datahelix.generator.common.ValidationException;
import com.scottlogic.datahelix.generator.common.profile.DateTimeGranularity;
import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpec;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpecFactory;

import java.util.Objects;

import static com.scottlogic.datahelix.generator.core.restrictions.linear.LinearRestrictionsFactory.createDateTimeRestrictions;
import static com.scottlogic.datahelix.generator.core.utils.GeneratorDefaults.DATETIME_MAX_LIMIT;
import static com.scottlogic.datahelix.generator.core.utils.GeneratorDefaults.DATETIME_MIN_LIMIT;

public class GranularToDateConstraint implements AtomicConstraint {
    public final Field field;
    public final DateTimeGranularity granularity;

    public GranularToDateConstraint(Field field, DateTimeGranularity granularity) {
        if(field == null)
            throw new IllegalArgumentException("field must not be null");
        if(granularity == null)
            throw new IllegalArgumentException("granularity must not be null");

        this.granularity = granularity;
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public AtomicConstraint negate() {
        throw new ValidationException("DateTime Granularity cannot be negated or used in if statements");
    }

    @Override
    public FieldSpec toFieldSpec() {
        return FieldSpecFactory.fromRestriction(createDateTimeRestrictions(DATETIME_MIN_LIMIT, DATETIME_MAX_LIMIT, granularity));
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GranularToDateConstraint constraint = (GranularToDateConstraint) o;
        return (field.equals(constraint.field) && Objects.equals(granularity, constraint.granularity));
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, granularity);
    }

    @Override
    public String toString() {
        return String.format("%s granular to %s", field.getName(), granularity);
    }
}
