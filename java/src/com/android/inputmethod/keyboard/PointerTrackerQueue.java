/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.inputmethod.keyboard;

import java.util.LinkedList;

public class PointerTrackerQueue {
    private LinkedList<PointerTracker> mQueue = new LinkedList<PointerTracker>();

    public void add(PointerTracker tracker) {
        mQueue.add(tracker);
    }

    public int lastIndexOf(PointerTracker tracker) {
        LinkedList<PointerTracker> queue = mQueue;
        for (int index = queue.size() - 1; index >= 0; index--) {
            PointerTracker t = queue.get(index);
            if (t == tracker)
                return index;
        }
        return -1;
    }

    public void releaseAllPointersOlderThan(PointerTracker tracker, long eventTime) {
        LinkedList<PointerTracker> queue = mQueue;
        int oldestPos = 0;
        for (PointerTracker t = queue.get(oldestPos); t != tracker; t = queue.get(oldestPos)) {
            if (t.isModifier()) {
                oldestPos++;
            } else {
                t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                t.setAlreadyProcessed();
                queue.remove(oldestPos);
            }
        }
    }

    public void releaseAllPointersExcept(PointerTracker tracker, long eventTime) {
        for (PointerTracker t : mQueue) {
            if (t == tracker)
                continue;
            t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
            t.setAlreadyProcessed();
        }
        mQueue.clear();
        if (tracker != null)
            mQueue.add(tracker);
    }

    public void remove(PointerTracker tracker) {
        mQueue.remove(tracker);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (PointerTracker tracker : mQueue) {
            if (sb.length() > 1)
                sb.append(" ");
            sb.append(String.format("%d", tracker.mPointerId));
        }
        sb.append("]");
        return sb.toString();
    }
}
