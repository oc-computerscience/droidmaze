package edu.oc.droidmaze.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks the main work method for {@link Droid}. The method is called once-per-droid-per-turn. This
 * method is where the {@link Droid} should process the turn, and this method is the only place where
 * {@link Server#commit()} can be called.
 * <p>
 * The method marked by this annotation must not require any parameters and it's result should be {@code void}, as it
 * will be ignored.
 * <p>
 * The method may have any visibility level.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleTurn {}
