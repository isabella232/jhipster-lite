package tech.jhipster.light.generator.server.springboot.core.domain;

public class SpringBoot {

  public static final String SPRING_BOOT_VERSION = "2.6.1";
  public static final String NEEDLE_APPLICATION_PROPERTIES = "# jhipster-needle-application-properties";
  public static final String NEEDLE_APPLICATION_FAST_PROPERTIES = "# jhipster-needle-application-fast-properties";
  public static final String NEEDLE_APPLICATION_TEST_PROPERTIES = "# jhipster-needle-application-test-properties";
  public static final String APPLICATION_PROPERTIES = "application.properties";
  public static final String APPLICATION_FAST_PROPERTIES = "application-fast.properties";

  private SpringBoot() {}

  public static String getVersion() {
    return SPRING_BOOT_VERSION;
  }
}