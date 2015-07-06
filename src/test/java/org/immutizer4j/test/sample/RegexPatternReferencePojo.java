package org.immutizer4j.test.sample;

import lombok.Value;

import java.util.regex.Pattern;

/**
 * A POJO with a regex matcher object, it caused a StackOverflowError first time
 * Used for testing that it does not happen any more
 */
@Value
public class RegexPatternReferencePojo {
    private Pattern pattern = Pattern.compile("([A-Z])+");
}
