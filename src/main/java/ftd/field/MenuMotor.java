package ftd.field;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ftd.block.ScratchBlock;

public class MenuMotor extends ScratchField {
	private List<String> MOTOR;
	private Motor motorSpecifier;

	@JsonCreator()
	private MenuMotor(@JsonProperty(value = "MOTOR") List<String> motor) {
		if (motor.size() < 2) {
			throw new IllegalStateException("unexpected");
		}
		if (motor.get(1) != null) {
			throw new IllegalStateException("expected null");
		}
		this.MOTOR = motor;
		if (motor.get(0) != null) {
			motorSpecifier = Motor.forValue(motor.get(0));
		}
	}

	public Motor getMotorSpecifier() {
		return motorSpecifier;
	}

	@Override
	public String toString() {
		return "MenuMotor [MOTOR=" + MOTOR + ", motorSpecifier=" + motorSpecifier + "]";
	}

	@Override
	public void updateRelations(Map<String, ScratchBlock> blocks) {
	}

	public static enum Motor {

		M1, M2, M3, M4;
		private static Map<String, Motor> namesMap = new HashMap<String, Motor>(4);
		static {
			namesMap.put("M1", M1);
			namesMap.put("M2", M2);
			namesMap.put("M3", M3);
			namesMap.put("M4", M4);
		}

		@JsonCreator
		public static Motor forValue(String value) {
			String searchValue = value.toUpperCase(Locale.ROOT);
			if (!namesMap.containsKey(searchValue)) {
				throw new IllegalStateException("unknown value:" + value);
			}
			return namesMap.get(searchValue);
		}

	}

}
