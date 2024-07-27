package kr.co.morandi.backend.defense_management.infrastructure.adapter.defensemessaging;

import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.defense_management.application.port.out.defensemessaging.DefenseMessagePort;
import kr.co.morandi.backend.defense_management.application.response.codesubmit.CodeResponse;
import kr.co.morandi.backend.defense_management.domain.error.SessionErrorCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class DefenseMessageSseAdapter implements DefenseMessagePort {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 24L;

    /*
    * createConnection 메서드는 defenseSessionId를 받아서 해당 defenseSessionId에 해당하는 SseEmitter를 생성합니다.
    * 이는 시험을 시작할 때 이벤트를 받았을 때 이 메서드를 실행하여 SseEmitter를 생성합니다.
    * */
    @Override
    public void createConnection(Long defenseSessionId) {
        SseEmitter emitter = createSseEmitter(defenseSessionId);
        emitters.put(defenseSessionId, emitter);
    }

    /*
    * getConnection 메서드에서는 defenseSessionId를 받아서 해당 defenseSessionId에 해당하는 SseEmitter를 반환합니다.
    * send를 실패할 경우 SseEmitter를 다시 만드는 재시도 로직을 구현했습니다. (retryConnection)
    * 재귀 호출로 최대 3번까지 재시도 합니다.
    * */
    @Override
    public SseEmitter getConnection(Long defenseSessionId) {
        emitters.putIfAbsent(defenseSessionId, createSseEmitter(defenseSessionId));

        final SseEmitter sseEmitter = emitters.get(defenseSessionId);

        // init 메세지 전송
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("init")
                    .data(defenseSessionId)
            );
        } catch (IOException e) {
            emitters.remove(defenseSessionId);

            // 재시도 로직 추가
            return retryConnection(defenseSessionId, 3);
        }

        return emitters.get(defenseSessionId);
    }
    /*
    * 메세지 보낼 떄 사용하면 됩니다. defenseSessionId를 기준으로 SseEmitter를 찾아서 해당 SseEmitter에 message를 전송합니다.
    * (message에 직렬화하여 전송)
    * 성공적으로 전송되면 true를 반환하고, 실패하면 false를 반환합니다.
    * */
    @Override
    public boolean sendMessage(Long defenseSessionId, String message) {
        SseEmitter emitter = emitters.get(defenseSessionId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message)
                );
                return true;
            } catch (Exception e) {
                emitters.remove(defenseSessionId);
            }
        }
        return false;
    }

    // 임시로 ping 메세지 전송
    @Scheduled(fixedRate = 5000)
    public void checkConnection() {
        emitters.forEach((k, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data("ping")
                        .reconnectTime(3000L)
                );
            }
            catch (Exception e) {
                emitters.remove(k);
            }

        });
    }
    private SseEmitter createSseEmitter(Long defenseSessionId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onTimeout(() -> {
            emitters.remove(defenseSessionId);
        });


        emitter.onCompletion(() -> {
            emitters.remove(defenseSessionId);
            emitter.complete();
        });

        emitter.onError((e) -> {
            emitters.remove(defenseSessionId);
        });

        return emitter;
    }
    /**
     * 재시도 로직을 구현했습니다.
     * 재귀 호출로 최대 3번까지 재시도
     */
    private SseEmitter retryConnection(Long defenseSessionId, int count) {
        if(count == 0) {
            throw new MorandiException(SessionErrorCode.SESSION_CONNECTION_FAIL);
        }
        emitters.putIfAbsent(defenseSessionId, createSseEmitter(defenseSessionId));

        final SseEmitter sseEmitter = emitters.get(defenseSessionId);

        try {
            sseEmitter.send(SseEmitter.event()
                    .name("init")
                    .data(defenseSessionId)
            );
        } catch (IOException e) {
            emitters.remove(defenseSessionId);

            return retryConnection(defenseSessionId, count - 1);
        }
        return sseEmitter;
    }

}
