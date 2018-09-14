package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.*;
import com.scottlogic.deg.generator.generation.IStringGenerator;
import com.scottlogic.deg.generator.generation.RegexStringGenerator;
import com.scottlogic.deg.generator.utils.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FieldSpecFactory {
    public FieldSpec construct(IConstraint constraint) {
        return construct(constraint, false);
    }

    private FieldSpec construct(IConstraint constraint, boolean negate) {
        if (constraint instanceof NotConstraint) {
            return construct(((NotConstraint) constraint).negatedConstraint, !negate);
        } else if (constraint instanceof IsInSetConstraint) {
            return construct((IsInSetConstraint) constraint, negate);
        } else if (constraint instanceof IsEqualToConstantConstraint) {
            return construct((IsEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanConstantConstraint) {
            return construct((IsGreaterThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanOrEqualToConstantConstraint) {
            return construct((IsGreaterThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanConstantConstraint) {
            return construct((IsLessThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanOrEqualToConstantConstraint) {
            return construct((IsLessThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterConstantDateTimeConstraint) {
            return construct((IsAfterConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterOrEqualToConstantDateTimeConstraint) {
            return construct((IsAfterOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeConstantDateTimeConstraint) {
            return construct((IsBeforeConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeOrEqualToConstantDateTimeConstraint) {
            return construct((IsBeforeOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsGranularToConstraint) {
            return construct((IsGranularToConstraint) constraint, negate);
        } else if (constraint instanceof IsNullConstraint) {
            return constructIsNull(negate);
        } else if (constraint instanceof MatchesRegexConstraint) {
            return construct((MatchesRegexConstraint) constraint, negate);
        } else if (constraint instanceof MatchesStandardConstraint) {
            return construct((MatchesStandardConstraint) constraint, negate);
        } else if (constraint instanceof IsOfTypeConstraint) {
            return construct((IsOfTypeConstraint) constraint, negate);
        } else if (constraint instanceof FormatConstraint) {
            return construct((FormatConstraint) constraint, negate);
        } else if (constraint instanceof StringHasLengthConstraint) {
            return construct((StringHasLengthConstraint) constraint, negate);
        } else if (constraint instanceof IsStringLongerThanConstraint) {
            return construct((IsStringLongerThanConstraint) constraint, negate);
        } else if (constraint instanceof IsStringShorterThanConstraint) {
            return construct((IsStringShorterThanConstraint) constraint, negate);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private FieldSpec construct(IsEqualToConstantConstraint constraint, boolean negate) {
        return construct(
            new IsInSetConstraint(
                constraint.field,
                Collections.singleton(constraint.requiredValue)
            ),
            negate);
    }

    private FieldSpec construct(IsInSetConstraint constraint, boolean negate) {
        return createFieldSpec(fs ->
            fs.setSetRestrictions(
                negate
                    ? SetRestrictions.fromBlacklist(constraint.legalValues)
                    : SetRestrictions.fromWhitelist(constraint.legalValues)));
    }

    private FieldSpec constructIsNull(boolean negate) {
        final NullRestrictions nullRestrictions = new NullRestrictions();

        nullRestrictions.nullness = negate
            ? NullRestrictions.Nullness.MustNotBeNull
            : NullRestrictions.Nullness.MustBeNull;

        return createFieldSpec(fs -> fs.setNullRestrictions(nullRestrictions));
    }

    private FieldSpec construct(IsOfTypeConstraint constraint, boolean negate) {
        final TypeRestrictions typeRestrictions;

        if (negate) {
            typeRestrictions = TypeRestrictions.createFromBlackList(constraint.requiredType);
        } else {
            typeRestrictions = TypeRestrictions.createFromWhiteList(constraint.requiredType);
        }

        return createFieldSpec(fs -> fs.setTypeRestrictions(typeRestrictions));
    }

    private FieldSpec construct(IsGreaterThanConstantConstraint constraint, boolean negate) {
        return constructGreaterThanConstraint(constraint.referenceValue, false, negate);
    }

    private FieldSpec construct(IsGreaterThanOrEqualToConstantConstraint constraint, boolean negate) {
        return constructGreaterThanConstraint(constraint.referenceValue, true, negate);
    }

    private FieldSpec constructGreaterThanConstraint(Number limitValue, boolean inclusive, boolean negate) {
        final NumericRestrictions numericRestrictions = new NumericRestrictions();

        final BigDecimal limit = NumberUtils.coerceToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.max = new NumericLimit<>(
                limit,
                !inclusive);
        } else {
            numericRestrictions.min = new NumericLimit<>(
                limit,
                inclusive);
        }

        return createFieldSpec(fs -> fs.setNumericRestrictions(numericRestrictions));
    }

    private FieldSpec construct(IsLessThanConstantConstraint constraint, boolean negate) {
        return constructLessThanConstraint(constraint.referenceValue, false, negate);
    }

    private FieldSpec construct(IsLessThanOrEqualToConstantConstraint constraint, boolean negate) {
        return constructLessThanConstraint(constraint.referenceValue, true, negate);
    }

    private FieldSpec constructLessThanConstraint(Number limitValue, boolean inclusive, boolean negate) {
        final NumericRestrictions numericRestrictions = new NumericRestrictions();
        final BigDecimal limit = NumberUtils.coerceToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.min = new NumericLimit<>(
                limit,
                !inclusive);
        } else {
            numericRestrictions.max = new NumericLimit<>(
                limit,
                inclusive);
        }

        return createFieldSpec(fs -> fs.setNumericRestrictions(numericRestrictions));
    }

    private FieldSpec construct(IsGranularToConstraint constraint, boolean negate) {
        if (negate) {
            // TODO: Decide what to do here
            throw new UnsupportedOperationException();
        }

        return createFieldSpec(fs -> fs.setGranularityRestrictions(
            new GranularityRestrictions(constraint.granularity)));
    }

    private FieldSpec construct(IsAfterConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsAfterConstraint(constraint.referenceValue, false, negate);
    }

    private FieldSpec construct(IsAfterOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsAfterConstraint(constraint.referenceValue, true, negate);
    }

    private FieldSpec constructIsAfterConstraint(LocalDateTime limit, boolean inclusive, boolean negate) {
        final DateTimeRestrictions dateTimeRestrictions = new DateTimeRestrictions();

        if (negate) {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }

        return createFieldSpec(fs -> fs.setDateTimeRestrictions(dateTimeRestrictions));
    }

    private FieldSpec construct(IsBeforeConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsBeforeConstraint(constraint.referenceValue, false, negate);
    }

    private FieldSpec construct(IsBeforeOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsBeforeConstraint(constraint.referenceValue, true, negate);
    }

    private FieldSpec constructIsBeforeConstraint(LocalDateTime limit, boolean inclusive, boolean negate) {
        final DateTimeRestrictions dateTimeRestrictions = new DateTimeRestrictions();

        if (negate) {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }

        return createFieldSpec(fs -> fs.setDateTimeRestrictions(dateTimeRestrictions));
    }

    private FieldSpec construct(MatchesRegexConstraint constraint, boolean negate) {
        return constructPattern(constraint.regex, negate);
    }

    private FieldSpec construct(MatchesStandardConstraint constraint, boolean negate) {
        return constructGenerator(constraint.standard, negate);
    }

    private FieldSpec construct(FormatConstraint constraint, boolean negate) {
        if (negate) {
            // TODO: Decide what to do here
            throw new UnsupportedOperationException();
        }

        final FormatRestrictions formatRestrictions = new FormatRestrictions();
        formatRestrictions.formatString = constraint.format;

        return createFieldSpec(fs -> fs.setFormatRestrictions(formatRestrictions));
    }

    private FieldSpec construct(StringHasLengthConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{%s}", constraint.referenceValue));
        return constructPattern(regex, negate);
    }

    private FieldSpec construct(IsStringShorterThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{0,%d}", constraint.referenceValue + 1));
        return constructPattern(regex, negate);
    }

    private FieldSpec construct(IsStringLongerThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{%d,}", constraint.referenceValue + 1));
        return constructPattern(regex, negate);
    }

    private FieldSpec constructPattern(Pattern pattern, boolean negate) {
        return constructGenerator(new RegexStringGenerator(pattern.toString()), negate);
    }

    private FieldSpec constructGenerator(IStringGenerator generator, boolean negate) {
        final StringRestrictions stringRestrictions = new StringRestrictions();

        stringRestrictions.stringGenerator = negate
            ? generator.complement()
            : generator;

        return createFieldSpec(fs -> fs.setStringRestrictions(stringRestrictions));
    }

    private FieldSpec createFieldSpec(Consumer<FieldSpec> mutateSpec) {
        final FieldSpec fieldSpec = new FieldSpec();
        mutateSpec.accept(fieldSpec);
        return fieldSpec;
    }
}
