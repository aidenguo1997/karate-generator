package pers.jie.karate.api;

import com.intuit.karate.junit5.Karate;

public class ApiTestRunner {
    @Karate.Test
    Karate testSample() {
        return Karate.run().relativeTo(getClass());
    }
}

