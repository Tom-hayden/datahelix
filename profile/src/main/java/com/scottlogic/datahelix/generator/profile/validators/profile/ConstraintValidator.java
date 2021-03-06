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

package com.scottlogic.datahelix.generator.profile.validators.profile;

import com.scottlogic.datahelix.generator.common.profile.FieldType;
import com.scottlogic.datahelix.generator.common.validators.ValidationResult;
import com.scottlogic.datahelix.generator.common.validators.Validator;
import com.scottlogic.datahelix.generator.profile.dtos.FieldDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.ConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.InvalidConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.EqualToConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.GranularToConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.InSetConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.IsNullConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.integer.LongerThanConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.integer.OfLengthConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.integer.ShorterThanConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.numeric.NumericConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.temporal.TemporalConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.textual.RegexConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.grammatical.AllOfConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.grammatical.AnyOfConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.grammatical.ConditionalConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.grammatical.NotConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.relations.InMapConstraintDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.relations.RelationalConstraintDTO;
import com.scottlogic.datahelix.generator.profile.services.FieldService;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.InMapConstraintValidator;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.NotConstraintValidator;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.RelationalConstraintValidator;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.atomic.*;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.grammatical.AllOfConstraintValidator;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.grammatical.AnyOfConstraintValidator;
import com.scottlogic.datahelix.generator.profile.validators.profile.constraints.grammatical.ConditionalConstraintValidator;

import java.util.List;

public abstract class ConstraintValidator<T extends ConstraintDTO> implements Validator<T>
{
    protected final List<FieldDTO> fields;
    protected final FieldService fieldService = new FieldService();

    protected ConstraintValidator(List<FieldDTO> fields)
    {
        this.fields = fields;
    }

    protected String getErrorInfo(T constraint)
    {
        return " | Constraint: " + constraint.getType().propertyName;
    }

    protected static ValidationResult validateConstraint(ConstraintDTO dto, List<FieldDTO> fields)
    {
        ValidationResult constraintMustBeSpecified = constraintMustBeSpecified(dto);
        if (!constraintMustBeSpecified.isSuccess) return constraintMustBeSpecified;
        switch (dto.getType())
        {
            case EQUAL_TO:
                return new EqualToConstraintValidator(fields).validate((EqualToConstraintDTO) dto);
            case IN_SET:
                return new InSetConstraintValidator(fields).validate((InSetConstraintDTO) dto);
            case IN_MAP:
                return new InMapConstraintValidator(fields).validate((InMapConstraintDTO) dto);
            case IS_NULL:
                return new IsNullConstraintValidator(fields).validate((IsNullConstraintDTO)dto);
            case GRANULAR_TO:
                return new GranularToConstraintValidator(fields).validate((GranularToConstraintDTO) dto);
            case MATCHES_REGEX:
            case CONTAINS_REGEX:
                return new RegexConstraintValidator(fields).validate((RegexConstraintDTO) dto);
            case EQUAL_TO_FIELD:
            case GREATER_THAN_FIELD:
            case GREATER_THAN_OR_EQUAL_TO_FIELD:
            case LESS_THAN_FIELD:
            case LESS_THAN_OR_EQUAL_TO_FIELD:
            case AFTER_FIELD:
            case AFTER_OR_AT_FIELD:
            case BEFORE_FIELD:
            case BEFORE_OR_AT_FIELD:
                return new RelationalConstraintValidator<>(fields).validate((RelationalConstraintDTO) dto);
            case OF_LENGTH:
                return new OfLengthConstraintValidator(fields, FieldType.STRING).validate((OfLengthConstraintDTO) dto);
            case LONGER_THAN:
                return new LongerThanConstraintValidator(fields, FieldType.STRING).validate((LongerThanConstraintDTO) dto);
            case SHORTER_THAN:
                return new ShorterThanConstraintValidator(fields, FieldType.STRING).validate((ShorterThanConstraintDTO) dto);
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
                return new NumericConstraintValidator(fields, FieldType.NUMERIC).validate((NumericConstraintDTO) dto);
            case AFTER:
            case AFTER_OR_AT:
            case BEFORE:
            case BEFORE_OR_AT:
                return new TemporalConstraintValidator(fields).validate((TemporalConstraintDTO) dto);
            case NOT:
                return new NotConstraintValidator(fields).validate((NotConstraintDTO) dto);
            case ANY_OF:
                return new AnyOfConstraintValidator(fields).validate((AnyOfConstraintDTO) dto);
            case ALL_OF:
                return new AllOfConstraintValidator(fields).validate((AllOfConstraintDTO) dto);
            case IF:
                return new ConditionalConstraintValidator(fields).validate((ConditionalConstraintDTO) dto);
            case GENERATOR:
                return ValidationResult.success();
            default:
                throw new IllegalStateException("Unexpected constraint type: " + dto.getType());
        }
    }

    private static ValidationResult constraintMustBeSpecified(ConstraintDTO dto)
    {
        if(dto == null)
        {
            return  ValidationResult.failure("Constraint must not be null");
        }
        if(dto instanceof InvalidConstraintDTO)
        {
            return ValidationResult.failure("Invalid json: " + ((InvalidConstraintDTO)dto).json);
        }
        if(dto.getType() == null)
        {
            return ValidationResult.failure("Constraint type must not be null");
        }
        return ValidationResult.success();
    }

    protected FieldType getFieldType(String field) {
        return fieldService.specificFieldTypeFromString(fields.stream()
                .filter(f -> f.name.equals(field))
                .findFirst()
                .get()
                .type,
            null).getFieldType();
    }

    protected ValidationResult validateGranularity(T dto, String field, Object value)
    {
        FieldType fieldType = getFieldType(field);
        switch (fieldType)
        {
            case BOOLEAN:
                return ValidationResult.failure("Granularity " + value + " is not supported for boolean fields" + getErrorInfo(dto));
            case STRING:
                return ValidationResult.failure("Granularity " + value + " is not supported for string fields" + getErrorInfo(dto));
            case DATETIME:
                return new DateTimeGranularityValidator(getErrorInfo(dto)).validate((String) value);
        }
        return ValidationResult.success();
    }
}
