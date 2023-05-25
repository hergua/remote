package xin.opstime.remote.utils;

/**
 * Created on 2023/5/24
 *
 * @author hergua
 */
public class StringUtils {


    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isAnyEmpty(String... strs){
        for (String str : strs) {
            if (isEmpty(str)) return true;
        }
        return false;
    }


}
