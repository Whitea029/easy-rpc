package fun.whitea.porxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        System.out.println("mock invoke " + returnType);
        return getDefaultObject(returnType);
    }

    private Object getDefaultObject(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == byte.class) {
                return (byte) 0;
            } else if (type == char.class) {
                return '\u0000';
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0f;
            } else if (type == double.class) {
                return 0.0;
            }
        } else if (type == void.class) {
            return null;
        }
        return null;
    }

}
