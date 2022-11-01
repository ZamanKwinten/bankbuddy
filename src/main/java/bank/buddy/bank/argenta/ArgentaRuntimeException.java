package bank.buddy.bank.argenta;

class ArgentaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ArgentaRuntimeException(String details, Exception e) {
		super(details, e);
	}

	public ArgentaRuntimeException(String details) {
		super(details);
	}
}
