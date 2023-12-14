package pw.geckonerd.CursedRB.Launcher;

public class AuthResponse {
	private ErrorType errorType;
	private boolean error;
	
	private String errorServerMessage;
	private Exception errorExceptionMessage;
	
	private String token;
	
	public AuthResponse() {
		error = false;
	}
	
	public void setErrorServer(String message) {
		error = true;
		errorType = ErrorType.SERVER;
		errorServerMessage = message;
	}
	public void setErrorException(Exception exception) {
		error = true;
		errorType = ErrorType.EXCEPTION;
		errorExceptionMessage = exception;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public boolean getError() {
		return this.error;
	}
	public ErrorType getErrorType() {
		return this.errorType;
	}
	public Exception getErrorE() {
		return this.errorExceptionMessage;
	}
	public String getErrorS() {
		return this.errorServerMessage;
	}
	public String getToken() {
		return this.token;
	}
}

enum ErrorType {
	SERVER,
	EXCEPTION
}
