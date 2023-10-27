package com.intuit.services.editprofile;

import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class EditProfileValidation {

    // Using Predicates for validating the additional user fields

    /*
     * / * A Predicate is a functional interface introduced in Java 8 that
     * represents a boolean-valued function of one argument. It essentially tests an
     * input for a particular condition and returns either true or false. The main
     * method in the Predicate interface is test(T t), which evaluates the predicate
     * on the given argument.
     * and: Logical AND of this predicate with another.
     * or: Logical OR of this predicate with another.
     * negate: Returns a predicate that represents the logical negation of this
     * predicate.
     */

    public Predicate<String> isValidString = s -> s != null && !s.trim().isEmpty();
    public Predicate<String> isValidPhoneNumber = num -> num != null && num.length() == 10;
}
