package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class StreamEditor {

	@FunctionalInterface
	interface Rule {
		// UnaryOperator<String> -> String
		Optional<String> rewrite(String value);
		
	}

	
	private final Rule rule;
	
	public StreamEditor(Rule rule) {
		Objects.requireNonNull(rule);
		this.rule = rule;
	}
	
	
	public static Rule createRules(String rules) {
		Objects.requireNonNull(rules);
		switch(rules) {
			case("s") :
				return ((s) -> Optional.of(s.replace(" ", "")));
			case("u") :
				return ((s) -> Optional.of(s.toUpperCase()));
			case("l") :
				return ((s) -> Optional.of(s.toLowerCase()));
			case("d") :
				return ((s) -> Optional.empty());
			default :
				throw new IllegalArgumentException("This rule doesnt exist");
		}
		
	}
	
	public void rewrite(BufferedReader reader, Writer writer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		
		for(var line = reader.readLine(); line != null; line = reader.readLine()) {
			var optional = rule.rewrite(line);
			if(optional.isPresent()) writer.write(optional.get() + "\n") ;
		}
		
	}
	
	
	public void rewrite(Path inputPath, Path outputPath) throws IOException {
		Objects.requireNonNull(inputPath);
		Objects.requireNonNull(outputPath);

		try(var writer = Files.newBufferedWriter(outputPath)) {
			try(var reader = Files.newBufferedReader(inputPath)) {
				rewrite(reader, writer);
			}
		}
	      
	}
	
}
