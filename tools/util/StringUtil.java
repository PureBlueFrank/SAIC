/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： StringUtil.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/
/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.context.ObjectFactory;
import javolution.lang.Reusable;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.ofbiz.base.lang.Appender;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.Validator;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.HTMLEntityCodec;
import org.owasp.esapi.codecs.PercentCodec;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.reference.DefaultEncoder;
import org.owasp.esapi.reference.DefaultValidator;

/**
 * Misc String Utility Functions
 *
 */
public class StringUtil {

    public static final String module = StringUtil.class.getName();
    protected static final Map<String, Pattern> substitutionPatternMap;

    /** OWASP ESAPI canonicalize strict flag; setting false so we only get warnings about double encoding, etc; can be set to true for exceptions and more security */
    public static final boolean esapiCanonicalizeStrict = false;
    public static final Encoder defaultWebEncoder;
    public static final Validator defaultWebValidator;
    static {
        // possible codecs: CSSCodec, HTMLEntityCodec, JavaScriptCodec, MySQLCodec, OracleCodec, PercentCodec, UnixCodec, VBScriptCodec, WindowsCodec
        List<Codec> codecList = Arrays.asList(new HTMLEntityCodec(), new PercentCodec());
        defaultWebEncoder = new DefaultEncoder(codecList);
        defaultWebValidator = new DefaultValidator();
        substitutionPatternMap = FastMap.newInstance();
        substitutionPatternMap.put("&&", Pattern.compile("@and", Pattern.LITERAL));
        substitutionPatternMap.put("||", Pattern.compile("@or", Pattern.LITERAL));
        substitutionPatternMap.put("<=", Pattern.compile("@lteq", Pattern.LITERAL));
        substitutionPatternMap.put(">=", Pattern.compile("@gteq", Pattern.LITERAL));
        substitutionPatternMap.put("<", Pattern.compile("@lt", Pattern.LITERAL));
        substitutionPatternMap.put(">", Pattern.compile("@gt", Pattern.LITERAL));
    }

    public static final SimpleEncoder htmlEncoder = new HtmlEncoder();
    public static final SimpleEncoder xmlEncoder = new XmlEncoder();
    public static final SimpleEncoder stringEncoder = new StringEncoder();

    public static interface SimpleEncoder {
        public String encode(String original);
    }

    public static class HtmlEncoder implements SimpleEncoder {
        public String encode(String original) {
//            return StringUtil.defaultWebEncoder.encodeForHTML(original);
        	if(UtilValidate.isNotEmpty(original))
        		original = original.replace("\\", "\\\\");
        	return filter(original);
        }
    }
    
    public static String filter(String value) {
        if ((value == null) || (value.length() == 0)) {
            return value;
        }

        StringBuffer result = null;
        String filtered = null;

        for (int i = 0; i < value.length(); i++) {
            filtered = null;

            switch (value.charAt(i)) {
            case '<':
                filtered = "&lt;";
                break;
            case '>':
                filtered = "&gt;";
                break;
            case '&':
                filtered = "&amp;";
                break;
            case '"':
                filtered = "&quot;";
                break;
            case '\'':
                filtered = "&#39;";
                break;
        	case '$':
                filtered = "&#36;";
                break;
            }
            if (result == null) {
                if (filtered != null) {
                    result = new StringBuffer(value.length() + 50);

                    if (i > 0) {
                        result.append(value.substring(0, i));
                    }

                    result.append(filtered);
                }
            } else {
                if (filtered == null) {
                    result.append(value.charAt(i));
                } else {
                    result.append(filtered);
                }
            }
        }

        return (result == null) ? value : result.toString();
    }

    public static class XmlEncoder implements SimpleEncoder {
        public String encode(String original) {
            return StringUtil.defaultWebEncoder.encodeForXML(original);
        }
    }

    public static class StringEncoder implements SimpleEncoder {
        public String encode(String original) {
            if (original != null) {
                original = original.replace("\"", "\\\"");
            }
            return original;
        }
    }


    // ================== Begin General Functions ==================
    
    public static int omitStrLength(String str){
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (Integer.toHexString(str.charAt(i)).length() == 4) {
				count += 2;
			} else {
				count++;
			}
		}
		return count;
	}
    
    /**
     * 把Map转换成Url后缀参数的形式
     */
    public static String MapToUrl(Map<String, Object> map){
    	boolean flag = true;
    	StringBuffer url = new StringBuffer();
    	for (Map.Entry<String, Object> m : map.entrySet()) {  
    		if(flag){
    			flag = false;
    		}else{
    			url.append("&");
    		}
    		url.append(m.getKey()+"="+m.getValue().toString());
    	} 
    	return url.toString();
    }

    public static SimpleEncoder getEncoder(String type) {
        if ("xml".equals(type)) {
            return StringUtil.xmlEncoder;
        } else if ("html".equals(type)) {
            return StringUtil.htmlEncoder;
        } else if ("string".equals(type)) {
            return StringUtil.stringEncoder;
        } else {
            return null;
        }
    }

    public static String internString(String value) {
        return value != null ? value.intern() : null;
    }

    /**
     * Replaces all occurrences of oldString in mainString with newString
     * @param mainString The original string
     * @param oldString The string to replace
     * @param newString The string to insert in place of the old
     * @return mainString with all occurrences of oldString replaced by newString
     */
    public static String replaceString(String mainString, String oldString, String newString) {
        if (mainString == null) {
            return null;
        }
        if (UtilValidate.isEmpty(oldString)) {
            return mainString;
        }
        if (newString == null) {
            newString = "";
        }

        int i = mainString.lastIndexOf(oldString);

        if (i < 0) return mainString;

        StringBuilder mainSb = new StringBuilder(mainString);

        while (i >= 0) {
            mainSb.replace(i, i + oldString.length(), newString);
            i = mainString.lastIndexOf(oldString, i - 1);
        }
        return mainSb.toString();
    }
    


    /**
     * Replaces Last occurrences of oldString in mainString with newString
     * @param mainString The original string
     * @param oldString The string to replace
     * @param newString The string to insert in place of the old
     * @return mainString with all occurrences of oldString replaced by newString
     */
    public static String replaceLastString(String mainString, String oldString, String newString) {
        if (mainString == null) {
            return null;
        }
        if (UtilValidate.isEmpty(oldString)) {
            return mainString;
        }
        if (newString == null) {
            newString = "";
        }

        int i = mainString.lastIndexOf(oldString);

        if (i < 0) return mainString;
        
        return mainString.substring(0, i);
    }

    /**
     * Creates a single string from a List of strings seperated by a delimiter.
     * @param list a list of strings to join
     * @param delim the delimiter character(s) to use. (null value will join with no delimiter)
     * @return a String of all values in the list seperated by the delimiter
     */
    public static String join(List<?> list, String delim) {
        if (list == null || list.size() < 1)
            return null;
        StringBuilder buf = new StringBuilder();
        Iterator<?> i = list.iterator();

        while (i.hasNext()) {
            buf.append(i.next());
            if (i.hasNext())
                buf.append(delim);
        }
        return buf.toString();
    }

    /**
     * Splits a String on a delimiter into a List of Strings.
     * @param str the String to split
     * @param delim the delimiter character(s) to join on (null will split on whitespace)
     * @return a list of Strings
     */
    public static List<String> split(String str, String delim) {
        List<String> splitList = null;
        StringTokenizer st = null;

        if (str == null)
            return splitList;

        if (delim != null)
            st = new StringTokenizer(str, delim);
        else
            st = new StringTokenizer(str);

        if (st != null && st.hasMoreTokens()) {
            splitList = FastList.newInstance();

            while (st.hasMoreTokens())
                splitList.add(st.nextToken());
        }
        return splitList;
    }

    /**
     * Encloses each of a List of Strings in quotes.
     * @param list List of String(s) to quote.
     */
    public static List<String> quoteStrList(List<String> list) {
        List<String> tmpList = list;

        list = FastList.newInstance();
        for (String str: tmpList) {
            str = "'" + str + "'";
            list.add(str);
        }
        return list;
    }

    /**
     * Creates a Map from an encoded name/value pair string
     * @param str The string to decode and format
     * @param trim Trim whitespace off fields
     * @return a Map of name/value pairs
     */
    public static Map<String, String> strToMap(String str, boolean trim) {
        if (str == null) return null;
        Map<String, String> decodedMap = FastMap.newInstance();
        List<String> elements = split(str, "|");

        for (String s: elements) {
            List<String> e = split(s, "=");

            if (e.size() != 2) {
                continue;
            }
            String name = e.get(0);
            String value = e.get(1);
            if (trim) {
                if (name != null) {
                    name = name.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
            }

            try {
                decodedMap.put(URLDecoder.decode(name, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                Debug.logError(e1, module);
            }
        }
        return decodedMap;
    }

    /**
     * Creates a Map from an encoded name/value pair string
     * @param str The string to decode and format
     * @return a Map of name/value pairs
     */
    public static Map<String, String> strToMap(String str) {
        return strToMap(str, false);
    }

    /**
     * Creates an encoded String from a Map of name/value pairs (MUST BE STRINGS!)
     * @param map The Map of name/value pairs
     * @return String The encoded String
     */
    public static String mapToStr(Map<? extends Object, ? extends Object> map) {
        if (map == null) return null;
        StringBuilder buf = new StringBuilder();
        boolean first = true;

        for (Map.Entry<? extends Object, ? extends Object> entry: map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (!(key instanceof String) || !(value instanceof String))
                continue;
            String encodedName = null;
            try {
                encodedName = URLEncoder.encode((String) key, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Debug.logError(e, module);
            }
            String encodedValue = null;
            try {
                encodedValue = URLEncoder.encode((String) value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Debug.logError(e, module);
            }

            if (first)
                first = false;
            else
                buf.append("|");

            buf.append(encodedName);
            buf.append("=");
            buf.append(encodedValue);
        }
        return buf.toString();
    }

    /**
     * Reads a String version of a Map (should contain only strings) and creates a new Map
     *
     * @param s String value of a Map ({n1=v1, n2=v2})
     * @return new Map
     */
    public static Map<String, String> toMap(String s) {
        Map<String, String> newMap = FastMap.newInstance();
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1);
            String[] entries = s.split("\\,\\s");
            for (String entry: entries) {
                String[] nv = entry.split("\\=");
                newMap.put(nv[0], nv[1]);
            }
        } else {
            throw new IllegalArgumentException("String is not from Map.toString()");
        }

        return newMap;
    }

    /**
     * Reads a String version of a List (should contain only strings) and creates a new List
     *
     * @param s String value of a Map ({n1=v1, n2=v2})
     * @return new List
     */
    public static List<String> toList(String s) {
        List<String> newList = FastList.newInstance();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
            String[] entries = s.split("\\,\\s");
            for (String entry: entries) {
                newList.add(entry);
            }
        } else {
            throw new IllegalArgumentException("String is not from List.toString()");
        }

        return newList;
    }

    /**
     * Reads a String version of a Set (should contain only strings) and creates a new Set
     *
     * @param s String value of a Map ({n1=v1, n2=v2})
     * @return new List
     */
    public static Set<String> toSet(String s) {
        Set<String> newSet = FastSet.newInstance();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
            String[] entries = s.split("\\,\\s");
            for (String entry: entries) {
                newSet.add(entry);
            }
        } else {
            throw new IllegalArgumentException("String is not from Set.toString()");
        }

        return newSet;
    }

    /**
     * Create a Map from a List of keys and a List of values
     * @param keys List of keys
     * @param values List of values
     * @return Map of combined lists
     * @throws IllegalArgumentException When either List is null or the sizes do not equal
     */
    public static <K, V> Map<K, V> createMap(List<K> keys, List<V> values) {
        if (keys == null || values == null || keys.size() != values.size()) {
            throw new IllegalArgumentException("Keys and Values cannot be null and must be the same size");
        }
        Map<K, V> newMap = FastMap.newInstance();
        for (int i = 0; i < keys.size(); i++) {
            newMap.put(keys.get(i), values.get(i));
        }
        return newMap;
    }

    /** Make sure the string starts with a forward slash but does not end with one; converts back-slashes to forward-slashes; if in String is null or empty, returns zero length string. */
    public static String cleanUpPathPrefix(String prefix) {
        if (UtilValidate.isEmpty(prefix)) return "";

        StringBuilder cppBuff = new StringBuilder(prefix.replace('\\', '/'));

        if (cppBuff.charAt(0) != '/') {
            cppBuff.insert(0, '/');
        }
        if (cppBuff.charAt(cppBuff.length() - 1) == '/') {
            cppBuff.deleteCharAt(cppBuff.length() - 1);
        }
        return cppBuff.toString();
    }

    /** Removes all spaces from a string */
    public static String removeSpaces(String str) {
        return removeRegex(str,"[\\ ]");
    }

    public static String toHexString(byte[] bytes) {
        return new String(Hex.encodeHex(bytes));
    }

    public static String cleanHexString(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != 32 && str.charAt(i) != ':') {
                buf.append(str.charAt(i));
            }
        }
        return buf.toString();
    }

    public static byte[] fromHexString(String str) {
        str = cleanHexString(str);
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            throw new GeneralRuntimeException(e);
        }
    }

    private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static int convertChar(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0' ;
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 0xa ;
        } else if ('A' <= c && c <= 'F') {
            return c - 'A' + 0xa ;
        } else {
            throw new IllegalArgumentException("Invalid hex character: [" + c + "]");
        }
    }

    public static char[] encodeInt(int i, int j, char digestChars[]) {
        if (i < 16) {
            digestChars[j] = '0';
        }
        j++;
        do {
            digestChars[j--] = hexChar[i & 0xf];
            i >>>= 4;
        } while (i != 0);
        return digestChars;
    }

    /** Removes all non-numbers from str */
    public static String removeNonNumeric(String str) {
        return removeRegex(str,"[\\D]");
    }

    /** Removes all numbers from str */
    public static String removeNumeric(String str) {
        return removeRegex(str,"[\\d]");
    }

    /**
     * @param str
     * @param regex
     * Removes all matches of regex from a str
     */
    public static String removeRegex(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    /**
     * Add the number to the string, keeping (padding to min of original length)
     *
     * @return the new value
     */
    public static String addToNumberString(String numberString, long addAmount) {
        if (numberString == null) return null;
        int origLength = numberString.length();
        long number = Long.parseLong(numberString);
        return padNumberString(Long.toString(number + addAmount), origLength);
    }

    public static String padNumberString(String numberString, int targetMinLength) {
        StringBuilder outStrBfr = new StringBuilder(numberString);
        while (targetMinLength > outStrBfr.length()) {
            outStrBfr.insert(0, '0');
        }
        return outStrBfr.toString();
    }

    /** Converts operator substitutions (@and, @or, etc) back to their original form.
     * <p>OFBiz script syntax provides special forms of common operators to make
     * it easier to embed logical expressions in XML:
     * <table border="1" cellpadding="2">
     * <tr><td><strong>@and</strong></td><td>&amp;&amp;</td></tr>
     * <tr><td><strong>@or</strong></td><td>||</td></tr>
     * <tr><td><strong>@gt</strong></td><td>&gt;</td></tr>
     * <tr><td><strong>@gteq</strong></td><td>&gt;=</td></tr>
     * <tr><td><strong>@lt</strong></td><td>&lt;</td></tr>
     * <tr><td><strong>@lteq</strong></td><td>&lt;=</td></tr>
     * </table></p>
     * @param expression The <code>String</code> to convert
     * @return The converted <code>String</code>
     */
    public static String convertOperatorSubstitutions(String expression) {
        String result = expression;
        if (result != null && (result.contains("@") || result.contains("'"))) {
            for (Map.Entry<String, Pattern> entry: substitutionPatternMap.entrySet()) {
                Pattern pattern = entry.getValue();
                result = pattern.matcher(result).replaceAll(entry.getKey());
            }
            if (Debug.verboseOn()) {
                Debug.logVerbose("Converted " + expression + " to " + result, module);
            }
        }
        return result;
    }

    /**
     * Uses a black-list approach for necessary characters for HTML.
     * Does not allow various characters (after canonicalization), including "<", ">", "&" (if not followed by a space), and "%" (if not followed by a space).
     *
     * @param value
     * @param errorMessageList
     */
    public static String checkStringForHtmlStrictNone(String valueName, String value, List<String> errorMessageList) {
        if (UtilValidate.isEmpty(value)) return value;

        // canonicalize, strict (error on double-encoding)
        try {
            value = defaultWebEncoder.canonicalize(value, true);
        } catch (EncodingException e) {
            // NOTE: using different log and user targeted error messages to allow the end-user message to be less technical
            Debug.logError("Canonicalization (format consistency, character escaping that is mixed or double, etc) error for attribute named [" + valueName + "], String [" + value + "]: " + e.toString(), module);
            errorMessageList.add("In field [" + valueName + "] found character espacing (mixed or double) that is not allowed or other format consistency error: " + e.toString());
        }

        // check for "<", ">"
        if (value.indexOf("<") >= 0 || value.indexOf(">") >= 0) {
        	errorMessageList.add("请勿输入包含( < > )的值！");
            //errorMessageList.add("In field [" + valueName + "] less-than (<) and greater-than (>) symbols are not allowed.");
        }

        /* NOTE DEJ 20090311: After playing with this more this doesn't seem to be necessary; the canonicalize will convert all such characters into actual text before this check is done, including other illegal chars like &lt; which will canonicalize to < and then get caught
        // check for & followed a semicolon within 7 characters, no spaces in-between (and perhaps other things sometime?)
        int curAmpIndex = value.indexOf("&");
        while (curAmpIndex > -1) {
            int semicolonIndex = value.indexOf(";", curAmpIndex + 1);
            int spaceIndex = value.indexOf(" ", curAmpIndex + 1);
            if (semicolonIndex > -1 && (semicolonIndex - curAmpIndex <= 7) && (spaceIndex < 0 || (spaceIndex > curAmpIndex && spaceIndex < semicolonIndex))) {
                errorMessageList.add("In field [" + valueName + "] the ampersand (&) symbol is only allowed if not used as an encoded character: no semicolon (;) within 7 spaces or there is a space between.");
                // once we find one like this we have the message so no need to check for more
                break;
            }
            curAmpIndex = value.indexOf("&", curAmpIndex + 1);
        }
         */

        /* NOTE DEJ 20090311: After playing with this more this doesn't seem to be necessary; the canonicalize will convert all such characters into actual text before this check is done, including other illegal chars like %3C which will canonicalize to < and then get caught
        // check for % followed by 2 hex characters
        int curPercIndex = value.indexOf("%");
        while (curPercIndex >= 0) {
            if (value.length() > (curPercIndex + 3) && UtilValidate.isHexDigit(value.charAt(curPercIndex + 1)) && UtilValidate.isHexDigit(value.charAt(curPercIndex + 2))) {
                errorMessageList.add("In field [" + valueName + "] the percent (%) symbol is only allowed if followed by a space.");
                // once we find one like this we have the message so no need to check for more
                break;
            }
            curPercIndex = value.indexOf("%", curPercIndex + 1);
        }
         */

        // TODO: anything else to check for that can be used to get HTML or JavaScript going without these characters?

        return value;
    }

    /**
     * Uses a white-list approach to check for safe HTML.
     * Based on the ESAPI validator configured in the antisamy-esapi.xml file.
     *
     * @param value
     * @param errorMessageList
     * @return String with updated value if needed for safer HTML.
     */
    public static String checkStringForHtmlSafeOnly(String valueName, String value, List<String> errorMessageList) {
        ValidationErrorList vel = new ValidationErrorList();
        value = defaultWebValidator.getValidSafeHTML(valueName, value, Integer.MAX_VALUE, true, vel);
        errorMessageList.addAll(UtilGenerics.checkList(vel.errors(), String.class));
        return value;
    }

    /**
     * Translates various HTML characters in a string so that the string can be displayed in a browser safely
     * <p>
     * This function is useful in preventing user-supplied text from containing HTML markup, such as in a message board or
     * guest book application.
     * </p>
     * The translations performed are: <ol>
     *    <li>'&' (ampersand) becomes '&amp;'
     *    <li>'"' (double quote) becomes '&quot;'
     *    <li>''' (single quote) becomes '&#039;'
     *    <li>'<' (less than) becomes '&lt;'
     *    <li>'>' (greater than) becomes '&gt;'
     *    <li>\n (Carriage Return) becomes '&lt;br&gt;gt;'
     * </ol>
     *
     * @deprecated Use StringUtil.htmlEncoder instead.
     */
    @Deprecated
    public static String htmlSpecialChars(String html) {
        html = StringUtil.replaceString(html, "&", "&amp;");
        html = StringUtil.replaceString(html, "<", "&lt;");
        html = StringUtil.replaceString(html, ">", "&gt;");
        html = StringUtil.replaceString(html, "\"", "&quot;");
        html = StringUtil.replaceString(html, "'", "&#039");
        html = StringUtil.replaceString(html, "\n", "<br>");

        return html;
    }

    /**
     * Remove/collapse multiple newline characters
     *
     * @param str string to collapse newlines in
     * @return the converted string
     */
    public static String collapseNewlines(String str) {
        return collapseCharacter(str, '\n');
    }

    /**
     * Remove/collapse multiple spaces
     *
     * @param str string to collapse spaces in
     * @return the converted string
     */
    public static String collapseSpaces(String str) {
        return collapseCharacter(str, ' ');
    }

    /**
     * Remove/collapse multiple characters
     *
     * @param str string to collapse characters in
     * @param c character to collapse
     * @return the converted string
     */
    public static String collapseCharacter(String str, char c) {
        StringBuilder sb = new StringBuilder();
        char last = str.charAt(0);

        for (int i = 0; i < str.length(); i++) {
            char current = str.charAt(i);
            if (i == 0 || current != c || last != c) {
                sb.append(current);
                last = current;
            }
        }

        return sb.toString();
    }

    public static StringWrapper wrapString(String theString) {
        return makeStringWrapper(theString);
    }
    public static StringWrapper makeStringWrapper(String theString) {
        if (theString == null) return null;
        if (theString.length() == 0) return StringWrapper.EMPTY_STRING_WRAPPER;
        return new StringWrapper(theString);
    }

    public static StringBuilder appendTo(StringBuilder sb, Iterable<? extends Appender<StringBuilder>> iterable, String prefix, String suffix, String sep) {
        Iterator<? extends Appender<StringBuilder>> it = iterable.iterator();
        while (it.hasNext()) {
            if (prefix != null) sb.append(prefix);
            it.next().appendTo(sb);
            if (suffix != null) sb.append(suffix);
            if (it.hasNext() && sep != null) sb.append(sep);
        }
        return sb;
    }

    public static StringBuilder append(StringBuilder sb, Iterable<? extends Object> iterable, String prefix, String suffix, String sep) {
        Iterator<? extends Object> it = iterable.iterator();
        while (it.hasNext()) {
            if (prefix != null) sb.append(prefix);
            sb.append(it.next());
            if (suffix != null) sb.append(suffix);
            if (it.hasNext() && sep != null) sb.append(sep);
        }
        return sb;
    }

    public static String substring(String s, int length) throws Exception  {  
    	if(s.length() <= length){
    		return s;
    	}
    	length *= 2;
    	length -= 3;
        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数  
        int i = 2; // 要截取的字节数，从第3个字节开始  
        for (; i < bytes.length && n < length; i++)  {  
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节  
            if (i % 2 == 1)  {  
                n++; // 在UCS2第二个字节时n加1  
            }   else {  
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节  
                if (bytes[i] != 0)  {  
                    n++;  
                }  
            }  
        }  
        // 如果i为奇数时，处理成偶数  
        if (i % 2 == 1)  {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字  
            if (bytes[i - 1] != 0)  
                i = i - 1;  
            // 该UCS2字符是字母或数字，则保留该字符  
            else 
                i = i + 1;  
        }  
        return new String(bytes, 0, i, "Unicode") + "...";  
    }  


	public static String delHTMLTag(String htmlStr){
	    String regEx_script="<script[^>]*?>[//s//S]*?<///script>"; //定义script的正则表达式
	    String regEx_style="<style[^>]*?>[//s//S]*?<///style>"; //定义style的正则表达式
	    String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式
	    Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
	    Matcher m_script=p_script.matcher(htmlStr);
	    htmlStr=m_script.replaceAll(""); //过滤script标签
	    Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
	    Matcher m_style=p_style.matcher(htmlStr);
	    htmlStr=m_style.replaceAll(""); //过滤style标签
	    Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
	    Matcher m_html=p_html.matcher(htmlStr);
	    htmlStr=m_html.replaceAll(""); //过滤html标签
	    return htmlStr.trim(); //返回文本字符串
	}

	public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    }
	
    
    /**
     * A super-lightweight object to wrap a String object. Mainly used with FTL templates
     * to avoid the general HTML auto-encoding that is now done through the Screen Widget.
     */
    public static class StringWrapper {
        public static final StringWrapper EMPTY_STRING_WRAPPER = new StringWrapper("");

        protected String theString;
        protected StringWrapper() { }
        public StringWrapper(String theString) {
            this.theString = theString;
        }

        /**
         * Fairly simple method used for the plus (+) base concatenation in Groovy.
         *
         * @param value
         * @return the wrapped string, plus the value
         */
        public String plus(Object value) {
            return this.theString + value;
        }

        /**
         * @return The String this object wraps.
         */
        @Override
        public String toString() {
            return this.theString;
        }
    }

    /**
     * A simple Map wrapper class that will do HTML encoding. To be used for passing a Map to something that will expand Strings with it as a context, etc.
     * To reduce memory allocation impact this object is recyclable and minimal in that it only keeps a reference to the original Map.
     */
    public static class HtmlEncodingMapWrapper<K> implements Map<K, Object>, Reusable {
        protected static final ObjectFactory<HtmlEncodingMapWrapper<?>> mapStackFactory = new ObjectFactory<HtmlEncodingMapWrapper<?>>() {
            @Override
            protected HtmlEncodingMapWrapper<?> create() {
                return new HtmlEncodingMapWrapper<Object>();
            }
        };
        public static <K> HtmlEncodingMapWrapper<K> getHtmlEncodingMapWrapper(Map<K, Object> mapToWrap, SimpleEncoder encoder) {
            if (mapToWrap == null) return null;

            HtmlEncodingMapWrapper<K> mapWrapper = (HtmlEncodingMapWrapper<K>) UtilGenerics.<K, Object>checkMap(mapStackFactory.object());
            mapWrapper.setup(mapToWrap, encoder);
            return mapWrapper;
        }

        protected Map<K, Object> internalMap = null;
        protected SimpleEncoder encoder = null;
        protected HtmlEncodingMapWrapper() { }

        public void setup(Map<K, Object> mapToWrap, SimpleEncoder encoder) {
            this.internalMap = mapToWrap;
            this.encoder = encoder;
        }
        public void reset() {
            this.internalMap = null;
            this.encoder = null;
        }

        public int size() { return this.internalMap.size(); }
        public boolean isEmpty() { return this.internalMap.isEmpty(); }
        public boolean containsKey(Object key) { return this.internalMap.containsKey(key); }
        public boolean containsValue(Object value) { return this.internalMap.containsValue(value); }
        public Object get(Object key) {
            Object theObject = this.internalMap.get(key);
            if (theObject instanceof String) {
                if (this.encoder != null) {
                    return encoder.encode((String) theObject);
                } else {
                    return StringUtil.defaultWebEncoder.encodeForHTML((String) theObject);
                }
            } else if (theObject instanceof Map) {
                return HtmlEncodingMapWrapper.getHtmlEncodingMapWrapper(UtilGenerics.<K, Object>checkMap(theObject), this.encoder);
            }
            return theObject;
        }
        public Object put(K key, Object value) { return this.internalMap.put(key, value); }
        public Object remove(Object key) { return this.internalMap.remove(key); }
        public void putAll(Map<? extends K, ? extends Object> arg0) { this.internalMap.putAll(arg0); }
        public void clear() { this.internalMap.clear(); }
        public Set<K> keySet() { return this.internalMap.keySet(); }
        public Collection<Object> values() { return this.internalMap.values(); }
        public Set<Map.Entry<K, Object>> entrySet() { return this.internalMap.entrySet(); }
        @Override
        public String toString() { return this.internalMap.toString(); }
    }
}
