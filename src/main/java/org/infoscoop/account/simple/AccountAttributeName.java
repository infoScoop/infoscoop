package org.infoscoop.account.simple;

public class AccountAttributeName {
	private AccountAttributeName(){}

	public static final String OWNED_SQUARE_NUMBER = "owned_square_number";

	/*
		control account update flg by api.
		0: Can't update by api.
		1: Only register user.
		2: All users.
	 */
	public static final String UPDATE_PERMISSION = "update_permission";

	public static final String REGISTERED_SQUARE = "registered_square";
}
