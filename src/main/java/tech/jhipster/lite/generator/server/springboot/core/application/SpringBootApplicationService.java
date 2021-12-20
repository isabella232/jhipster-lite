package tech.jhipster.lite.generator.server.springboot.core.application;

import org.springframework.stereotype.Service;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.server.springboot.core.domain.SpringBootService;

@Service
public class SpringBootApplicationService {

  private final SpringBootService springBootService;

  public SpringBootApplicationService(SpringBootService springBootService) {
    this.springBootService = springBootService;
  }

  public void init(Project project) {
    springBootService.init(project);
  }

  public void addSpringBootParent(Project project) {
    springBootService.addSpringBootParent(project);
  }

  public void addSpringBootDependencies(Project project) {
    springBootService.addSpringBootDependencies(project);
  }

  public void addSpringBootMavenPlugin(Project project) {
    springBootService.addSpringBootMavenPlugin(project);
  }

  public void addMainApp(Project project) {
    springBootService.addMainApp(project);
  }

  public void addApplicationProperties(Project project) {
    springBootService.addApplicationProperties(project);
  }

  public void addApplicationFastProperties(Project project) {
    springBootService.addApplicationFastProperties(project);
  }

  public void addApplicationTestProperties(Project project) {
    springBootService.addApplicationTestProperties(project);
  }

  public void addLoggingConfiguration(Project project) {
    springBootService.addLoggingConfiguration(project);
  }

  public void addLoggingTestConfiguration(Project project) {
    springBootService.addLoggingTestConfiguration(project);
  }
}