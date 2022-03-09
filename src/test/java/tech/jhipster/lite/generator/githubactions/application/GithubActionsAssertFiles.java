package tech.jhipster.lite.generator.githubactions.application;

import static tech.jhipster.lite.TestUtils.assertFileExist;

import tech.jhipster.lite.generator.project.domain.Project;

public class GithubActionsAssertFiles {

  public static void assertFilesYml(Project project) {
    assertFileExist(project, ".github/workflows/github-actions.yml");
    assertFileExist(project, ".github/actions/setup/action.yml");
  }

  public static void assertFilesGithubActions(Project project) {
    assertFilesYml(project);
  }
}