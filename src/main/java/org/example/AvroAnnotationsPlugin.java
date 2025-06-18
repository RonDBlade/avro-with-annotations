package org.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class AvroAnnotationsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Apply the Avro plugin
        project.getPluginManager().apply("com.github.davidmc24.gradle.plugin.avro");
        
        // Create a new configuration for Avro
        project.getConfigurations().create("avroDeps");
        project.getConfigurations().create("compileOnlyResolvable", config -> {
            config.setCanBeResolved(true);
            config.setCanBeConsumed(false);
            config.extendsFrom(project.getConfigurations().getByName("compileOnly"));
        });

        // Add required dependencies
        project.getDependencies().add("implementation", "org.apache.avro:avro:1.11.3");
        project.getDependencies().add("implementation", "org.jetbrains:annotations:24.1.0");
        project.getDependencies().add("implementation", "com.github.javaparser:javaparser-core:3.25.5");
        project.getDependencies().add("compileOnly", "org.jetbrains:annotations:24.1.0");
        project.getDependencies().add("annotationProcessor", "org.jetbrains:annotations:24.1.0");
        project.getDependencies().add("avroDeps", "org.jetbrains:annotations:24.1.0");

        // Configure Avro plugin
        ExtensionContainer extensions = ((ExtensionAware) project).getExtensions();
        extensions.configure("avro", avro -> {
            // Use property access for Avro configuration
            project.getExtensions().getExtraProperties().set("avro.createSetters", true);
            project.getExtensions().getExtraProperties().set("avro.fieldVisibility", "PRIVATE");
            project.getExtensions().getExtraProperties().set("avro.stringType", "String");
            project.getExtensions().getExtraProperties().set("avro.enableDecimalLogicalType", true);
        });

        // Create the processAvroClasses task
        TaskProvider<JavaExec> processAvroClasses = project.getTasks().register("processAvroClasses", JavaExec.class, task -> {
            task.dependsOn("compileJava");
            task.dependsOn("generateAvroJava");
            task.getMainClass().set("org.example.AvroClassProcessor");
            task.setClasspath(project.getConfigurations().getByName("runtimeClasspath"));
            task.args(
                project.getBuildDir() + "/generated-avro-java",
                "src/main/avro/*.avsc"
            );
        });

        // Configure source sets
        project.getExtensions().configure("sourceSets", sourceSets -> {
            SourceSetContainer sourceSetContainer = (SourceSetContainer) sourceSets;
            SourceSet mainSourceSet = sourceSetContainer.getByName("main");
            mainSourceSet.getCompileClasspath().plus(project.getConfigurations().getByName("compileOnlyResolvable"));
        });
    }
} 