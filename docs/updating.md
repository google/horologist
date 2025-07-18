# Updating & releasing Horologist

This doc is mostly for maintainers.

Ensure your Maven Central credentials are set in ~/.gradle/gradle.properties.
Follow https://vanniktech.github.io/gradle-maven-publish-plugin/central/#configuring-the-pom

```
mavenCentralUsername=username
mavenCentralPassword=the_password

signing.keyId=12345678
signing.password=some_password
signing.secretKeyRingFile=/Users/yourusername/.gnupg/secring.gpg
```

Publish the artifacts to staging.

```bash
./gradlew publishToMavenCentral
```

The deployment then needs to be manually released via the [Central Portal](https://central.sonatype.com/publishing/deployments).

## Snapshot release

For a snapshot release, change the version to end with -SNAPSHOT e.g. 0.8.0-SNAPSHOT.

```bash
./gradlew publishToMavenCentral
```
