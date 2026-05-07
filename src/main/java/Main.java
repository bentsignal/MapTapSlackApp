import com.slack.api.bolt.App;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.event.MessageEvent;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import listeners.Listeners;
import maptap.Tapbot;

public class Main {

    public static void main(String[] args) throws Exception {
        // App expects an env variable: SLACK_BOT_TOKEN
        var app = new App();
        Tapbot tapbot = new Tapbot();
        Listeners.register(app);
        final String convoID = System.getenv("GAMECHANNEL_ID");
        ConversationsHistoryRequest historyReq = ConversationsHistoryRequest.builder()
                .channel(convoID)
                .limit(999)
                .build();
        ConversationsHistoryResponse resp = app.client().conversationsHistory(historyReq);
        if (!resp.isOk()) {
            System.out.println("ERROR: failed to get channel - " + resp.getError());
            return;
        }
        resp.getMessages().reversed().forEach(m -> {
            String body = m.getText();
            String name = m.getUser();
            if (body != null && body.contains("maptap.gg") && body.contains("Final score:")) {
                tapbot.addMsg(body, name);
            }
        });
        app.event(MessageEvent.class, (payload, ctx) -> {
            MessageEvent event = payload.getEvent();
            String body = event.getText();
            String name = event.getUser();

            if (body != null && body.contains("maptap.gg") && body.contains("Final score:")) {
                tapbot.addMsg(body, name);
            }
            return ctx.ack();
        });
        app.command("/eod", (req, ctx) -> {
            ChatPostMessageResponse response = ctx.client()
                    .chatPostMessage(
                            r -> r.channel(req.getPayload().getChannelId()).text(tapbot.endOfDay()));
            if (!response.isOk()) {
                ctx.logger.error("Error posting message: " + response.getError());
            }
            return ctx.ack();
        });
        app.command("/stats", (req, ctx) -> {
            ChatPostMessageResponse response = ctx.client()
                    .chatPostMessage(
                            r -> r.channel(req.getPayload().getChannelId()).text(tapbot.stats()));
            if (!response.isOk()) {
                ctx.logger.error("Error posting message: " + response.getError());
            }
            return ctx.ack();
        });
        app.command("/averages", (req, ctx) -> {
            ChatPostMessageResponse response = ctx.client()
                    .chatPostMessage(
                            r -> r.channel(req.getPayload().getChannelId()).text(tapbot.averages()));
            if (!response.isOk()) {
                ctx.logger.error("Error posting message: " + response.getError());
            }
            return ctx.ack();
        });
        app.command("/playerstats", (req, ctx) -> {
            String text = req.getPayload().getText();
            ctx.logger.info("Getting logs for text: " + text);
            int pipeIndex = text.indexOf('|');
            if (pipeIndex == -1) {
                ctx.logger.error("Invalid command format. Expected: /playerstats <@username>");
                return ctx.ack();
            }
            String name = text.substring(2, pipeIndex);
            ctx.logger.info("Getting logs for user: " + name);
            ChatPostMessageResponse response = ctx.client()
                    .chatPostMessage(
                            r -> r.channel(req.getPayload().getChannelId()).text(tapbot.playerStats(name)));
            if (!response.isOk()) {
                ctx.logger.error("Error posting message: " + response.getError());
            }
            return ctx.ack();
        });
        app.command("/worst-locations", (req, ctx) -> {
            ChatPostMessageResponse response = ctx.client()
                    .chatPostMessage(
                            r -> r.channel(req.getPayload().getChannelId()).text(tapbot.worstLocations()));
            if (!response.isOk()) {
                ctx.logger.error("Error posting message: " + response.getError());
            }
            return ctx.ack();
        });
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "eod-scheduler");
            t.setDaemon(true);
            return t;
        });
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextEod = now.toLocalDate().atTime(23, 59).atZone(ZoneId.of("America/New_York"));
        if (now.isAfter(nextEod)) {
            nextEod = nextEod.plusDays(1);
        }
        long initialDelay = Duration.between(now, nextEod).getSeconds();
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        String message = tapbot.endOfDay();
                        app.client().chatPostMessage(r -> r.channel(convoID).text(message));
                    } catch (Exception e) {
                        System.err.println("Failed to post end-of-day message: " + e.getMessage());
                    }
                },
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);

        // SocketModeApp expects an env variable: SLACK_APP_TOKEN
        new SocketModeApp(app).start();
    }
}
