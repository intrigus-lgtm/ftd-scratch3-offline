package ftd.block;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import ftd.ScratchValue;

/**
 * Implements the scratch control forever operator. It repeats the specified
 * sub-blocks forever.
 */
public class control_forever extends ScratchBlock {

	@JsonProperty(value = "inputs")
	private Input inputs;

	private static class Input {
		@JsonProperty(value = "SUBSTACK")
		public ScratchValue subStack;
	}

	public String gen() {
		if (this.next != null) {
			throw new IllegalStateException("nothing can be after forever block!");
		}
		String code = "while(1) {\n";
		if (this.inputs.subStack != null) {
			code += inputs.subStack.generateCode();
		}
		code += "}\n";
		return code;
	}

	@Override
	protected void updateOtherRelations(Map<String, ScratchBlock> blocks) {
		this.inputs.subStack.updateRelations(blocks);
	}
}