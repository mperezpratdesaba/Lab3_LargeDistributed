package com.github.tukaaa;

import com.github.tukaaa.adapter.SimplifiedTweetAdapter;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import org.apache.spark.api.java.Optional;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import social.bigbone.MastodonClient;
import social.bigbone.MastodonRequest;
import social.bigbone.api.Pageable;
import social.bigbone.api.Range;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.TimelineMethods;

import java.util.concurrent.atomic.AtomicReference;

public class MastodonDStreamReceiver extends Receiver<SimplifiedTweetWithHashtags> {
    private final Integer sleepInterval;
    private final String mastodonServer;

    public MastodonDStreamReceiver(StorageLevel storageLevel, Integer sleepInterval, String mastodonServer) {
        super(storageLevel);
        this.sleepInterval = sleepInterval;
        this.mastodonServer = mastodonServer;
    }

    private Thread receiver;

    @Override
    public void onStart() {
        MastodonClient client = new MastodonClient.Builder(mastodonServer).build();
        AtomicReference<Range> range = new AtomicReference<>();
        receiver = new Thread( () -> {
         while(! isStopped()) {
             try {
                 final Pageable<Status> statuses = getStatuses(client, range.get());
                 store(statuses.getPart().stream().map(SimplifiedTweetAdapter::fromStatus).filter(Optional::isPresent).map(Optional::get).iterator());
                 range.set(statuses.nextRange(20)); //todo is this the immediately next set of tweets?
                 Thread.sleep(sleepInterval);
             } catch (Exception e) {
                 System.err.println("[ERROR] Something happened: " + e.getMessage());
                 stop(e.getMessage(), e);
             }
         }
        });
        receiver.start();
    }

    private Pageable<Status> getStatuses(MastodonClient client, Range range) {
        TimelineMethods timelines = client.timelines();
        MastodonRequest<Pageable<Status>> request;
        if (range == null) {
            request = timelines.getPublicTimeline(TimelineMethods.StatusOrigin.LOCAL_AND_REMOTE);
        } else {
            request = timelines.getPublicTimeline(TimelineMethods.StatusOrigin.LOCAL_AND_REMOTE, range);
        }
        try {
            Pageable<Status> statuses = request.execute();
            return statuses;
        } catch (BigBoneRequestException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
    }

}

