package com.ithinkrok.msm.common.util.io;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by paul on 05/02/16.
 */
public class DirectoryWatcher {

    private final List<PathListenerGroup> targets = new CopyOnWriteArrayList<>();

    private final WatchService watcher;

    private final WatcherThread thread;

    public DirectoryWatcher() throws IOException {
        this(FileSystems.getDefault());
    }

    public DirectoryWatcher(FileSystem fileSystem) throws IOException {
        this(fileSystem.newWatchService());
    }

    public DirectoryWatcher(WatchService watcher) {
        this.watcher = watcher;

        thread = new WatcherThread();
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        //Flag the thread for stopping

        thread.stop = true;
    }

    public void registerListener(Path path, DirectoryListener listener) {
        if(!Files.isDirectory(path)) throw new IllegalArgumentException("Provided path is not a directory");

        for(PathListenerGroup pathListenerGroup : targets) {
            if(!path.equals(pathListenerGroup.path)) continue;

            pathListenerGroup.add(listener);
            return;
        }

        try {
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            System.err.println("Failed to register path with directory watcher");
            e.printStackTrace();
        }

        targets.add(new PathListenerGroup(path, listener));
    }

    public void unregisterListener(DirectoryListener listener) {
        for(PathListenerGroup pathListenerGroup : targets) {
            pathListenerGroup.remove(listener);
        }
    }

    private static class PathListenerGroup {

        Path path;
        List<DirectoryListener> listeners = new CopyOnWriteArrayList<>();

        private PathListenerGroup(Path path, DirectoryListener listener) {
            this.path = path;
            this.listeners.add(listener);
        }

        private void add(DirectoryListener listener) {
            listeners.add(listener);
        }

        private void remove(DirectoryListener listener) {
            listeners.remove(listener);
        }
    }

    private class WatcherThread extends Thread {

        private boolean stop;

        private WatcherThread() {
            super("Directory change watcher");
        }

        @Override
        public void run() {
            while (!stop) {

                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    return;
                }

                Path dir = (Path) key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                    Path file = watchEvent.context();

                    Path changed = dir.resolve(file);

                    for (PathListenerGroup pathListenerGroup : targets) {
                        //Made sure the listeners in this group are listening for this directory
                        if (!changed.startsWith(pathListenerGroup.path)) continue;

                        for (DirectoryListener listener : pathListenerGroup.listeners) {
                            listener.fileChanged(changed, kind);
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) break;
            }
        }
    }
}
