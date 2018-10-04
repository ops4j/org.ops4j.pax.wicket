/**
 * Copyright OPS4J
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.it.samples.karaf;

import java.io.BufferedReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;

import org.apache.karaf.features.BootFinished;
import org.apache.wicket.protocol.http.WebApplication;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.wicket.samples.plain.simple.service.EchoService;
import org.osgi.framework.BundleContext;

import static shaded.org.apache.http.HttpHeaders.USER_AGENT;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpService;
import shaded.org.apache.http.client.HttpResponseException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class IssueTest {

    /**
     * WebApplicationFactory of the some of the applications we started. We
     * don't use these members, except for synchronizing the test. Injecting
     * them guarantees that the services are available before our test runs. The
     * timeouts are rather high for the benefit of our CI server.
     */
    protected static final String WEBUI_PORT = "8181";
    protected static final String LOG_LEVEL = "WARN";
    protected static final String SYMBOLIC_NAME_PAX_WICKET_SERVICE = "org.ops4j.pax.wicket.service";

    private static final int TIMEOUT = 120 * 1000;

    @Inject
    private BootFinished bootFinished;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private HttpService httpService;

    @Configuration
    public final Option[] configureAdditionalProvision() {

        MavenUrlReference wicketFeatureRepo = maven()
                .groupId("org.ops4j.pax.wicket").artifactId("paxwicket")
                .versionAsInProject().classifier("features").type("xml");

        MavenUrlReference paxwicketFeatureRepo = maven()
                .groupId("org.ops4j.pax.wicket").artifactId("features")
                .versionAsInProject().classifier("features").type("xml");
        MavenUrlReference karafSampleFeatureRepo = maven()
                .groupId("org.ops4j.pax.wicket.samples").artifactId("features")
                .versionAsInProject().classifier("features").type("xml");
        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features").artifactId("standard").versionAsInProject().classifier("features").type("xml");

        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf").artifactId("apache-karaf")
                .version("4.0.5").type("zip");

        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target", "exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                configureConsole().ignoreLocalConsole(), logLevel(LogLevel.ERROR),
                provision(mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").versionAsInProject().start(false)),
                features(karafStandardRepo, "scr"),
                features(karafStandardRepo, "webconsole"),
                features(wicketFeatureRepo, "wicket"),
                features(paxwicketFeatureRepo, "pax-wicket"),
                features(karafSampleFeatureRepo, "wicket-samples-issues")};

    }

    /**
     * used for manually testing put in @Test() and it will bring up a karaf
     * with all samples loaded
     *
     * @throws IOException
     */
    public void waitForever() throws IOException {

        System.in.read();
    }

    @Inject
    @Filter(value = "(pax.wicket.applicationname=issues)", timeout = TIMEOUT)
    private WebApplicationFactory<WebApplication> factoryIssue;

    @Test
    public void testIssues() throws Exception {
        //Have too sleep for a while otherwise test will run too quickly and fail before http context are published
        //TODO  Someday implement or use https://github.com/ops4j/org.ops4j.pax.web/blob/master/pax-web-itest/pax-web-itest-base/src/main/java/org/ops4j/pax/web/itest/base/AbstractControlledTestBase.java
        String page = sendGet("http://localhost:" + WEBUI_PORT + "/issues/", 0);
        assertTrue(page.contains("HomePage"));
    }

    private String sendGet(String url, Integer i) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            return response.toString();
        } catch (IOException exeception) {
            if (con.getResponseCode() == 401) {
                if (i < 5) {
                    con.disconnect();
                    Thread.sleep(1000);

                    return sendGet(url, ++i);
                } else {
                    Assert.fail("Http context never became ready before timeout");
                }
            }
        }
        throw new RuntimeException();
    }

    /**
     * Simple Echo Implementation for itest...
     */
    private final class EchoServiceImplementation implements EchoService {

        private static final long serialVersionUID = 6447679249771482700L;

        public String someEchoMethod(String toEcho) {
            return "Echo: " + toEcho;
        }
    }
}
