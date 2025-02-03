package com.mockxpert.interview_marketplace.dto;


public class FirebaseLoginResponse {
    private String idToken;
    private String refreshToken;
    private String localId;
    
	public FirebaseLoginResponse(String idToken, String refreshToken, String localId) {
		super();
		this.idToken = idToken;
		this.refreshToken = refreshToken;
		this.localId = localId;
	}
	
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getLocalId() {
		return localId;
	}
	public void setLocalId(String localId) {
		this.localId = localId;
	}
    
    
}