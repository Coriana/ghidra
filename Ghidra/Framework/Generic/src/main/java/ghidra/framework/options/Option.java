/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.framework.options;

import java.beans.PropertyEditor;
import java.util.Objects;

import ghidra.util.HelpLocation;
import ghidra.util.SystemUtilities;
import utilities.util.reflection.ReflectionUtilities;

public abstract class Option {
	private final String name;
	private final Object defaultValue;
	private boolean isRegistered;
	private final String description;
	private final HelpLocation helpLocation;
	private final OptionType optionType;

	private final PropertyEditor propertyEditor;
	private String inceptionInformation;

	protected Option(String name, OptionType optionType, String description,
			HelpLocation helpLocation, Object defaultValue, boolean isRegistered,
			PropertyEditor editor) {
		this.name = name;
		this.optionType = optionType;
		this.description = description;
		this.helpLocation = helpLocation;
		this.defaultValue = defaultValue;
		this.isRegistered = isRegistered;
		this.propertyEditor = editor;
		if (!isRegistered) {
			recordInception();
		}
	}

	public abstract Object getCurrentValue();

	public abstract void doSetCurrentValue(Object value);

	public void setCurrentValue(Object value) {
		this.isRegistered = true;
		doSetCurrentValue(value);
	}

	public String getName() {
		return name;
	}

	public PropertyEditor getPropertyEditor() {
		return propertyEditor;
	}

	public HelpLocation getHelpLocation() {
		return helpLocation;
	}

	public String getDescription() {
		return description == null ? "Unregistered Option" : description;
	}

	public Object getValue(Object passedInDefaultValue) {
		Object value = getCurrentValue();
		if (value == null && defaultValue == null) {
			// Assume that both values being null is an indication that there is no value
			// to use.  Otherwise, a null current value implies that the user has cleared
			// the default value.
			return passedInDefaultValue;
		}
		return value;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void restoreDefault() {
		setCurrentValue(defaultValue);
	}

	public boolean isDefault() {
		Object value = getCurrentValue();
		return Objects.equals(value, defaultValue);
	}

	@Override
	public String toString() {
		return "[current value=" + getCurrentValue() + ", default value=" + defaultValue +
			", isRegistered=" + isRegistered + "]";
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getInceptionInformation() {
		return inceptionInformation;
	}

	private void recordInception() {
		if (!SystemUtilities.isInDevelopmentMode()) {
			return;
		}
		Throwable throwable = new Throwable();
		StackTraceElement[] stackTrace = throwable.getStackTrace();

		StackTraceElement[] filteredTrace =
			ReflectionUtilities.filterStackTrace(stackTrace, "option", "OptionsManager");
		inceptionInformation = filteredTrace[0].toString();
	}

	public OptionType getOptionType() {
		return optionType;
	}
}
