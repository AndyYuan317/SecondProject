package com.wingplus.coomohome.util;

import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.entity.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author leaffun.
 *         Create on 2017/9/18.
 */
public class StringUtil {

    /**
     * 以空格拼接字符串
     *
     * @param ss
     * @return
     */
    public static String bindStr(LinkedHashMap<String, String> ss) {
        String result = "";
        if (ss == null) {
            LogUtil.i("StringUtil", "LinkedHashMap<String, String> ss == null");
        } else if (ss.size() <= 0) {
            LogUtil.i("StringUtil", "LinkedHashMap<String, String> ss.size() <= 0");
        } else {
            List<String> strings = new ArrayList<>();
            for (Map.Entry<String, String> entry : ss.entrySet()) {
                strings.add(entry.getKey());
            }

            Collections.sort(strings);

            for (int i = 0; i < strings.size(); i++) {
                result += ss.get(strings.get(i));
                if (i != ss.size() - 1) {
                    result += " ";
                }
            }
        }
        LogUtil.i("StringUtil", result);
        return result;
    }

    public static String bindStr(List<TypeSpec> tps) {
        String result = "";
        if (tps == null) {
            LogUtil.i("StringUtil", "List<TypeSpec> tps == null");
        } else if (tps.size() <= 0) {
            LogUtil.i("StringUtil", "List<TypeSpec> tps.size() <= 0");
        } else {
            List<String> strings = new ArrayList<>();
            for (TypeSpec tp : tps) {
                strings.add(tp.getType());
            }

            Collections.sort(strings);

            for (int i = 0; i < strings.size(); i++) {
                for (TypeSpec ts : tps) {
                    if (ts.getType().equals(strings.get(i))) {
                        result += ts.getValue();
                        if (i != strings.size() - 1) {
                            result += " ";
                        }
                    }
                }
            }
        }
        LogUtil.i("StringUtil", result);
        return result;
    }

    public static String bindStr(Set<String> set, String split) {
        if (set == null || set.size() <= 0) {
            return "";
        } else {
            String s = "";
            for (String str : set) {
                s += str;
                s += split;
            }
            return s.substring(0, s.length() - 1);
        }
    }

    public static String bindStr(List<String> list, String split) {
        String rs = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                rs += list.get(i);
                if (i < list.size() - 1) {
                    rs += split;
                }
            }
        }
        return rs;
    }

    public static String getStrByOrderStatus(int orderStatus) {
        switch (orderStatus) {
            case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY:
                return Constant.OrderStatus.ORDER_STATUS_WAITING_PAY;
            case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_SEND:
                return Constant.OrderStatus.ORDER_STATUS_WAITING_SEND;
            case Constant.OrderStatus.ORDER_STATUS_INT_ALREADY_SEND:
                return Constant.OrderStatus.ORDER_STATUS_ALREADY_SEND;
            case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_COMMIT:
                return Constant.OrderStatus.ORDER_STATUS_WAITING_COMMIT;
            case Constant.OrderStatus.ORDER_STATUS_INT_DONE:
                return Constant.OrderStatus.ORDER_STATUS_DONE;
            case Constant.OrderStatus.ORDER_STATUS_INT_IN_AFTER_SALE:
                return Constant.OrderStatus.ORDER_STATUS_DONE;
            case Constant.OrderStatus.ORDER_STATUS_INT_CANCEL:
                return Constant.OrderStatus.ORDER_STATUS_CANCEL;
            default:
                return "未知状态";
        }
    }
    public static String getStrByAfterSaleOrderStatus(int orderStatus) {
        switch (orderStatus) {
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_SERVICE:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_WAITING_SERVICE;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_GOODS:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_WAITING_GOODS;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_REJECT:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_REJECT;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_RETURN:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_WAITING_RETURN;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_DONE_RETURN:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_DONE_RETURN;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_CANCEL:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_CANCEL;
            case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_LOGISTIC:
                return Constant.OrderStatus.ORDER_AF_STATUS_STR_WAITING_LOGISTIC;
            default:
                return "未知状态";
        }
    }

    public static String getCateIdByName(String name, List<Category> list) {
        if (list != null && list.size() > 0) {
            for (Category cate : list) {
                if (name.equals(cate.getName())) {
                    return cate.getId();
                }
            }
        }
        return "0";
    }


    /**
     * 格式化成JSonView
     *
     * @param json
     * @return
     */
    public static String format(String json) {
        String jsonStr = convertUnicode(json);

        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }
}
