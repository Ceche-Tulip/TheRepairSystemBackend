package org.trs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UseBCrypt {

	private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	/**
	 * 生成 BCrypt 密文（每次结果都可能不同，属于正常现象）。
	 */
	public static String encode(String rawPassword) {
		if (rawPassword == null || rawPassword.isBlank()) {
			throw new IllegalArgumentException("rawPassword 不能为空");
		}
		return PASSWORD_ENCODER.encode(rawPassword);
	}

	/**
	 * 验证明文与 BCrypt 密文是否匹配。
	 */
	public static boolean verify(String rawPassword, String encodedPassword) {
		if (rawPassword == null || encodedPassword == null) {
			return false;
		}
		return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
	}

	public static void main(String[] args) {
		// 用法:
		// 1) 只传一个参数: 生成密文
		//    java org.trs.UseBCrypt admin
		// 2) 传两个参数: 校验是否匹配
		//    java org.trs.UseBCrypt admin "$2a$10$..."
		if (args.length == 1) {
			String encoded = encode(args[0]);
			System.out.println("Raw      : " + args[0]);
			System.out.println("Encoded  : " + encoded);
			return;
		}

		if (args.length == 2) {
			boolean matched = verify(args[0], args[1]);
			System.out.println("Raw      : " + args[0]);
			System.out.println("Encoded  : " + args[1]);
			System.out.println("Matched  : " + matched);
			return;
		}

		System.out.println("Usage:");
		System.out.println("  Generate hash: java org.trs.UseBCrypt <rawPassword>");
		System.out.println("  Verify hash  : java org.trs.UseBCrypt <rawPassword> <encodedPassword>");
	}
}
