package com.bianfeng.ymnsdk.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA签名验签类
 */
public class RSASignature {
	
	/**
	 * 公钥
	 */
	public static final String PUBLICKEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDyesxvCKEiWA2ie4QpGN9xCtI8q6HqmspYl+4aN8YRTS1adT4pxrvw3ZXMleBA+AFL9ZZzt40Vkgq893wO4jT23UdoSkLfRajHMfCyKZP7cxmrok7YSGFiyBdsucJ+IbbE//H+egha6ixbv14TO6ObCBjn6EVjLqccrGOUVoikbwIDAQAB";
	
	/**
	 * 签名算法
	 */
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	public static String sign(String content, String privateKey) {
		return sign(content, privateKey, "UTF-8");
	}
	
	/**
	 * RSA签名
	 * @param content 待签名数据
	 * @param privateKey 商户私钥
	 * @param encode 字符集编码
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey, String encode) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			
			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
			signature.initSign(priKey);
			signature.update(content.getBytes(encode));

			return Base64.encode(signature.sign());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean doCheck(String content, String sign) {
		return verify(content, sign, PUBLICKEY, "UTF-8");
	}
	

	public static boolean doCheck(String content, String sign, String publicKey) {
		return verify(content, sign, publicKey, "UTF-8");
	}
	
	/**
	* RSA验签名检查
	* @param content 待签名数据
	* @param sign 签名值
	* @param ali_public_key 支付宝公钥
	* @param input_charset 编码格式
	* @return 布尔值
	*/
	public static boolean verify(String content, String sign, String ali_public_key, String input_charset)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = Base64.decode(ali_public_key);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		
			signature.initVerify(pubKey);
			signature.update( content.getBytes(input_charset) );
		
			boolean bverify = signature.verify( Base64.decode(sign) );
			return bverify;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
}
