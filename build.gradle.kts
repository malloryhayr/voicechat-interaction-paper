import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    `java`
    id("io.papermc.paperweight.userdev") version "1.5.4"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("com.modrinth.minotaur") version "2.+"
    id("io.papermc.hangar-publish-plugin") version "0.0.5"
}

group = "dev.igalaxy.voicechatinteraction"
version = "1.2.3"
description = "Detect voice chat with the sculk sensor"
val minecraftVersion = "1.19.x"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.maxhenkel.de/repository/public")
}

dependencies {
    paperweight.paperDevBundle("1.19-R0.1-SNAPSHOT")
    implementation("de.maxhenkel.voicechat:voicechat-api:2.4.0")
}

tasks {
    reobfJar {
        version = "v${project.version}+$minecraftVersion"
    }

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val versions = listOf("1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4")

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("MEPADOya")
    versionName.set("v${project.version} (Paper $minecraftVersion)")
    versionNumber.set("${project.version}+$minecraftVersion")
    versionType.set("release")
    uploadFile.set(tasks.reobfJar.get().outputJar)
    gameVersions.addAll(versions)
    loaders.addAll(listOf("paper", "purpur"))
    dependencies {
        required.project("simple-voice-chat")
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        namespace("iGalaxy", "voice-chat-interaction-paper")
        channel.set("Release")
        apiKey.set(System.getenv("HANGAR_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.reobfJar.get().outputJar)
                platformVersions.set(versions)
                dependencies {
                    hangar("henkelmax", "SimpleVoiceChat") {
                        required.set(true)
                    }
                }
            }
        }
    }
}