package edu.oc.droidmaze.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an optional method that is called when the droid maze system is initially started up. This
 * happens after all {@link Droid}s are registered with the system, but before the maze has been initialized.
 * {@link Server#getDroids()} and {@link Server#getMessageChannel()} can safely be called, but
 * {@link Server#getMaze()} and {@link Server#commit()} cannot.
 * <p>
 * The method marked by this annotation must not require any parameters and it's result should be {@code void}, as it
 * will be ignored.
 * <p>
 * The method may have any visibility level.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InitDroid {}
