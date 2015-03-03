package me.wener.showea.collect.qq;

public class QQUtils
{
    public static String getGTK()
    {
        return null;
    }

    public static String getGTK(String skey)
    {
        int hash = 5381;
        for (int i = 0, len = skey.length(); i < len; ++i)
        {
            hash += (hash << 5) + (int) (char) skey.charAt(i);
        }
        return (hash & 0x7fffffff) + "";
    }
}
