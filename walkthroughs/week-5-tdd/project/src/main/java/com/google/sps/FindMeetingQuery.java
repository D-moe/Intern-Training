// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

/**
 * Returns a range of possible meeting times given events of individuals, the
 * length of the meeting, and the people involved.
 */
public final class FindMeetingQuery {
  // Comparator using the Java 8 lambda syntax.
  private final Comparator<Event> ORDER_END =
      (Event a, Event b) -> Long.compare(a.getWhen().end(), b.getWhen().end());

  public Collection<TimeRange> query(Collection<Event> events,
                                     MeetingRequest request) {
    /* Algorithm overview:
    Remove people from the event list who are in the list but not in the
    meeting.
    Sort all of events in descending order by end time.
    Find all events that intersect with the current event and create event
    encompassing them.
    Continue algorithm on next event after events used.
    Grab the start
    time for this current event and traverse through array all events that have
    an end time greater than this start time. The time slot that can not be used
    is then the end time of the current iteration and the minimum start time of
    those other events. Now take the event that after the last event that was
    discovered still within the constraints and repeat from there.

    To handle the optional attendees case we essentially run the algorithm
    twice, thus giving us the same overall complexity and general structure.

    Complexity:
    Such an approach will take O(nlogn) time to sort and O(n) time to iterate
    where n is the number of meetings and m is the number of people in the
    new meeting. The removal will take O(mn) time giving us a complexity of O(mn
    + nlogn).
    */

    // Store the times when we cannot meet, essentially take the inverse of this
    // to determine the times we can meet.
    Collection<String> meetingAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    Collection<TimeRange> toReturn = new ArrayList<>();
    // Change to ArrayList so we can sort.
    ArrayList<Event> eventList = new ArrayList<>(events);
    // Remove an event if none of the members in the meeting attended it.
    eventList.removeIf(event
                       -> !event.getAttendees().stream().anyMatch(
                           attendee
                           -> meetingAttendees.contains(attendee) ||
                                  optionalAttendees.contains(attendee)));
    Collections.sort(eventList, ORDER_END.reversed());
    ArrayList<Event> noOptional = new ArrayList<>(eventList);
    // Remove an event if it is only attended by optional guests.
    noOptional.removeIf(event
                        -> event.getAttendees().stream().allMatch(
                            attendee -> optionalAttendees.contains(attendee)));
    // Change this to get overlapping events.
    ArrayList<TimeRange> timesWithOptional = getUnavailableTimes(eventList);
    ArrayList<TimeRange> timesWithoutOptional = getUnavailableTimes(noOptional);
    toReturn = getInverse(timesWithOptional, request);
    if (toReturn.isEmpty() && !timesWithoutOptional.isEmpty()) {
      toReturn = getInverse(timesWithoutOptional, request);
    }

    return toReturn;
  }

  private int getMinStartTime(int currentStartTime, ListIterator<Event> it) {
    int minStartTime = currentStartTime;
    while (it.hasNext()) {
      Event nextEvent = it.next();
      TimeRange nextEventTime = nextEvent.getWhen();
      // If an event is in the current range.
      if (nextEventTime.end() >= currentStartTime) {
        minStartTime = Math.min(minStartTime, nextEventTime.start());
      } else {
        // In this case it is not possible to expand this event any farther,
        // the iterator already points to the next element we should continue
        // from.
        // Reset the iterator so the last tried value isn't skipped.
        it.previous();
        break;
      }
    }
    return minStartTime;
  }
  // This function assumes that the arguments it is given are in decreasing
  // order. It thus parses them from end to beginning.
  private ArrayList<TimeRange> getInverse(ArrayList<TimeRange> fullTimes,
                                          MeetingRequest request) {
    long meetingDuration = request.getDuration();
    ArrayList<TimeRange> toReturn = new ArrayList<>();
    int priorStartTime = TimeRange.START_OF_DAY;
    // Finally we loop through the time in between these events (the inverse
    // of the set), and if these times are within the length of the meeting
    // we add them to our answer (going from smallest to biggest).

    for (ListIterator<TimeRange> it = fullTimes.listIterator(fullTimes.size());
         it.hasPrevious();) {
      TimeRange currentRange = it.previous();
      int currentStartTime = currentRange.start();
      int currentEndTime = currentRange.end();
      int currentDuration = currentStartTime - priorStartTime;

      if (currentDuration >= meetingDuration) {
        // The meeting is long enough, only take an action in this case.
        toReturn.add(
            TimeRange.fromStartDuration(priorStartTime, currentDuration));
      }
      priorStartTime = currentEndTime;
    }
    // Also need to make sure to include the last range as well.
    int endDuration = TimeRange.END_OF_DAY - priorStartTime;
    if (endDuration >= meetingDuration) {
      toReturn.add(
          TimeRange.fromStartEnd(priorStartTime, TimeRange.END_OF_DAY, true));
    }
    return toReturn;
  }

  private ArrayList<TimeRange> getUnavailableTimes(ArrayList<Event> eventList) {
    ArrayList<TimeRange> toReturn = new ArrayList<>();
    for (ListIterator<Event> it = eventList.listIterator(); it.hasNext();) {
      // Get information for the current event.
      Event currentEvent = it.next();
      TimeRange currentTime = currentEvent.getWhen();
      int currentEndTime = currentTime.end();
      int currentStartTime = currentTime.start();
      // For optional can just compute twice for the same time complexity.
      int minStartTime = getMinStartTime(currentStartTime, it);
      int fullDuration = currentEndTime - minStartTime;
      toReturn.add(TimeRange.fromStartDuration(minStartTime, fullDuration));
    }
    return toReturn;
  }
}