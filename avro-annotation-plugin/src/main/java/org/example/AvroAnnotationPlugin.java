package org.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AvroAnnotationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Automatically apply the Avro generation plugin
        project.getPluginManager().apply("com.github.davidmc24.gradle.plugin.avro");

        // Register extension for configuration
        AvroAnnotationExtension extension = project.getExtensions().create("avroAnnotation", AvroAnnotationExtension.class);
        // Register the task and configure it from the extension
        project.getTasks().register("annotateAvroClasses", AnnotateAvroClassesTask.class, task -> {
            task.setDescription("Annotate Avro classes with custom annotations");
            task.setGroup("Avro");
            task.setInputDir(extension.getInputDir());
            task.setOutputDir(extension.getOutputDir());
            task.setSchemaFile(extension.getSchemaFile());
        });
    }
} 