package cn.hachin.t9finder.tools;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * Created by yanghanqing on 15/11/21.
 */
public class Tools {

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 汉字转拼音
     *
     * @param name 汉字
     * @return 拼音数组 全拼和简拼
     */

    public static String[] hz2py(String name) {
        StringBuffer py = new StringBuffer("");
        StringBuffer p = new StringBuffer("");
        //诸葛亮
        if (name == null) {
            return new String[]{"", ""};
        }
        for (char c : name.toCharArray()) {//诸 葛 亮
            String pinyin;
            if (!Pinyin.isChinese(c)) {
                if('0'<=c&&c<='9'){//数字
                    py.append(c);
                    p.append(c);
                }else  if(((c>='a'&&c<='z')||(c>='A'&&c<='Z')))  {
                    c =(char) (c & 223);
                    c=abcTo123(c);
                    py.append(c);
                    p.append(c);
                }

            } else {
                pinyin = Pinyin.toPinyin(c);    //ZHU
                boolean first = true; //判断是否首字母
                for (char ch : pinyin.toCharArray()) {//Z
                    char c1 = abcTo123(ch);//9
                    py.append(c1);
                    if (first) {
                        p.append(c1);
                        first = false;
                    }

                }
            }
        }
        return new String[]{py.toString(), p.toString()};
    }

    public static char abcTo123(char c) {
        char num = ' ';
        if (c == 'A' || c == 'B' || c == 'C')
            num = '2';
        else if (c == 'D' || c == 'E' || c == 'F')
            num = '3';
        else if (c == 'G' || c == 'H' || c == 'I')
            num = '4';
        else if (c == 'J' || c == 'K' || c == 'L')
            num = '5';
        else if (c == 'M' || c == 'N' || c == 'O')
            num = '6';
        else if (c == 'P' || c == 'Q' || c == 'R' || c == 'S')
            num = '7';
        else if (c == 'T' || c == 'U' || c == 'V')
            num = '8';
        else if (c == 'W' || c == 'X' || c == 'Y' || c == 'Z')
            num = '9';

        return num;
    }


}
