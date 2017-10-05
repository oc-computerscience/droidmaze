module edu.oc.droidmaze.api {
    requires annotations;

    exports edu.oc.droidmaze.api;
    exports edu.oc.droidmaze.api.exception;

    exports edu.oc.droidmaze.api.impl to edu.oc.droidmaze.loader;
    uses edu.oc.droidmaze.api.impl.ServerProvider;
}
