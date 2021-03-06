package com.github.intrigus.ftd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.intrigus.ftd.exception.ScratchNoTopLevelHatBlockException;
import com.github.intrigus.ftd.exception.ScratchParseException;
import com.github.intrigus.ftd.exception.ScratchTooManyTopLevelHatBlocksException;
import com.github.intrigus.ftd.exception.ScratchUnimplementedException;

/**
 * The main class. Converts a sb3 file to an Arduino C++ program. Can be used
 * just like a unix command line program. Input is taken from System.in, output
 * is written to System.out.
 * <p>
 * Limitations:
 * <li>Only one hat block.</li>
 * <li>Not every operator is supported.</li>
 * <li>Numbers are floats and not doubles as in the scratch runtime.</li>
 * </p>
 *
 */
public class Sb3ToArduinoC {

	public static final String VERSION = "1.0.0";

	public static void main(String[] args) {
		Args parsedArgs = new Args();
		JCommander command = JCommander.newBuilder().addObject(parsedArgs).build();
		command.setProgramName("sb3toc");
		try {
			command.parse(args);
		} catch (ParameterException e) {
			e.usage();
			System.exit(1);
		}
		if (parsedArgs.showVersion) {
			showVersion();
		}
		if (parsedArgs.showHelp) {
			showHelp();
		}

		try {
			System.out.println(convertToArduinoC(System.in));
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		} catch (ScratchParseException e) {
			e.printStackTrace(System.err);
			System.exit(3);
		} catch (ScratchUnimplementedException e) {
			e.printStackTrace(System.err);
			System.exit(4);
		} catch (ScratchNoTopLevelHatBlockException e) {
			e.printStackTrace(System.err);
			System.exit(5);
		} catch (ScratchTooManyTopLevelHatBlocksException e) {
			e.printStackTrace(System.err);
			System.exit(6);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	private static void showHelp() {
		System.out.println("sb3toc takes input from STDIN and writes on sucess to STDOUT");
		System.out.println("Errors are written to STDERR");
		System.out.println("Exit code to error map:");
		System.out.println("1: Unkown error");
		System.out.println("2: I/O error");
		System.out.println("3: Parsing failed");
		System.out.println("4: Unimplemented Scratch feature");
		System.out.println("5: No top-level block i.e. hat found");
		System.out.println("6: More than one top-level block i.e. hat found");
	}

	private static void showVersion() {
		System.out.println("Version: " + VERSION);
	}

	private static class Args {
		@Parameter(names = { "-h", "--help", "--usage" }, help = true, description = "Display the help")
		private boolean showHelp;
		@Parameter(names = { "-v", "--version" }, description = "Display the current version")
		private boolean showVersion;
	}

	private static ObjectMapper newDefaultMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		mapper.registerModule(module);
		return mapper;
	}

	/**
	 * Expects an input stream that represents a project.json file. This
	 * project.json file contains the scratch program. This program is then
	 * converted to an Arduino C++ program.
	 * 
	 * @param is the input stream that represents the project.json file.
	 * @return the project.json file converted to an Arduino C++ program.
	 * @throws ScratchParseException if the parsing failed.
	 * @throws IOException           if the inputs stream could not be read or some
	 *                               other i/o error.
	 */
	public static String convertProjectJsonToArduinoC(InputStream is) throws IOException, ScratchParseException {
		byte[] projectJsonBytes = is.readAllBytes();

		if (projectJsonBytes == null) {
			throw new RuntimeException("The given json input is empty.");
		}
		return convertProjectJsonToArduinoC(projectJsonBytes);
	}

	/**
	 * Converts a String to an InputStream. The String is assumed to be encoded
	 * using UTF8.
	 * 
	 * @param string the String to convert to an InputStream
	 * @return the String converted to an InputStream
	 */
	private static InputStream toInputStream(String string) {
		return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * @see Sb3ToArduinoC#convertProjectJsonToArduinoC(InputStream)
	 */
	public static String convertProjectJsonToArduinoC(String code) throws IOException, ScratchParseException {
		return convertProjectJsonToArduinoC(toInputStream(code));
	}

	/**
	 * @see Sb3ToArduinoC#convertProjectJsonToArduinoC(InputStream)
	 */
	private static String convertProjectJsonToArduinoC(byte[] projectJsonBytes)
			throws IOException, ScratchParseException {
		ObjectMapper mapper = newDefaultMapper();
		ScratchSave scratchSave;
		try {
			scratchSave = mapper.readValue(projectJsonBytes, ScratchSave.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new ScratchParseException(e);
		}

		if (scratchSave == null) {
			throw new RuntimeException("Parsing succeeded, but returned a null value.");
		}

		ScratchBlocks scratchBlocks = scratchSave.getBlocks();
		scratchBlocks.init();
		String code = scratchBlocks.generateCCode();
		return code;
	}

	/**
	 * Expects an input stream that represents a single {@link ScratchTarget}. This
	 * program is then converted to an Arduino C++ program.
	 * 
	 * @param is the input stream that represents the single scratch target.
	 * @return the single scratch target converted to an Arduino C++ program.
	 * @throws ScratchParseException if the parsing failed.
	 * @throws IOException           if the inputs stream could not be read or some
	 *                               other i/o error.
	 */
	public static String convertSingleTargetJsonToArduinoC(InputStream is) throws IOException, ScratchParseException {
		byte[] projectJsonBytes = is.readAllBytes();

		if (projectJsonBytes == null) {
			throw new RuntimeException("The given json input is empty.");
		}
		return convertSingleTargetJsonToArduinoC(projectJsonBytes);
	}

	/**
	 * @see Sb3ToArduinoC#convertSingleTargetJsonToArduinoC(InputStream)
	 */
	public static String convertSingleTargetJsonToArduinoC(String code) throws IOException, ScratchParseException {
		return convertSingleTargetJsonToArduinoC(toInputStream(code));
	}

	/**
	 * @see Sb3ToArduinoC#convertSingleTargetJsonToArduinoC(InputStream)
	 */
	private static String convertSingleTargetJsonToArduinoC(byte[] projectJsonBytes)
			throws IOException, ScratchParseException {
		ObjectMapper mapper = newDefaultMapper();
		ScratchTarget singleScratchTarget;
		try {
			singleScratchTarget = mapper.readValue(projectJsonBytes, ScratchTarget.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new ScratchParseException(e);
		}

		if (singleScratchTarget == null) {
			throw new RuntimeException("Parsing succeeded, but returned a null value.");
		}

		ScratchBlocks scratchBlocks = singleScratchTarget.getBlocks();
		scratchBlocks.init();
		String code = scratchBlocks.generateCCode();
		return code;
	}

	/**
	 * Expects an input stream that represents a sb3/zip file. The file must contain
	 * a project.json file that contains the scratch program. This program is then
	 * converted to an Arduino C++ program.
	 * 
	 * @param is the input stream that represents the sb3 file.
	 * @return the sb3 file converted to an Arduino C++ program.
	 * @throws ScratchParseException if the parsing failed.
	 * @throws IOException           if the inputs stream could not be read or the
	 *                               zip is malformed or some other i/o error.
	 */
	public static String convertToArduinoC(InputStream is) throws ScratchParseException, IOException {
		Objects.requireNonNull(is);

		ZipInputStream zipStream = new ZipInputStream(is);
		byte[] projectJsonBytes = null;

		ZipEntry entry;

		while ((entry = zipStream.getNextEntry()) != null) {
			if ("project.json".equals(entry.getName())) {
				projectJsonBytes = zipStream.readAllBytes();
				break;
			} else {
				continue;
			}
		}

		if (projectJsonBytes == null) {
			throw new RuntimeException("project.json is missing from the .sb3 file.");
		}

		return convertProjectJsonToArduinoC(projectJsonBytes);
	}
}
