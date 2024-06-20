package fit.hutech.book.validators;

import fit.hutech.book.entities.Category;
import fit.hutech.book.validators.annotations.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidCategoryIdValidator implements {
ConstraintValidator<ValidCategoryId, Category> {
        @Override
        public boolean isValid(Category category, ConstraintValidatorContext context) {
            return category != null && category.getId() != null;
        }
}
