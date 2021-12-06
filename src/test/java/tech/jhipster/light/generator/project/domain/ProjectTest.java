package tech.jhipster.light.generator.project.domain;

import static org.assertj.core.api.Assertions.*;
import static tech.jhipster.light.TestUtils.*;
import static tech.jhipster.light.common.domain.FileUtils.getPath;
import static tech.jhipster.light.common.domain.FileUtils.tmpDirForTest;
import static tech.jhipster.light.generator.project.domain.DefaultConfig.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.jhipster.light.TestUtils;
import tech.jhipster.light.UnitTest;
import tech.jhipster.light.common.domain.FileUtils;
import tech.jhipster.light.error.domain.GeneratorException;
import tech.jhipster.light.error.domain.MissingMandatoryValueException;
import tech.jhipster.light.error.domain.UnauthorizedValueException;

@UnitTest
class ProjectTest {

  @Nested
  class Build {

    @Test
    void shouldBuildMinimalProject() {
      String folder = tmpDirForTest();
      Project project = minimalBuilder(folder).build();

      assertThat(project.getFolder()).isEqualTo(folder);
      assertThat(project.getConfig()).isEmpty();

      assertThat(project.getLanguage()).isEmpty();
      assertThat(project.getBuildTool()).isEmpty();
      assertThat(project.getServer()).isEmpty();
      assertThat(project.getClient()).isEmpty();

      assertThat(project.getDatabase()).isEmpty();
      assertThat(project.getDatabaseMigration()).isEmpty();
      assertThat(project.getCache()).isEmpty();
      assertThat(project.getSecurity()).isEmpty();
    }

    @Test
    void shouldBuildWithNullConfig() {
      String folder = tmpDirForTest();
      Project project = minimalBuilder(folder).config(null).build();

      assertThat(project.getFolder()).isEqualTo(folder);
      assertThat(project.getConfig()).isEmpty();
    }

    @Test
    void shouldBuildFullProject() {
      String folder = tmpDirForTest();
      Project project = fullBuilder(folder).config(Map.of(PROJECT_NAME, "JHipster Light")).build();

      assertThat(project.getFolder()).isEqualTo(folder);
      assertThat(project.getConfig()).isEqualTo(Map.of(PROJECT_NAME, "JHipster Light"));

      assertThat(project.getLanguage()).contains(LanguageType.JAVA);
      assertThat(project.getBuildTool()).contains(BuildToolType.MAVEN);
      assertThat(project.getServer()).contains(ServerFrameworkType.SPRING);
      assertThat(project.getClient()).contains(ClientFrameworkType.ANGULAR);

      assertThat(project.getDatabase()).contains(DatabaseType.POSTGRESQL);
      assertThat(project.getDatabaseMigration()).contains(DatabaseMigrationType.LIQUIBASE);
      assertThat(project.getCache()).contains(CacheType.EHCACHE);
      assertThat(project.getSecurity()).contains(SecurityType.JWT);
    }

    @Test
    void shouldNotBuildWithNullFolder() {
      Project.ProjectBuilder builder = Project.builder().folder(null);

      assertThatThrownBy(builder::build).isExactlyInstanceOf(MissingMandatoryValueException.class).hasMessageContaining("folder");
    }

    @Test
    void shouldNotBuildWithBlankPath() {
      Project.ProjectBuilder builder = Project.builder().folder(" ");

      assertThatThrownBy(builder::build).isExactlyInstanceOf(MissingMandatoryValueException.class).hasMessageContaining("folder");
    }
  }

  @Nested
  class GetConfig {

    @Test
    void shouldGetConfig() {
      String path = FileUtils.tmpDirForTest();
      Project project = Project.builder().folder(path).config(Map.of(PROJECT_NAME, "JHipster Light")).build();

      assertThat(project.getConfig(PROJECT_NAME)).contains("JHipster Light");

      assertThat(project.getConfig(BASE_NAME)).isEmpty();
      assertThat(project.getConfig("projectname")).isEmpty();
      assertThat(project.getConfig("test")).isEmpty();
    }
  }

  @Nested
  class AddConfig {

    @Test
    void shouldAddConfigInEmptyConfig() {
      String path = FileUtils.tmpDirForTest();
      Project project = Project.builder().folder(path).build();

      assertThat(project.getConfig("apero")).isEmpty();

      project.addConfig("apero", "beer");
      assertThat(project.getConfig("apero")).contains("beer");

      project.addConfig("apero", "chips");
      assertThat(project.getConfig("apero")).contains("beer");
    }

    @Test
    void shouldAddConfigInExistingConfig() {
      // Given
      String path = FileUtils.tmpDirForTest();
      Map<String, Object> config = new HashMap<>(Map.of(PROJECT_NAME, "JHipster Light"));
      Project project = Project.builder().folder(path).config(config).build();

      assertThat(project.getConfig(PROJECT_NAME)).contains("JHipster Light");
      assertThat(project.getConfig("apero")).isEmpty();

      // When + Then
      project.addConfig("apero", "beer");
      assertThat(project.getConfig("apero")).contains("beer");

      // When + Then
      project.addConfig(PROJECT_NAME, "chips");
      assertThat(project.getConfig(PROJECT_NAME)).contains("JHipster Light");
    }

    @Test
    void shouldNotAddConfigWithBadBaseName() {
      Project project = tmpProject();

      assertThatThrownBy(() -> project.addConfig(BASE_NAME, "jhipster with space"))
        .isExactlyInstanceOf(UnauthorizedValueException.class)
        .hasMessageContaining(BASE_NAME);
    }

    @Test
    void shouldNotAddConfigWithBadPackageName() {
      Project project = tmpProject();

      assertThatThrownBy(() -> project.addConfig(PACKAGE_NAME, "tech jhipster light"))
        .isExactlyInstanceOf(UnauthorizedValueException.class)
        .hasMessageContaining(PACKAGE_NAME);
    }

    @Test
    void shouldAddDefaultConfig() {
      String path = FileUtils.tmpDirForTest();
      Project project = Project.builder().folder(path).build();

      project.addDefaultConfig(BASE_NAME);

      assertThat(project.getConfig(BASE_NAME)).contains("jhipster");
    }

    @Test
    void shouldNotAddDefaultConfig() {
      String path = FileUtils.tmpDirForTest();
      Project project = Project.builder().folder(path).build();

      project.addDefaultConfig("apero");

      assertThat(project.getConfig("apero")).isEmpty();
    }
  }

  @Nested
  class PackageName {

    @Test
    void shouldGetPackageName() {
      Project project = tmpProject();

      project.addConfig(PACKAGE_NAME, "tech.jhipster.light");

      assertThat(project.getPackageName()).contains("tech.jhipster.light");
    }

    @Test
    void shouldNotGetPackageNameWithEmpty() {
      Project project = tmpProject();

      assertThat(project.getPackageName()).isEmpty();
    }

    @Test
    void shouldGetPackageNamePath() {
      Project project = tmpProject();

      project.addConfig(PACKAGE_NAME, "tech.jhipster.light");

      assertThat(project.getPackageNamePath()).contains(getPath("tech/jhipster/light"));
    }

    @Test
    void shouldNotGetPackageNamePathForDefault() {
      Project project = tmpProject();

      assertThat(project.getPackageNamePath()).isEmpty();
    }
  }

  @Nested
  class GetStringConfig {

    @Test
    void shouldGetStringConfig() {
      Project project = tmpProject();

      project.addConfig("apero", "beer");

      assertThat(project.getStringConfig("apero")).contains("beer");
    }

    @Test
    void shouldGetStringConfigWithEmpty() {
      Project project = tmpProject();

      assertThat(project.getStringConfig("apero")).isEmpty();
    }

    @Test
    void shouldNotGetStringConfig() {
      Project project = tmpProject();

      project.addConfig("apero", List.of("beer"));

      assertThatThrownBy(() -> project.getStringConfig("apero"))
        .isExactlyInstanceOf(UnauthorizedValueException.class)
        .hasMessageContaining("apero");
    }
  }

  @Nested
  class GetIntegerConfig {

    @Test
    void shouldGetIntegerConfig() {
      Project project = tmpProject();

      project.addConfig("serverPort", 1337);

      assertThat(project.getIntegerConfig("serverPort")).contains(1337);
    }

    @Test
    void shouldGetIntegerConfigWithEmpty() {
      Project project = tmpProject();

      assertThat(project.getIntegerConfig("serverPort")).isEmpty();
    }

    @Test
    void shouldNotGetIntegerConfig() {
      Project project = tmpProject();

      project.addConfig("serverPort", List.of(1337));

      assertThatThrownBy(() -> project.getIntegerConfig("serverPort"))
        .isExactlyInstanceOf(UnauthorizedValueException.class)
        .hasMessageContaining("serverPort");
    }
  }

  @Test
  void shouldBeMavenProject() throws Exception {
    Project project = Project.builder().folder(tmpDirForTest()).build();
    TestUtils.copyPomXml(project);

    assertThat(project.isMavenProject()).isTrue();
  }

  @Test
  void shouldNotBeMavenProjectWithGradle() {
    Project project = Project.builder().folder(tmpDirForTest()).build();

    assertThat(project.isMavenProject()).isFalse();
  }

  @Test
  void shouldNotBeMavenProjectWithoutBuildTool() {
    Project project = Project.builder().folder(tmpDirForTest()).build();

    assertThat(project.isMavenProject()).isFalse();
  }

  @Test
  void shouldBeGradleProject() throws Exception {
    Project project = Project.builder().folder(tmpDirForTest()).build();
    TestUtils.copyBuildGradle(project);

    assertThat(project.isGradleProject()).isTrue();
  }

  @Test
  void shouldNotBeGradleProjectWithMaven() {
    Project project = Project.builder().folder(tmpDirForTest()).build();

    assertThat(project.isGradleProject()).isFalse();
  }

  @Test
  void shouldNotBeGradleProjectWithoutBuildTool() {
    Project project = Project.builder().folder(tmpDirForTest()).build();

    assertThat(project.isGradleProject()).isFalse();
  }

  @Test
  void shouldCheckBuildToolWithException() {
    Project project = tmpProject();
    assertThatThrownBy(project::checkBuildTool).isExactlyInstanceOf(GeneratorException.class);
  }

  @Test
  void shouldCheckBuildToolWithMaven() {
    Project project = tmpProjectWithPomXml();
    assertThatCode(project::checkBuildTool).doesNotThrowAnyException();
  }

  @Test
  void shouldCheckBuildToolWithGradle() throws Exception {
    Project project = tmpProjectWithBuildGradle();
    assertThatCode(project::checkBuildTool).doesNotThrowAnyException();
  }

  private Project.ProjectBuilder minimalBuilder(String tmpFolder) {
    return Project.builder().folder(tmpFolder);
  }

  private Project.ProjectBuilder fullBuilder(String tmpFolder) {
    return minimalBuilder(tmpFolder)
      .language(LanguageType.JAVA)
      .buildTool(BuildToolType.MAVEN)
      .server(ServerFrameworkType.SPRING)
      .client(ClientFrameworkType.ANGULAR)
      .database(DatabaseType.POSTGRESQL)
      .databaseMigration(DatabaseMigrationType.LIQUIBASE)
      .cache(CacheType.EHCACHE)
      .security(SecurityType.JWT);
  }
}