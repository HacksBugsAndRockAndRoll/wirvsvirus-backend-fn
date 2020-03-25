package de.wirvsvirus.testresult;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TestResult {

	@JsonProperty("_id")
	String id;
	Result status;
	String contact;
	boolean notified;
	
	public enum Result { POSITIVE, NEGATIVE, PENDING}
	
}
