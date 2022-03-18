# Updating & releasing Horologist

This doc is mostly for maintainers.

Ensure your [Sonatype JIRA](https://issues.sonatype.org/login.jsp) credentials are set in your environment variables.

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=username
export ORG_GRADLE_PROJECT_mavenCentralPassword=password
```

Decrypt the signing key to release a public build.

```bash
release/signing-setup.sh '<Horologist AES key>'
gradlew clean publish --no-parallel --stacktrace
release/signing-cleanup.sh
```

The deployment then needs to be manually relased via the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories). See [Releasing Deployment from OSSRH](https://central.sonatype.org/publish/release/).

## Snapshot release

For a snapshot release, the signing key is not used. Ensure `VERSION_NAME` in [gradle.properties](https://github.com/google/horologist/blob/main/gradle.properties) has the `-SNAPSHOT` suffix or specify the version via `-PVERSION_NAME=...`.

```bash
gradlew -PVERSION_NAME=0.0.1-SNAPSHOT clean publish --no-parallel --stacktrace
```
