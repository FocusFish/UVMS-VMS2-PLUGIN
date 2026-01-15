package fish.focus.uvms.plugins.vms2;

import fish.focus.uvms.plugins.vms2.rest.UvmsPluginFilter;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ArquillianSuiteDeployment
public abstract class BuildDeployment {
    @Deployment(name = "normal", order = 2)
    public static Archive<?> createDeployment() {
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "fish.focus.uvms.plugins.vms2");

        testWar.deleteClass(UvmsPluginFilter.class);

        File[] metaFiles = new File("target/classes/META-INF").listFiles();
        assertThat(metaFiles, is(notNullValue()));
        for (File metaFile : metaFiles) {
            testWar.addAsManifestResource(metaFile);
        }

        File[] webInfFiles = new File("target/classes/WEB-INF").listFiles();
        assertThat(webInfFiles, is(notNullValue()));
        for (File webInfFile : webInfFiles) {
            testWar.addAsWebInfResource(webInfFile);
        }

        testWar.addAsResource("plugin.properties", "plugin.properties");
        testWar.addAsResource("capabilities.properties", "capabilities.properties");
        testWar.addAsResource("settings.properties", "settings.properties");

        return testWar;
    }
}

