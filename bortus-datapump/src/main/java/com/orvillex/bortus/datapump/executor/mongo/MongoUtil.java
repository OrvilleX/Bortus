package com.orvillex.bortus.datapump.executor.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.orvillex.bortus.datapump.exception.DataPumpException;

public class MongoUtil {
    public static MongoClient initMongoClient(AbstractParam conf) {
        List<String> addressList = conf.getAddress();
        if (addressList == null || addressList.size() <= 0) {
            throw new DataPumpException("不合法参数");
        }
        try {
            return new MongoClient(parseServerAddress(addressList));
        } catch (UnknownHostException e) {
            throw new DataPumpException("不合法的地址");
        } catch (NumberFormatException e) {
            throw new DataPumpException("不合法参数");
        } catch (Exception e) {
            throw new DataPumpException("未知异常");
        }
    }

    public static MongoClient initCredentialMongoClient(AbstractParam conf, String userName, String password,
            String database) {
        List<String> addressList = conf.getAddress();
        if (!isHostPortPattern(addressList)) {
            throw new DataPumpException("不合法参数");
        }
        try {
            MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
            return new MongoClient(parseServerAddress(addressList), Arrays.asList(credential));
        } catch (UnknownHostException e) {
            throw new DataPumpException("不合法的地址");
        } catch (NumberFormatException e) {
            throw new DataPumpException("不合法参数");
        } catch (Exception e) {
            throw new DataPumpException("未知异常");
        }
    }

    private static boolean isHostPortPattern(List<String> addressList) {
        for (String address : addressList) {
            String regex = "(\\S+):([0-9]+)";
            if (!(address).matches(regex)) {
                return false;
            }
        }
        return true;
    }

    private static List<ServerAddress> parseServerAddress(List<String> rawAddressList) throws UnknownHostException {
        List<ServerAddress> addressList = new ArrayList<ServerAddress>();
        for (Object address : rawAddressList) {
            String[] tempAddress = ((String) address).split(":");
            try {
                ServerAddress sa = new ServerAddress(tempAddress[0], Integer.valueOf(tempAddress[1]));
                addressList.add(sa);
            } catch (Exception e) {
                throw new UnknownHostException();
            }
        }
        return addressList;
    }
}
