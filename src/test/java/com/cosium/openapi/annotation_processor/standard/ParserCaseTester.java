package com.cosium.openapi.annotation_processor.standard;

import com.cosium.openapi.annotation_processor.OpenAPIProcessor;
import com.google.common.io.ByteSource;
import com.google.common.truth.Truth;
import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParserCaseTester {

	private static final Logger LOG = LoggerFactory.getLogger(ParserCaseTester.class);

	private static final JavaFileManager.Location LOCATION = StandardLocation.CLASS_OUTPUT;

	private final String parserName;
	private final String caseName;

	private final Path optionsPath;
	private final Path inputPath;
	private final Path expectedPath;

	public ParserCaseTester(String parserName, Path path) {
		requireNonNull(parserName);
		requireNonNull(path);

		this.parserName = parserName;
		this.caseName = path.getFileName().toString();

		this.optionsPath = path.resolve("options.properties");
		this.inputPath = path.resolve("input");
		this.expectedPath = path.resolve("expected");
	}

	public void test() {
		try {
			doTest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void doTest() throws Exception {
		LOG.info("Running test '{}' for parser '{}'", caseName, parserName);

		List<JavaFileObject> inputs = subFileObjetcs(inputPath);
		LOG.debug("Using inputs {}", inputs);

		List<String> options = parseOptions();
		LOG.debug("Using options {}", options);

		CompileTester.SuccessfulCompilationClause clause = Truth.assert_()
				.about(JavaSourcesSubjectFactory.javaSources())
				.that(subFileObjetcs(inputPath))
				.withCompilerOptions(options)
				.processedWith(new OpenAPIProcessor())
				.compilesWithoutError();

		resources(expectedPath)
				.stream()
				.peek(resource -> LOG.debug("Validating expectation on {}", resource))
				.forEach(resource -> clause.and()
						.generatesFileNamed(LOCATION, resource.packageName, resource.relativeName)
						.withStringContents(StandardCharsets.UTF_8, resource.stringSource));
	}

	private List<String> parseOptions() {
		if (!Files.exists(optionsPath)) {
			return Collections.emptyList();
		}
		Properties properties = new Properties();
		try (InputStream inputStream = Files.newInputStream(optionsPath)) {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		List<String> options = new ArrayList<>();
		properties.forEach((key, value) -> options.add("-A" + key + "=" + value));
		return options;
	}

	private List<Resource> resources(Path path) {
		return PathUtils
				.subFiles(path)
				.stream()
				.map(subPath -> new Resource(path, subPath))
				.collect(Collectors.toList());
	}

	private List<JavaFileObject> subFileObjetcs(Path path) {
		return PathUtils
				.subFiles(path)
				.stream()
				.map(Path::toUri)
				.map(uri -> {
					try {
						return uri.toURL();
					} catch (MalformedURLException e) {
						throw new RuntimeException(e);
					}
				})
				.map(JavaFileObjects::forResource)
				.collect(Collectors.toList());
	}

	private class Resource {
		private final ByteSource byteSource;
		private final String stringSource;
		private final String packageName;
		private final String relativeName;

		private Resource(Path rootPath, Path path) {
			requireNonNull(path);

			try {
				this.stringSource = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.byteSource = com.google.common.io.Files.asByteSource(path.toFile());
			this.packageName = rootPath.relativize(path).getParent().toString();
			this.relativeName = path.getFileName().toString();
		}


		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("packageName", packageName)
					.append("relativeName", relativeName)
					.toString();
		}
	}

}
