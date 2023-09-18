package fr.uge.sed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class StreamEditor {

	@FunctionalInterface
	interface Rule {
		// UnaryOperator<String> -> String
		Optional<String> rewrite(String value);
		
		static Rule andThen(Rule first, Rule second) {
            Objects.requireNonNull(first);
            Objects.requireNonNull(second);
            return s -> first.rewrite(s).flatMap(second::rewrite);
        }
		
		public default Rule andThen(Rule other) {
			Objects.requireNonNull(other);
            return Rule.andThen(this, other);

		} 
		
		static Rule guard(Predicate<String> condition, Rule rule) {
            Objects.requireNonNull(condition);
            Objects.requireNonNull(rule);
            return s -> condition.test(s) ? rule.rewrite(s) : Optional.of(s);
		}
	}

	private final Rule rule;
	
	public StreamEditor(Rule rule) {
		Objects.requireNonNull(rule);
		this.rule = rule;
	}

	private static Rule createRule(char rule) {
		switch(rule) {
			case('s') : return s -> Optional.of(s.replaceAll(" ", ""));
			case('u') : return s -> Optional.of(s.toUpperCase(Locale.ROOT));
			case('l') : return s -> Optional.of(s.toLowerCase(Locale.ROOT));
			case('d') : return s -> Optional.empty();
			default : {
				throw new IllegalArgumentException("The rule '"+rule+"' doesn't exist");
			}
		}	
	}
	
	/*
	  		case('i') : return s -> Optional.of(s);
			case('=') : return s -> Optional.of(s);
			case(';') : return s -> Optional.of(s);
	 */
	
	public static Rule createRules(String rules) {
		Objects.requireNonNull(rules);
		  Rule result = Optional::of;
		     
		    var pattern = Pattern.compile("i=(.*);(.*)");
		    var matcher = pattern.matcher(rules);
		    
		    
		    if(matcher.matches()) {
		    	
		    	/*
		    	for (char rule: rules.toCharArray()) {
		    		if(rule == 'i') break;
			    	result = result.andThen(createRule(rule));
		        }
		        */
	        
	            var text = matcher.group(1);
	            var conditionalRules = matcher.group(2);
	            
	            System.out.println("Apply " + conditionalRules + " on " + text);
	            
	            result = Rule.guard(s -> s.equals(text), createRules(conditionalRules));

		    } else {
		    	
			    for (char rule: rules.toCharArray()) {
			    	result = result.andThen(createRule(rule));
		        }
		    }

			return result;
	}
	
	public void rewrite(BufferedReader reader, Writer writer) throws IOException {
		Objects.requireNonNull(reader);
		Objects.requireNonNull(writer);
		
		for(var line = reader.readLine(); line != null; line = reader.readLine()) {
			var optional = rule.rewrite(line);
			if(optional.isPresent()) writer.write(optional.get() + "\n");
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
