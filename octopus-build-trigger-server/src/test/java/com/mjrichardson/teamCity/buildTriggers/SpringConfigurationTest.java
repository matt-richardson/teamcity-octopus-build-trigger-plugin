package com.mjrichardson.teamCity.buildTriggers;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

@Test
@ContextConfiguration(locations={"classpath:META-INF/build-server-plugin-octopus-build-trigger.xml",
                                 "classpath:test-spring-context.xml",
                                 "classpath:META-INF/plugin-model-shared-spring.xml",
                                 "file:///Users/mattr/Downloads/TeamCity/webapps/ROOT/WEB-INF/buildServerPluginsSupportWeb.xml"})
public class SpringConfigurationTest extends AbstractTestNGSpringContextTests {

//    <bean class="com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentCompleteBuildTriggerService"/>
//    <bean class="com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleaseCreatedBuildTriggerService"/>
//    <bean class="com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachineAddedBuildTriggerService"/>
//    <bean class="com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessChangedBuildTriggerService"/>
//    <bean class="com.mjrichardson.teamCity.buildTriggers.ProjectsController" init-method="register" />
//    <!--<bean class="com.mjrichardson.teamCity.buildTriggers.MetricsController" init-method="register" />-->

//    @Autowired
//    private DeploymentCompleteBuildTriggerService deploymentCompleteBuildTriggerService;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws MalformedURLException, ClassNotFoundException {
//        URLClassLoader child = new URLClassLoader (new URL[] {new URL("file://~/Downloads/TeamCity/webapps/ROOT/WEB-INF/lib/agent-upgrade.jar")}, this.getClass().getClassLoader());
//        Class classToLoad = Class.forName ("com.MyClass", true, child);
//        Method method = classToLoad.getDeclaredMethod ("myMethod");
//        Object instance = classToLoad.newInstance ();
//        Object result = method.invoke (instance);
    }

    @Test
    public void testSpringConfiguration() {
    }
}
