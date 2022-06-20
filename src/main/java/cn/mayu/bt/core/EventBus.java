//package cn.mayu.bt.core;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// *
// */
//public class EventBus {
//
//    private static final List<EventConsumer> LIST = new ArrayList<>();
//
//    public static void addConsumer(EventConsumer... consumer) {
//        LIST.addAll(Arrays.asList(consumer));
//    }
//
//    public static void publish(Event event) {
//        LIST.stream().filter(consumer -> consumer.match(event.getClass()))
//                .forEach(consumer -> consumer.receive(event));
//    }
//
//    public interface EventConsumer {
//
//        void receive(Event event);
//
//        boolean match(Class<?> clazz);
//    }
//
//    public interface Event {
//
//    }
//}
