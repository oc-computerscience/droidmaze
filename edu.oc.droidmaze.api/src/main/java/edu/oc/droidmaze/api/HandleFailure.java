package edu.oc.droidmaze.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method which will be called when the server reports a move failure after a turn has been
 * submitted. A move failure occurs when a droid attempts to make an invalid move, such as:
 *
 * <ul>
 *     <li>{@link MoveFailure#WALL Moving into a wall}</li>
 *     <li>{@link MoveFailure#SLOT_ALREADY_OCCUPIED Moving into a slot already occupied by another droid}</li>
 *     <li>{@link MoveFailure#DISTANCE_NOT_ALLOWED Attempting to move a distance farther than allowed}</li>
 *     <li>{@link MoveFailure#NOT_A_PORTAL Attempting to move up or down through a portal which doesn't exist}</li>
 *     <li></li>
 * </ul>
 * <p>
 *
 * The method marked with this annotation will be called during the turn that the error occurred, but when a failure
 * occurs the droid forfeits its turn and must wait until the next turn to move again.
 * <p>
 * The method which is annotated with {@link HandleFailure} may optionally take a single {@link MoveFailure} parameter
 * for context on why the move failed.
 * <p>
 * The method may have any visibility level.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleFailure {}
