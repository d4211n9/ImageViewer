package dk.easv;

import javafx.concurrent.Task;

import java.util.List;

public class SlideShowTask<T> extends Task<T> {
    private List<T> list;
    private int delaySeconds;
    private int currentListItemIndex;

    public SlideShowTask(List<T> list, int startingIndex, int delaySeconds) {
        this.list = list;
        this.delaySeconds = delaySeconds;
        currentListItemIndex = startingIndex;
    }

    @Override
    protected T call() throws Exception {

        while(!isCancelled()) {
            T nextListItem = list.get(nextListItemIndex());

            updateValue(nextListItem);

            Thread.sleep(delaySeconds * 1000L);
        }

        return null;
    }

    private int nextListItemIndex() {
        if (++currentListItemIndex >= list.size()) currentListItemIndex = 0;

        return currentListItemIndex;
    }
}
