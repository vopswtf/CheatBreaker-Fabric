package cc.vops.cheatbreaker.client.util;

public interface Matrix3x2fStackDebug {
    StackTraceElement cb$getPushOrigin(int index);
    void cb$setPushOrigin(int index, StackTraceElement element);
}