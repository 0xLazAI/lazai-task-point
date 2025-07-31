package com.lazai.utils;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

public class EthAddressValidateUtils {

    public static boolean isValidEthereumAddress(String address) {
        // 检查 hex 和长度
        if (address == null || !address.matches("^0x[0-9a-fA-F]{40}$")) {
            return false;
        }
        // 校验 EIP-55 校验和（如果有大小写混合）
        if (address.equals(address.toLowerCase()) || address.equals(address.toUpperCase())) {
            return true; // 全小写/大写时跳过 checksum
        }
        try {
            return Keys.toChecksumAddress(address).equals(address);
        } catch (Exception e) {
            return false;
        }
    }

}
