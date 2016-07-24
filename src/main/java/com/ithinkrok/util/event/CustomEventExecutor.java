package com.ithinkrok.util.event;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by paul on 01/01/16.
 */
public class CustomEventExecutor {

    private static final Map<Class<? extends CustomListener>, ListenerHandler> listenerHandlerMap = new HashMap<>();

    public static void executeEvent(CustomEvent event, CustomListener... listeners) {
        executeListeners(event, getMethodExecutorMap(event, listeners));
    }

    private static void executeListeners(CustomEvent event, Map<MethodExecutor, Collection<CustomListener>> map) {
        for (Map.Entry<MethodExecutor, Collection<CustomListener>> entry : map.entrySet()) {
            for(CustomListener listener : entry.getValue()) {
                try {
                    entry.getKey().execute(listener, event);
                } catch (EventException e) {
                    System.out.println("Failed while calling event listener: " + listener.getClass());
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private static Map<MethodExecutor, Collection<CustomListener>> getMethodExecutorMap(CustomEvent event,
                                                                                        CustomListener... listeners) {
        SortedMap<MethodExecutor, Collection<CustomListener>> map = new TreeMap<>();

        for (CustomListener listener : listeners) {
            if (listener == null) continue;
            for (MethodExecutor methodExecutor : getMethodExecutors(listener, event)) {

                Collection<CustomListener> group = map.get(methodExecutor);
                if(group == null) {
                    group = new ArrayList<>();
                    map.put(methodExecutor, group);
                }

                group.add(listener);
            }
        }

        return map;
    }

    private static Iterable<MethodExecutor> getMethodExecutors(CustomListener listener, CustomEvent event) {
        ListenerHandler handler = listenerHandlerMap.get(listener.getClass());

        if (handler == null) {
            handler = new ListenerHandler(listener.getClass());
            listenerHandlerMap.put(listener.getClass(), handler);
        }

        return handler.getMethodExecutors(event);
    }

    @SafeVarargs
    public static void executeEvent(CustomEvent event, Collection<CustomListener>... listeners) {
        executeListeners(event, getMethodExecutorMap(event, listeners));
    }

    @SafeVarargs
    private static Map<MethodExecutor, Collection<CustomListener>> getMethodExecutorMap(CustomEvent event,
                                                                            Collection<CustomListener>... listeners) {
        SortedMap<MethodExecutor, Collection<CustomListener>> map = new TreeMap<>();

        for (Collection<CustomListener> listenerGroup : listeners) {
            addToMethodExecutorMap(event, listenerGroup, map);
        }

        return map;
    }

    @SuppressWarnings("Duplicates")
    private static void addToMethodExecutorMap(CustomEvent event, Iterable<CustomListener> listenerGroup,
                                               SortedMap<MethodExecutor, Collection<CustomListener>> map) {
        for (CustomListener listener : listenerGroup) {
            if (listener == null) continue;
            for (MethodExecutor methodExecutor : getMethodExecutors(listener, event)) {

                Collection<CustomListener> group = map.get(methodExecutor);
                if(group == null) {
                    group = new ArrayList<>();
                    map.put(methodExecutor, group);
                }

                group.add(listener);
            }
        }
    }

    public static void executeEvent(CustomEvent event, Iterable<Collection<CustomListener>> listeners) {
        executeListeners(event, getMethodExecutorMap(event, listeners));
    }

    private static Map<MethodExecutor, Collection<CustomListener>> getMethodExecutorMap(CustomEvent event,
                                                                            Iterable<Collection<CustomListener>> listeners) {
        SortedMap<MethodExecutor, Collection<CustomListener>> map = new TreeMap<>();

        for (Collection<CustomListener> listenerGroup : listeners) {
            addToMethodExecutorMap(event, listenerGroup, map);
        }

        return map;
    }

    private static class ListenerHandler {
        private final Class<? extends CustomListener> listenerClass;

        private final Map<Class<? extends CustomEvent>, List<MethodExecutor>> eventMethodsMap = new HashMap<>();

        public ListenerHandler(Class<? extends CustomListener> listenerClass) {
            this.listenerClass = listenerClass;
        }

        public Collection<MethodExecutor> getMethodExecutors(CustomEvent event) {
            List<MethodExecutor> eventMethods = eventMethodsMap.get(event.getClass());

            if (eventMethods == null) {
                eventMethods = new ArrayList<>();

                for (Method method : listenerClass.getMethods()) {
                    if (method.getParameterCount() != 1) continue;
                    if (!method.isAnnotationPresent(CustomEventHandler.class)) continue;
                    if (!method.getParameterTypes()[0].isInstance(event)) continue;

                    //Allows the usage of private classes as listeners
                    method.setAccessible(true);

                    eventMethods.add(new MethodExecutor(method,
                            method.getAnnotation(CustomEventHandler.class).ignoreCancelled()));
                }

                Collections.sort(eventMethods);

                eventMethodsMap.put(event.getClass(), eventMethods);
            }

            return eventMethods;
        }
    }

    private static class MethodExecutor implements Comparable<MethodExecutor> {
        private final Method method;
        private final boolean ignoreCancelled;

        public MethodExecutor(Method method, boolean ignoreCancelled) {
            this.method = method;
            this.ignoreCancelled = ignoreCancelled;
        }

        public void execute(CustomListener listener, CustomEvent event) throws EventException {
            if (ignoreCancelled && (event instanceof Cancellable) && ((Cancellable) event).isCancelled()) return;

            try {
                method.invoke(listener, event);
            } catch (Exception e) {
                throw new EventException("Failed while calling event method: " + method.getName(), e);
            }
        }

        @Override
        public int compareTo(MethodExecutor o) {
            int priorityCompare = Integer.compare(method.getAnnotation(CustomEventHandler.class).priority(),
                    o.method.getAnnotation(CustomEventHandler.class).priority());

            if (priorityCompare != 0) return priorityCompare;

            return method.toString().compareTo(o.method.toString());
        }

        @Override
        public int hashCode() {
            int result = method.hashCode();
            result = 31 * result + (ignoreCancelled ? 1 : 0);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodExecutor that = (MethodExecutor) o;

            if (ignoreCancelled != that.ignoreCancelled) return false;
            return method.equals(that.method);

        }
    }
}
