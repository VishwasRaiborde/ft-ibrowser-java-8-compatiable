package com.cloudsherpas.request;

public class TypeValidationRequest {
	public String key;
	public String type;
	public String groupName;
	public String groupCode;
	
	public TypeValidationRequest(){
		super();
	}
	public TypeValidationRequest(String key, String type, String groupName, String groupCode){
		this.key = key;
		this.type = type;
		this.groupName = groupName;
		this.groupCode = groupCode;
	}
}
