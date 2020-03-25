package de.wirvsvirus.testresult;

import lombok.Data;

@Data
public class TestResult {

	String id;
	Result status;
	String contact;
	boolean notified;
	
	public enum Result { POSITIVE, NEGATIVE, PENDING}
	
}
