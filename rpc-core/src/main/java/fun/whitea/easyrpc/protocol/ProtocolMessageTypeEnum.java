package fun.whitea.easyrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    public static ProtocolMessageTypeEnum fromKey(int key) {
        for (ProtocolMessageTypeEnum type : ProtocolMessageTypeEnum.values()) {
            if (type.key == key) {
                return type;
            }
        }
        return OTHERS;
    }

}
