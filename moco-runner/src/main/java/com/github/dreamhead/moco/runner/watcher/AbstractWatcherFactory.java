package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.google.common.base.Function;

import java.io.File;

import static com.github.dreamhead.moco.runner.watcher.Watchers.threadSafe;
import static com.google.common.collect.FluentIterable.from;

public abstract class AbstractWatcherFactory implements FileWatcherFactory {
    protected abstract Watcher doCreate(final FileRunner fileRunner, final File file);

    public Watcher createWatcher(final FileRunner fileRunner, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        if (files.length == 1) {
            return create(fileRunner, files[0]);
        }

        return doCreate(fileRunner, files);
    }

    private Watcher create(final FileRunner fileRunner, final File file) {
        return threadSafe(doCreate(fileRunner, file));
    }

    private Watcher doCreate(final FileRunner fileRunner, File[] files) {
        return new CompositeWatcher(from(files).transform(new Function<File, Watcher>() {
            @Override
            public Watcher apply(final File file) {
                return create(fileRunner, file);
            }
        }));
    }
}
