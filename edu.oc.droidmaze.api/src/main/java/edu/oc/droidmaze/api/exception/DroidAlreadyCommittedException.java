package edu.oc.droidmaze.api.exception;

import edu.oc.droidmaze.api.Server;

/**
 * Exception thrown when a {@link edu.oc.droidmaze.api.Droid} attempts to call {@link Server#commit()} more than one
 * time in a single turn.
 */
public class DroidAlreadyCommittedException extends RuntimeException {}
