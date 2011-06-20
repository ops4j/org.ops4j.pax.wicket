package org.ops4j.pax.wicket.it.samples;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SampleWebUiTest extends PaxWicketIntegrationTest {

    @Configuration
    public final Option[] configureAdditionalProvision() {
        return options(
            provision(mavenBundle().groupId("org.apache.servicemix.bundles")
                .artifactId("org.apache.servicemix.bundles.aopalliance").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-aop").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-beans").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-core").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-context").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-expression").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework").artifactId("spring-asm").versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework.osgi").artifactId("spring-osgi-core")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework.osgi").artifactId("spring-osgi-io")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework.osgi").artifactId("spring-osgi-extender")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.springframework.osgi").artifactId("spring-osgi-annotation")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket").artifactId("pax-wicket-service")
                .versionAsInProject()),
            provision(mavenBundle()
                .groupId("org.ops4j.pax.wicket.samples.model")
                .artifactId("pax-wicket-samples-model-core").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.service")
                .artifactId("pax-wicket-samples-service-alternative").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.service")
                .artifactId("pax-wicket-samples-service-basic").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.view")
                .artifactId("pax-wicket-samples-view-floor").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.view")
                .artifactId("pax-wicket-samples-view-franchisee").versionAsInProject()), provision(mavenBundle()
                .groupId("org.ops4j.pax.wicket.samples.view").artifactId("pax-wicket-samples-view-about")
                .versionAsInProject()), provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.view")
                .artifactId("pax-wicket-samples-view-application").versionAsInProject()), provision(mavenBundle()
                .groupId("org.openengsb.wrapped").artifactId("net.sourceforge.htmlunit-all").versionAsInProject()));
    }

    @Test
    public void testNonSpringSampleApplication_shouldAllowBaseFunctions() throws Exception {
        WebClient webclient = new WebClient();
        HtmlPage page = webclient.getPage("http://localhost:8080/deptStore/");
        assertThatAllTabsExist(page.asText());
    }

    @Test
    public void testSpringSampleApplication_shouldAllowBaseFunctions() throws Exception {
        WebClient webclient = new WebClient();
        HtmlPage page = webclient.getPage("http://localhost:8080/deptStore/");
        assertThatAllTabsExist(page.asText());
    }

    private void assertThatAllTabsExist(String pageContent) {
        assertTrue(pageContent.contains("4th"));
        assertTrue(pageContent.contains("RoofTop"));
        assertTrue(pageContent.contains("G"));
        assertTrue(pageContent.contains("1st"));
        assertTrue(pageContent.contains("2nd"));
        assertTrue(pageContent.contains("Basement"));
        assertTrue(pageContent.contains("C"));
        assertTrue(pageContent.contains("3rd"));
        assertTrue(pageContent.contains("LG"));
    }

}
