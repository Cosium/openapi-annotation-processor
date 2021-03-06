[![Maven Central Latest](https://img.shields.io/maven-central/v/com.cosium.openapi/openapi-annotation-processor.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.openapi%22%20AND%20a%3A%22openapi-annotation-processor%22)

# OpenAPI Annotation Processor
The OpenAPI annotation processor parses Java source code and generates OpenAPI specification and optionally related code.

It currently supports `Spring MVC` and `Swagger 2.0`.

### Why an annotation processor instead of a maven plugin?
Because it is natively supported by `javac`. Therefore, it will make your development workflow smoother. 

### How to use it
Add the maven dependency:
```xml
<dependency>
    <groupId>com.cosium.openapi</groupId>
    <artifactId>openapi-annotation-processor</artifactId>
    <version>1.25</version>
    <scope>provided</scope>
</dependency>
```
Options can be passed to the annotation processor like this:
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
		<compilerArguments>
    		<Acom.cosium.openapi.code_generator.languages>typescript-angular</Acom.cosium.openapi.code_generator.languages>
		</compilerArguments>
	</configuration>
</plugin>
```
By default, the processor will only generate specification in yaml and json formats.

You have to provide `languages` option to enable code generation.

All generated files will be available in the classes output folder.

### Options
##### Generation package
- Key: `com.cosium.openapi.generation_package`
- Default value: `com.cosium.openapi.generated`
- Description: The package where generated files will be written
##### Specification title
- Key: `com.cosium.openapi.specification_generator.title`
- Default value: ``
- Description: The OpenAPI title
##### Specification base path
- Key: `com.cosium.openapi.specification_generator.base_path`
- Default value: `/`
- Description: The OpenAPI base path
##### Specification global produces
- Key: `com.cosium.openapi.specification_generator.produces`
- Default value: `application/json`
- Description: The OpenAPI global produces mime type
##### Specification global consumes
- Key: `com.cosium.openapi.specification_generator.consumes`
- Default value: `application/json`
- Description: The OpenAPI global consumes mime type
##### Code generated languages
- Key: `com.cosium.openapi.code_generator.languages`
- Example of value: `typescript-angular,typescript-angular2`
- Description: The languages to use to generate OpenAPI related code
##### One code generation folder per language
- Key: `com.cosium.openapi.code_generator.one_generation_folder_per_language`
- Default: `true`
- Description: True if each language code should be written in a separate folder named as the language. False to put all languages in the same folder.

### Architecture
The processor calls the following components:
1. One or more path parser
2. One specification generator
3. One code generator

##### Path parser
`PathParser` takes Java source code as input and returns a list of Swagger 2.0 API paths.

The following parsers are available out of the box:
- `SpringParser` which parses your Spring MVC controllers

More parsers are expected to be added over time (JAX-RS, Servlet, ...)

##### Specification generator
`SpecificationGenerator` takes a list of Swagger 2.0 API paths as input and generates a full Swagger 2.0 specification.

`DefaultSpecificationGenerator` takes care of this task. 
This generator should remain the only implementation until Swagger 3.0 specification.

##### Code generator
`CodeGenerator` takes a full Swagger 2.0 specification as input and generates related code.

`DefaultCodeGenerator` takes care of this task. It uses [swagger-codegen](https://github.com/swagger-api/swagger-codegen).
This generator should remain the only implementation until Swagger 3.0 specification.
