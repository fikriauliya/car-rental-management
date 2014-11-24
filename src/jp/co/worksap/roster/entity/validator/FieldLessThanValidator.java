package jp.co.worksap.roster.entity.validator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.reflect.FieldUtils;

public class FieldLessThanValidator implements ConstraintValidator<FieldLessThan, Object>
{
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldLessThan constraintAnnotation)
    {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try
        {
            final Comparable<Object> firstObj = (Comparable<Object>) FieldUtils.readField(value, firstFieldName, true);
            final Comparable<Object> secondObj = (Comparable<Object>) FieldUtils.readField(value, secondFieldName, true);

            return firstObj.compareTo(secondObj) == -1;
        }
        catch (final Exception ignore)
        {
        	return false;
        }
    }
}
