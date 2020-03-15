package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import java.util.HashMap;

/**
 * Parses and creates simple JSON string. Arrays are not supported for now
 * <p>
 * <pre>
 * {@code
 * 		JSON data = JSON.fromString("{'d':5.0 , 'b' : {'g':'rf'}}");
 * 		String v = data.getString("d");
 * 		if(v != null) {
 * 			System.out.println(String.format("Found string value %s for key 'd'",  v));
 *        } else {
 * 			System.out.println("NOT found string value for key 'd'");
 *        }
 * 		JSON v1 = data.getObject("b");
 * 		if(v1 != null) {
 * 			System.out.println(String.format("Found object value %s for key 'b'",  v1.toString()));
 *        } else {
 * 			System.out.println("NOT found JASN value for key 'b'");
 *        }
 * }
 * </pre>
 */
public class JSON extends HashMap<String, Object> {
    /**
     *
     */
    private static final long serialVersionUID = 5256509343718494486L;

    private enum Token {
        TOK_NONE,
        TOK_STRING,
        TOK_NUMBER,
        TOK_OPEN_BLOCK,
        TOK_CLOSE_BLOCK,
        TOK_COMMA,
        TOK_COLON,
    }

    private enum State {
        EXPECTING_TAG_OR_CLOSE,
        EXPECTING_COMMA_OR_CLOSE,
        EXPECTING_COLON,
        EXPECTING_VALUE_OR_OPEN,
    }

    final private static String spaces = " \t\n\r";
    final private static String numbers = "0123456789.-+eE";

    private static class Internal {
        String data;
        int next_pos;
        StringBuilder str_val;

        public Internal(String s) {
            data = s;
            str_val = null;
            next_pos = 0;
        }

        private Token next_token() {
            char closing_char;
            while (next_pos < data.length() && spaces.indexOf(data.charAt(next_pos)) >= 0) {
                next_pos++;
            }
            if (next_pos >= data.length())
                return Token.TOK_NONE;
            switch (data.charAt(next_pos)) {
                case '\"':
                case '\'':
                    closing_char = data.charAt(next_pos);
                    next_pos++;
                    str_val = new StringBuilder(data.length() - next_pos);
                    while (next_pos < data.length() && data.charAt(next_pos) != closing_char) {
                        if (data.charAt(next_pos) == '\\') {
                            if (next_pos < data.length() - 1)
                                next_pos++;
                            else
                                return Token.TOK_NONE;
                        }
                        str_val.append(data.charAt(next_pos++));
                    }
                    if (next_pos >= data.length())
                        return Token.TOK_NONE;
                    next_pos++;
                    return Token.TOK_STRING;
                case '{':
                    next_pos++;
                    return Token.TOK_OPEN_BLOCK;
                case '}':
                    next_pos++;
                    return Token.TOK_CLOSE_BLOCK;
                case ':':
                    next_pos++;
                    return Token.TOK_COLON;
                case ',':
                    next_pos++;
                    return Token.TOK_COMMA;
                default:
                    str_val = new StringBuilder(data.length() - next_pos);
                    while (next_pos < data.length() && numbers.indexOf(data.charAt(next_pos)) >= 0) {
                        str_val.append(data.charAt(next_pos++));
                    }
                    if (str_val.length() > 0)
                        return Token.TOK_NUMBER;
                    else
                        return Token.TOK_NONE;
            }
        }
    }

    private JSON() {
        super();
    }

    private static JSON fromStringInternal(Internal p) {
        JSON result = new JSON();
        State state = State.EXPECTING_TAG_OR_CLOSE;
        String currentTag = null;
        for (; ; ) {
            Token tok = p.next_token();
            switch (state) {
                case EXPECTING_TAG_OR_CLOSE:
                    switch (tok) {
                        case TOK_STRING:
                            currentTag = p.str_val.toString();
                            state = State.EXPECTING_COLON;
                            break;
                        case TOK_CLOSE_BLOCK:
                            return result;
                        default:
                            return null;
                    }
                    break;
                case EXPECTING_COMMA_OR_CLOSE:
                    switch (tok) {
                        case TOK_COMMA:
                            state = State.EXPECTING_TAG_OR_CLOSE;
                            break;
                        case TOK_CLOSE_BLOCK:
                            return result;
                        default:
                            return null;
                    }
                    break;
                case EXPECTING_COLON:
                    switch (tok) {
                        case TOK_COLON:
                            state = State.EXPECTING_VALUE_OR_OPEN;
                            break;
                        default:
                            return null;
                    }
                    break;
                case EXPECTING_VALUE_OR_OPEN:
                    switch (tok) {
                        case TOK_STRING:
                            result.put(currentTag, p.str_val.toString());
                            state = State.EXPECTING_COMMA_OR_CLOSE;
                            break;
                        case TOK_OPEN_BLOCK:
                            JSON res = fromStringInternal(p);
                            if (res == null)
                                return null;
                            result.put(currentTag, res);
                            state = State.EXPECTING_COMMA_OR_CLOSE;
                            break;
                        case TOK_NUMBER:
                            try {
                                double val = Double.valueOf(p.str_val.toString());
                                result.put(currentTag, String.valueOf(val));
                                state = State.EXPECTING_COMMA_OR_CLOSE;
                            } catch (NumberFormatException e) {
                                return null;
                            }
                            break;
                        default:
                            return null;
                    }
            }
        }
    }

    public static JSON fromString(String jsonString) {
        Internal p = new Internal(jsonString);
        switch (p.next_token()) {
            case TOK_NONE:
                return new JSON();
            case TOK_OPEN_BLOCK:
                return fromStringInternal(p);
            default:
                return null;
        }
    }

    private static boolean toStringInternal(StringBuilder s, JSON data) {
        if (data != null) {
            char c = '{';
            for (String key : data.keySet()) {
                Object v = data.get(key);
                s.append(c);
                if (v.getClass() == String.class) {
                    s.append(String.format("\"%s\":\"%s\"", key, data.get(key)));
                } else if (v.getClass() == JSON.class) {
                    s.append(String.format("\"%s\":", key));
                    if (!toStringInternal(s, (JSON) v))
                        return false;
                } else {
                    return false;
                }
                c = ',';
            }
            if (s.length() == 0)
                s.append('{');
            s.append('}');
        }
        return true;
    }

    public static String toString(JSON data) {
        StringBuilder s = new StringBuilder();
        if (!toStringInternal(s, data))
            return null;
        return s.toString();
    }

    public String toString() {
        return toString(this);
    }

    public String getString(String key) {
        Object v = get(key);
        if (v != null && v.getClass() == String.class) {
            return (String) v;
        }
        return null;
    }

    public JSON getObject(String key) {
        Object v = get(key);
        if (v != null && v.getClass() == JSON.class) {
            return (JSON) v;
        }
        return null;
    }

}
