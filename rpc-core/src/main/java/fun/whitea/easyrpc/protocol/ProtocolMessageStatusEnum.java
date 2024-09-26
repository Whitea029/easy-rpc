package fun.whitea.easyrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolMessageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50),

    ;
    private final String text;
    private final int val;

    public static ProtocolMessageStatusEnum of(int val) {
        for (ProtocolMessageStatusEnum e : ProtocolMessageStatusEnum.values()) {
            if (e.val == val) {
                return e;
            }
        }
        return null;
    }

}
