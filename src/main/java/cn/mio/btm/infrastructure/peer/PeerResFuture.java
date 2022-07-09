package cn.mio.btm.infrastructure.peer;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PeerResFuture implements Future<PeerResponse> {

    private Consumer<PeerResponse> consumer;

    private Consumer<Throwable> error;

    private PeerResponse response;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final AtomicBoolean isDone = new AtomicBoolean(Boolean.FALSE);

    protected void setResponse(PeerResponse response) {
        this.response = response;
        isDone.set(Boolean.TRUE);
        countDownLatch.countDown();
        if (Objects.nonNull(consumer)) {
            consumer.accept(response);
        }
    }

    protected void setThrowable(Throwable throwable) {
        isDone.set(Boolean.TRUE);
        countDownLatch.countDown();
        if (Objects.nonNull(error)) {
            error.accept(throwable);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone.get();
    }

    @Override
    public PeerResponse get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return this.response;
    }

    @Override
    @SuppressWarnings("all")
    public PeerResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        countDownLatch.await(timeout, unit);
        return this.response;
    }

    public PeerResFuture onComplete(Consumer<PeerResponse> consumer) {
        this.consumer = consumer;
        return this;
    }

    public PeerResFuture onError(Consumer<Throwable> error) {
        this.error = error;
        return this;
    }
}
