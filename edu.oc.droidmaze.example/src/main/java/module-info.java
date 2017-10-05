module edu.oc.droidmaze.example {
    requires edu.oc.droidmaze.api;
    requires annotations;
    requires guice;
    requires log4j.api;

    opens edu.oc.droidmaze.example to
        edu.oc.droidmaze.loader,
        guice;

    provides edu.oc.droidmaze.api.Droid with edu.oc.droidmaze.example.ExampleDroid;
}
