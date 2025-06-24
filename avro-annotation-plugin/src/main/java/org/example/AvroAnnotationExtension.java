package org.example;

import java.io.File;

public class AvroAnnotationExtension {
    private File inputDir;
    private File outputDir;
    private File schemaFile;

    public File getInputDir() { return inputDir; }
    public void setInputDir(File inputDir) { this.inputDir = inputDir; }

    public File getOutputDir() { return outputDir; }
    public void setOutputDir(File outputDir) { this.outputDir = outputDir; }

    public File getSchemaFile() { return schemaFile; }
    public void setSchemaFile(File schemaFile) { this.schemaFile = schemaFile; }
} 