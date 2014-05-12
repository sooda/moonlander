package rocketpower.library;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class Track {
    private String name;
    // ArrayList stored in sorted order by
    // key trackKey.row.
    // Not the most efficient data structure
    // for this use, but suffices for now.
    private ArrayList<TrackKey> keys;

    public Track(String name) {
        this.name = name;
        this.keys = new ArrayList<TrackKey>();
    }

    public List<TrackKey> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    /*
     * Returns index for a key that should be used as
     * first interpolation key when calculating value
     * at the given row.
     *
     * Effectively the returned index is either index
     * of TrackKey that is set for the row, or if no exact
     * match is found, it's index of TrackKey that
     * is right before given row (key.row < row) in the
     * keys-array. 
     *
     * @returns -1 if key not found, otherwise usable key index
     */
    private int getKeyIndex(int row) {
        // TODO: better way to do this search
        int index = Collections.binarySearch(this.keys, new TrackKey(row, 0.f, TrackKey.KeyType.STEP));

        // Positive index is usable as it is to index this.keys.
        // -1 means that index 0 should be used as insertion point ->
        // means that no usable key is found that passes rule
        // key.row < row. It's therefore an error value.
        // If index < -1, a valid key index can be calculated
        // and returned.
        if (index >= 0 || index == -1)
            return index;
        else
            // Return closest match
            return -index-2;
    }

    /*
     * Find key for the given row.
     *
     * If TrackKey object isn't found for the row,
     * the key before the given row is returned.
     *
     * @returns TrackKey if found, otherwise null.
     */
    public TrackKey getKey(int row) {
        int index = getKeyIndex(row);

        // Exact hit 
        if (index >= 0)
            return this.keys.get(index);
        // No usable key
        else 
            return null;
    }

    public void insertKey(TrackKey key) {
        this.keys.add(key);
        Collections.sort(keys);
    }

    /*
     * For testing
     */
    public void printKeys() {
        for (TrackKey key: keys)
            System.out.println(key);
    }

    /*
     * Returns value of the track for the given
     * (fractional) row.
     *
     * @param   row  
     * @return  interpolated value for the row
     */
    public double getValue(double row) {
        // No keys -> return 0
        if (keys.size() == 0)
            return 0.0;

        int irow = (int) row;
        int idx = getKeyIndex(irow);

        // No key found below the given row
        // -> return 0 (asked row < keys[0].row)
        if (idx == -1)
            return 0.0;
        // Last key -> return value without interpolation
        else if (idx == keys.size() - 1)
            return keys.get(idx).getValue();

        // Somewhere in between two keys: return interpolated value
        return TrackKey.interpolate(keys.get(idx), keys.get(idx+1), row);
    }

    public String toString() {
        return String.format("Track(keys=%d)", keys.size());
    }
}
