package org.example.project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IntervalGraph {

    // Class to represent an interval with a vertex
    static class Interval {
        int start, end, vertex;

        Interval(int start, int end, int vertex) {
            this.start = start;
            this.end = end;
            this.vertex = vertex;
        }

        @Override
        public String toString() {
            return "Vertex " + vertex + ": [" + start + ", " + end + "]";
        }
    }


    public static List<Interval> generateIntervalGraph(int numVertices) {
        List<Interval> intervals = new ArrayList<>();
        for (int i = 1; i <= numVertices; i++) {
            int start = (int) (Math.random() * 50) + 1;
            int end = start + (int) (Math.random() * 10) + 1;
            intervals.add(new Interval(start, end, i));
        }
        return intervals;
    }


    public static boolean isIntervalGraph(List<Interval> intervals) {
        intervals.sort(Comparator.comparingInt(a -> a.start));

        for (int i = 0; i < intervals.size(); i++) {
            Interval current = intervals.get(i);
            for (int j = i + 1; j < intervals.size(); j++) {
                Interval next = intervals.get(j);
                if (next.start > current.end) {
                    break;
                }
                if (current.vertex == next.vertex) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int calculateChromaticNumber(List<Interval> intervals) {
        intervals.sort(Comparator.comparingInt(a -> a.start));

        int maxOverlap = 0;
        int currentOverlap = 0;

        List<int[]> points = new ArrayList<>();
        for (Interval interval : intervals) {
            points.add(new int[]{interval.start, 1});
            points.add(new int[]{interval.end, -1});
        }

        points.sort((a, b) -> (a[0] == b[0]) ? Integer.compare(a[1], b[1]) : Integer.compare(a[0], b[0]));

        for (int[] point : points) {
            currentOverlap += point[1];
            maxOverlap = Math.max(maxOverlap, currentOverlap);
        }

        return maxOverlap;
    }
}
