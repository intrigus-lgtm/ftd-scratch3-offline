/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.intrigus.ftd;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.intrigus.ftd.exception.ScratchParseException;

public class ConversionTest {

	@Test
	public void testNullConversion() {
		assertThrows(NullPointerException.class, () -> {
			Sb3ToArduinoC.convertToArduinoC(null);
		});
	}

	private static Stream<Arguments> provideScratchTestFiles() {
		return Stream
				.of("biggertest.sb3", /* "Einparker_V1_0.sb3", */ "motor_stop_test.sb3", "when_input_test.sb3",
						"all_milestone_1_blocks.sb3")
				.map((name) -> Arguments.of(name,
						Thread.currentThread().getContextClassLoader().getResourceAsStream(name)));
	}

	@ParameterizedTest(name = "{index} {0}")
	@MethodSource("provideScratchTestFiles")
	public void testConversion(String testName, InputStream testFile) throws ScratchParseException, IOException {
		Sb3ToArduinoC.convertToArduinoC(testFile);
	}

	}
}
