import javafx.util.Pair
import org.reactivestreams.Publisher
import ratpack.sse.ServerSentEvents

import java.time.Duration

import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.ratpack
import static ratpack.sse.ServerSentEvents.serverSentEvents
import ratpack.stream.Streams
import static ratpack.stream.Streams.periodically


ratpack {
    handlers {
        get { ctx ->
            Publisher<List<Pair<String, Integer>>> stream = Streams.bindExec(periodically(ctx, Duration.ofSeconds(10), { i ->
                List<Pair<String, Integer>> list = new ArrayList<>()
                for(int x = 0; x < 10; x++) {
                    list.add(new Pair<>("FPA", Math.random()))
                }
                return list
            }))

            ServerSentEvents events = serverSentEvents(stream, { e ->
                e.id(e.getId()).event("FPA").data(toJson(ctx))
            })

            render events
        }
    }
}
