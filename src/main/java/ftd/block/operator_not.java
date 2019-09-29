package ftd.block;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import ftd.ScratchConstants;
import ftd.ScratchValue;

/**
 * Implements the scratch not operator. The actual C++ code is in operators.cpp.
 * Computes !operand. The input is converted to a boolean if necessary. The
 * returned value is a boolean.
 */
public class operator_not extends ScratchBlock {

	@JsonProperty(value = "inputs")
	private Input inputs;

	private static class Input {
		@JsonProperty(value = "OPERAND")
		public ScratchValue operand;
	}

	public String gen() {
		String operand = (inputs.operand != null ? inputs.operand.generateCode() : ScratchConstants.SCRATCH_FALSE);
		return "s_not((" + operand + "))";
	}

	@Override
	protected void updateOtherRelations(Map<String, ScratchBlock> blocks) {
		this.inputs.operand.updateRelations(blocks);
	}
}
