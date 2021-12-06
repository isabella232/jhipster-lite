package tech.jhipster.light.generator.project.infrastructure.secondary;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static tech.jhipster.light.TestUtils.*;
import static tech.jhipster.light.common.domain.FileUtils.getPath;
import static tech.jhipster.light.generator.project.domain.Constants.MAIN_RESOURCES;
import static tech.jhipster.light.generator.project.domain.Constants.TEST_TEMPLATE_RESOURCES;

import com.github.mustachejava.MustacheNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.jhipster.light.UnitTest;
import tech.jhipster.light.common.domain.FileUtils;
import tech.jhipster.light.error.domain.GeneratorException;
import tech.jhipster.light.error.domain.MissingMandatoryValueException;
import tech.jhipster.light.generator.project.domain.Project;

@UnitTest
@ExtendWith(SpringExtension.class)
class ProjectLocalRepositoryTest {

  @InjectMocks
  ProjectLocalRepository repository;

  @Test
  void shouldCreate() {
    Project project = tmpProject();

    repository.create(project);

    assertFileExist(project.getFolder());
  }

  @Test
  void shouldNotCreate() {
    Project project = tmpProject();

    try (MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class)) {
      fileUtils.when(() -> FileUtils.createFolder(anyString())).thenThrow(new IOException());

      assertThatThrownBy(() -> repository.create(project)).isExactlyInstanceOf(GeneratorException.class);
    }
  }

  @Test
  void shouldAdd() {
    Project project = tmpProject();

    repository.add(project, "mustache", "README.txt");

    assertFileExist(project, "README.txt");
  }

  @Test
  void shouldNotAdd() {
    Project project = tmpProject();
    String randomString = UUID.randomUUID().toString();

    assertThatThrownBy(() -> repository.add(project, "common", randomString)).isInstanceOf(GeneratorException.class);
  }

  @Test
  void shouldNotAddWhenErrorOnCopy() {
    Project project = tmpProject();

    try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
      files.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(CopyOption.class))).thenThrow(new IOException());

      assertThatThrownBy(() -> repository.add(project, "mustache", "README.txt")).isInstanceOf(GeneratorException.class);
    }
  }

  @Test
  void shouldAddWithDestination() {
    Project project = tmpProject();

    repository.add(project, "mustache", "README.txt", getPath(MAIN_RESOURCES));

    assertFileExist(project, MAIN_RESOURCES, "README.txt");
  }

  @Test
  void shouldAddWithDestinationAndDestinationFilename() {
    Project project = tmpProject();

    repository.add(project, "mustache", "README.txt", getPath(MAIN_RESOURCES), "FINAL-README.txt");

    assertFileExist(project, MAIN_RESOURCES, "FINAL-README.txt");
  }

  @Test
  void shouldTemplate() {
    Project project = tmpProject();

    repository.template(project, "mustache", "README.md");

    assertFileExist(project, "README.md");
  }

  @Test
  void shouldNotTemplate() {
    Project project = Project.builder().folder(FileUtils.tmpDirForTest()).build();

    try (MockedStatic<MustacheUtils> mustacheUtils = Mockito.mockStatic(MustacheUtils.class)) {
      mustacheUtils.when(() -> MustacheUtils.template(anyString(), any())).thenThrow(new IOException());

      assertThatThrownBy(() -> repository.template(project, "mustache", "README.md")).isExactlyInstanceOf(GeneratorException.class);
    }
  }

  @Test
  void shouldNotTemplateWithNonExistingFile() {
    Project project = tmpProject();

    assertThatThrownBy(() -> repository.template(project, "mustache", "README.md.wrong.mustache"))
      .isInstanceOf(MustacheNotFoundException.class);
  }

  @Test
  void shouldTemplateWithExtension() {
    Project project = tmpProject();

    repository.template(project, "mustache", "README.md.mustache");

    assertFileExist(project, "README.md");
  }

  @Test
  void shouldTemplateWithDestination() {
    Project project = tmpProject();

    repository.template(project, "mustache", "README.md.mustache", getPath(MAIN_RESOURCES));

    assertFileExist(project, "src/main/resources/README.md");
  }

  @Test
  void shouldTemplateWithDestinationAndDestinationFilename() {
    Project project = tmpProject();

    repository.template(project, "mustache", "README.md.mustache", getPath(MAIN_RESOURCES), "FINAL-README.md");

    assertFileExist(project, MAIN_RESOURCES, "FINAL-README.md");
  }

  @Test
  void shouldReplaceText() {
    Project project = tmpProjectWithPomXml();
    String oldText = """
      <name>jhipster</name>
        <description>JHipster Project</description>""";
    String newText = """
      <name>chips</name>

        <description>Chips Project</description>""";

    repository.replaceText(project, "", "pom.xml", oldText, newText);

    assertFileContent(project, "pom.xml", List.of("<name>chips</name>", "", "<description>Chips Project</description>"));
  }

  @Test
  void shouldNotReplaceText() {
    Project project = tmpProject();
    String oldText = """
      <name>jhipster</name>
        <description>JHipster Project</description>""";
    String newText = """
      <name>chips</name>
        <description>Chips Project</description>""";

    assertThatThrownBy(() -> repository.replaceText(project, "", "pom.xml", oldText, newText))
      .isExactlyInstanceOf(GeneratorException.class);
  }

  @Test
  void shouldWrite() {
    Project project = tmpProject();

    repository.write(project, "hello world", "hello", "hello.world");

    assertFileExist(project, "hello/hello.world");
    assertFileContent(project, "hello/hello.world", "hello world");
  }

  @Test
  void shouldNotWriteWhenDestinationCantBeCreated() {
    Project project = tmpProject();
    repository.write(project, "hello world", ".", "hello");

    assertThatThrownBy(() -> repository.write(project, "another hello world", "hello", "hello.world"))
      .isExactlyInstanceOf(GeneratorException.class);
  }

  @Test
  void shouldNotWriteNullText() {
    Project project = tmpProject();

    assertThatThrownBy(() -> repository.write(project, null, null, null))
      .isExactlyInstanceOf(MissingMandatoryValueException.class)
      .hasMessageContaining("text");
  }

  @Test
  void shouldGitInit() {
    Project project = tmpProject();

    repository.gitInit(project);

    assertFileExist(project, ".git/config");
  }

  @Test
  void shouldNotGitInit() {
    Project project = tmpProject();

    try (MockedStatic<GitUtils> gitUtils = Mockito.mockStatic(GitUtils.class)) {
      gitUtils.when(() -> GitUtils.init(anyString())).thenThrow(new InvalidConfigurationException("error"));

      assertThatThrownBy(() -> repository.gitInit(project)).isExactlyInstanceOf(GeneratorException.class);
      assertFileNotExist(project, ".git/config");
    }
  }

  @Test
  void shouldInitThenAddAndCommit() throws Exception {
    Project project = tmpProject();

    repository.gitInit(project);
    File file = File.createTempFile("hello", ".world", new File(project.getFolder()));
    repository.gitAddAndCommit(project, "1st commit");

    assertFileExist(project, ".git");
    assertFileExist(project, file.getName());
  }

  @Test
  void shouldNotAddAndCommit() {
    Project project = tmpProject();

    try (MockedStatic<GitUtils> gitUtils = Mockito.mockStatic(GitUtils.class)) {
      gitUtils.when(() -> GitUtils.addAndCommit(anyString(), anyString())).thenThrow(new InvalidConfigurationException("error"));

      assertThatThrownBy(() -> repository.gitAddAndCommit(project, "1st commit")).isExactlyInstanceOf(GeneratorException.class);
      assertFileNotExist(project, ".git/config");
    }
  }

  @Test
  void shouldGitApplyPatch() {
    Project project = tmpProject();

    repository.gitInit(project);
    repository.gitApplyPatch(project, getPath(TEST_TEMPLATE_RESOURCES, "utils", "example.patch"));

    assertFileExist(project, "example.md");
  }

  @Test
  void shouldNotApplyPatch() {
    Project project = tmpProject();

    try (MockedStatic<GitUtils> gitUtils = Mockito.mockStatic(GitUtils.class)) {
      gitUtils.when(() -> GitUtils.apply(anyString(), anyString())).thenThrow(new InvalidConfigurationException("error"));

      assertThatThrownBy(() -> repository.gitApplyPatch(project, getPath(TEST_TEMPLATE_RESOURCES, "utils", "example.patch")))
        .isExactlyInstanceOf(GeneratorException.class);
      assertFileNotExist(project, "example.md");
    }
  }
}