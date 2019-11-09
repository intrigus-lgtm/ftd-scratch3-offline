package com.github.intrigus.ftd.block;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.intrigus.ftd.ScratchConstants;
import com.github.intrigus.ftd.ScratchValue;

/**
 * Implements the scratch subtract operator. The actual C++ code is in
 * operators.cpp. Computes number1 - number2. Both inputs are converted to
 * floats if necessary. The returned value is a float.
 */
@JsonIgnoreProperties(value = { "fields" })
public class operator_subtract extends ScratchBlock {

	@JsonProperty(value = "inputs")
	private Input inputs;

	private static class Input {
		@JsonProperty(value = "NUM1")
		public ScratchValue number1;

		@JsonProperty(value = "NUM2")
		public ScratchValue number2;
	}

	public String gen() {
		String number1 = (inputs.number1 != null ? inputs.number1.generateCode() : ScratchConstants.SCRATCH_ZERO);
		String number2 = (inputs.number2 != null ? inputs.number2.generateCode() : ScratchConstants.SCRATCH_ZERO);
		return "s_subtract((" + number1 + "), (" + number2 + "))";
	}

	@Override
	protected void updateOtherRelations(Map<String, ScratchBlock> blocks) {
		this.inputs.number1.updateRelations(blocks);
		this.inputs.number2.updateRelations(blocks);
	}
}
