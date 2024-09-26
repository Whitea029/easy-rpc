package fun.whitea.easyrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    HESSIAN(2, "hessian"),
    KRYO(3, "kryo"),
    ;


    private final int key;
    private final String val;

    public static List<String> getVals() {
        return Arrays.stream(values()).map(item -> item.val).toList();
    }

    public static ProtocolMessageSerializerEnum fromKey(int key) {
        for (ProtocolMessageSerializerEnum item : ProtocolMessageSerializerEnum.values()) {
            if (item.key == key) {
                return item;
            }
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum fromVal(String val) {
        for (ProtocolMessageSerializerEnum item : ProtocolMessageSerializerEnum.values()) {
            if (item.val.equals(val)) {
                return item;
            }
        }
        return null;
    }

}
